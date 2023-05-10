package hungteen.opentd.common.entity;

import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.registry.EntityHelper;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.common.codec.BulletSetting;
import hungteen.opentd.common.codec.ParticleSetting;
import hungteen.opentd.common.codec.ShootGoalSetting;
import hungteen.opentd.common.event.events.BulletHitEvent;
import hungteen.opentd.common.impl.tower.PVZPlantComponent;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-20 14:22
 **/
public class BulletEntity extends Projectile implements IEntityAdditionalSpawnData, IAnimatable {

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private BulletSetting setting;
    private IntOpenHashSet hitSet = new IntOpenHashSet();
    protected Optional<Entity> lockTarget = Optional.empty();
    protected Optional<BlockPos> lockPos = Optional.empty();
    private boolean isParabola = false;
    private double pultHeight = 10;
    private int hitCount = 0;
    private boolean canExist = true;


    public BulletEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {

    }

    public void summonBy(Entity owner, ShootGoalSetting.ShootSetting shootSetting) {
        this.setOwner(owner);
        this.setParabola(shootSetting.isParabola());
        this.pultHeight = shootSetting.pultHeight();
        this.setting = shootSetting.bulletSetting();
        final double d0 = this.getDeltaMovement().horizontalDistance();
        this.setYRot((float)(Mth.atan2(this.getDeltaMovement().x, this.getDeltaMovement().z) * (double)(180F / (float)Math.PI)));
        this.setXRot((float)(Mth.atan2(this.getDeltaMovement().y, d0) * (double)(180F / (float)Math.PI)));
    }

    public void tick() {
        super.tick();
        this.noPhysics = this.ignoreBlock();
        if (this.bulletSetting() == null) {
            if (this.tickCount > 5) {
                this.discard();
            }
            return;
        }
        // reach alive time limit.
        if (!level.isClientSide) {
            if (this.tickCount >= this.getMaxLiveTick()) {
                this.discard();
            }
        }
        // lock to target.
        if (this.bulletSetting().lockToTarget() && this.lockTarget.isPresent() && EntityHelper.isEntityValid(lockTarget.get())) {
            final Entity target = this.lockTarget.get();
            final Vec3 speed = this.getDeltaMovement();
            if (this.isParabola) {
                final double g = this.getGravity();
                final double t1 = speed.y / g;
                final double height = speed.y * speed.y / 2 / g;
                final double downHeight = this.getY() + height - target.getY() - target.getBbHeight();
                if (downHeight < 0) {
                    return;
                }
                final double t2 = Math.sqrt(2 * downHeight / g);
                final double dx = target.getX() + target.getDeltaMovement().x() * (t1 + t2) - this.getX();
                final double dz = target.getZ() + target.getDeltaMovement().z() * (t1 + t2) - this.getZ();
                final double dxz = Math.sqrt(dx * dx + dz * dz);
                final double vxz = dxz / (t1 + t2);
                if (dxz == 0) {
                    this.setDeltaMovement(0, speed.y, 0);
                } else {
                    this.setDeltaMovement(vxz * dx / dxz, speed.y, vxz * dz / dxz);
                }
            } else{
                final Vec3 direction = target.getEyePosition().subtract(this.position()).normalize().scale(this.getSpeed());
                final double scale = 0.2;
                this.setDeltaMovement(speed.add((direction.x() - speed.x()) * scale, (direction.y() - speed.y) * scale, (direction.z() - speed.z()) * scale));
            }
        }
        // on hit.
        HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
        boolean flag = false;
        if (hitresult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult) hitresult).getBlockPos();
            BlockState blockstate = this.level.getBlockState(blockpos);
            if (blockstate.is(Blocks.NETHER_PORTAL)) {
                this.handleInsidePortal(blockpos);
                flag = true;
            } else if (blockstate.is(Blocks.END_GATEWAY)) {
                BlockEntity blockentity = this.level.getBlockEntity(blockpos);
                if (blockentity instanceof TheEndGatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
                    TheEndGatewayBlockEntity.teleportEntity(this.level, blockpos, blockstate, this, (TheEndGatewayBlockEntity) blockentity);
                }

                flag = true;
            }
        }

        if (hitresult.getType() != HitResult.Type.MISS && !flag && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
        }
        //move.
        this.checkInsideBlocks();
        Vec3 vec3 = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = vec3.horizontalDistance();
            this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
            this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
        double d2 = this.getX() + vec3.x;
        double d0 = this.getY() + vec3.y;
        double d1 = this.getZ() + vec3.z;
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);
        float f = this.bulletSetting().slowDown();
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                this.level.addParticle(ParticleTypes.BUBBLE, d2 - vec3.x * 0.25D, d0 - vec3.y * 0.25D, d1 - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
            }

            f = 0.8F;
        }

        this.setDeltaMovement(vec3.scale(f));

        if (!this.isNoGravity()) {
            Vec3 vec31 = this.getDeltaMovement();
            this.setDeltaMovement(vec31.x, vec31.y - (double) this.getGravity(), vec31.z);
        }

        if (this.getTrailParticle() != null) {
            this.getTrailParticle().spawn(this.level, new Vec3(d2, d0 + 0.5D, d1), this.random);
        }

        this.setPos(d2, d0, d1);
    }

    protected void onHit(HitResult result) {
        HitResult.Type type = result.getType();
        if (type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult) result);
        } else if (type == HitResult.Type.BLOCK) {
            this.onHitBlock((BlockHitResult) result);
        }

        if (type != HitResult.Type.MISS) {
            this.gameEvent(GameEvent.PROJECTILE_LAND, this.getOwner());
        }
        //handle hit and remove.
        if (!this.level.isClientSide && !this.canExist) {
            this.level.broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }

    }

    protected void onHitEntity(EntityHitResult result) {
        if (this.shouldHit(result.getEntity()) && this.level instanceof ServerLevel) {
            this.getEffect().ifPresent(l -> l.effectTo((ServerLevel) this.level, this, result.getEntity()));
            hitSet.add(result.getEntity().getId());
            if (++this.hitCount >= this.getMaxHitCount()) {
                this.canExist = false;
            }
            MinecraftForge.EVENT_BUS.post(new BulletHitEvent(this, result));
        }
    }

    protected void onHitBlock(BlockHitResult result) {
        if (!this.ignoreBlock() && this.level instanceof ServerLevel) {
            BlockState blockstate = this.level.getBlockState(result.getBlockPos());
            blockstate.onProjectileHit(this.level, blockstate, result, this);
            this.getEffect().ifPresent(l -> l.effectTo((ServerLevel) this.level, this, result.getBlockPos()));
            this.canExist = false;
            MinecraftForge.EVENT_BUS.post(new BulletHitEvent(this, result));
        }
    }

    protected boolean shouldHit(Entity target) {
        return (this.bulletSetting() == null || (level instanceof ServerLevel && this.bulletSetting().targetFilter().match((ServerLevel) level, this, target))) && !this.hitSet.contains(target.getId());
    }

    /**
     * handle server to client event.
     */
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {//die event.
            if (this.getHitParticle() != null) {
                this.getHitParticle().spawn(this.level, this.position(), this.random);
            }
        }
    }

    @Nullable
    protected ParticleSetting getHitParticle() {
        if(bulletSetting() != null && bulletSetting().hitParticle().isPresent()) {
            return bulletSetting().hitParticle().get();
        };
        return null;
    }

    @Nullable
    protected ParticleSetting getTrailParticle() {
        if(bulletSetting() != null && bulletSetting().trailParticle().isPresent()) {
            return bulletSetting().trailParticle().get();
        };
        return null;
    }


    /**
     * attack bullet such as pea or spore
     */
    public void shootToTarget(Mob owner, ShootGoalSetting.ShootSetting shootSetting, Entity target, double dx, double dy, double dz) {
        this.lockTarget = Optional.ofNullable(target);
        if (shootSetting.isParabola()) {
            this.pult(owner, shootSetting, target);
        } else {
            if (shootSetting.verticalAngleLimit() < 90) {
                final double dxz = Math.sqrt(dx * dx + dz * dz);
                final double tan = Math.tan(Math.toRadians(shootSetting.verticalAngleLimit()));
                final double limitY = tan * dxz;
                dy = Mth.clamp(dy, -limitY, limitY);//fix dy by angle
            }
            final Vec3 speed = MathHelper.rotate(new Vec3(dx, dy, dz), shootSetting.horizontalAngleOffset(), 0);
            this.setDeltaMovement(speed.normalize().scale(shootSetting.bulletSetting().bulletSpeed()));
        }
        this.summonBy(owner, shootSetting);
    }

    public void shootTo(Mob owner, ShootGoalSetting.ShootSetting shootSetting, Vec3 vec) {
        if (shootSetting.isParabola()) {
            this.pult(owner, shootSetting, owner);
        } else {
            final Vec3 speed = MathHelper.rotate(vec, shootSetting.horizontalAngleOffset(), 0);
            this.setDeltaMovement(speed.normalize().scale(shootSetting.bulletSetting().bulletSpeed()));
        }
        this.summonBy(owner, shootSetting);
    }

    public void pult(Mob owner, ShootGoalSetting.ShootSetting shootSetting, @Nonnull Entity target) {
        final double g = shootSetting.bulletSetting().gravity();
        final double h = shootSetting.pultHeight();
        final double t1 = Math.sqrt(2 * h / g);//go up time.
        double t2 = 0;
        if (this.getY() + h - target.getY() - target.getBbHeight() >= 0) {//random pult.
            t2 = Math.sqrt(2 * (this.getY() + h - target.getY() - target.getBbHeight()) / g);//go down time.
        }
        final double dx = target.getX() + target.getDeltaMovement().x() * (t1 + t2) - this.getX();
        final double dz = target.getZ() + target.getDeltaMovement().z() * (t1 + t2) - this.getZ();
        setPultSpeed(g, t1, t2, dx, dz);
    }

    private void setPultSpeed(double g, double t1, double t2, double dx, double dz) {
        final double dxz = Math.sqrt(dx * dx + dz * dz);
        final double vxz = dxz / (t1 + t2);
        final double vy = g * t1;
        if (dxz == 0) {
            this.setDeltaMovement(0, vy, 0);
        } else {
            this.setDeltaMovement(vxz * dx / dxz, vy, vxz * dz / dxz);
        }
    }

    public void setParabola(boolean isParabola) {
        this.isParabola = isParabola;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double dis) {
        double d0 = this.getBoundingBox().getSize() * 4.0D;
        if (Double.isNaN(d0)) {
            d0 = 4.0D;
        }

        d0 *= 64.0D;
        return dis < d0 * d0;
    }

    public Entity getOwnerOrSelf() {
        return this.getOwner() == null ? this : this.getOwner();
    }

    /**
     * how long can bullet exist.
     */
    public int getMaxLiveTick() {
        return this.bulletSetting() == null ? 0 : this.bulletSetting().maxExistTick();
    }

    public float getGravity() {
        return this.bulletSetting() == null ? 0 : this.bulletSetting().gravity();
    }

    public float getSpeed() {
        return this.bulletSetting() == null ? 0.1F : this.bulletSetting().bulletSpeed();
    }

    public int getMaxHitCount() {
        return this.bulletSetting() == null ? 1 : this.bulletSetting().maxHitCount();
    }

    public int getHitCount(){
        return this.hitCount;
    }

    public boolean ignoreBlock() {
        return this.bulletSetting() != null && this.bulletSetting().ignoreBlock();
    }

    protected Optional<IEffectComponent> getEffect() {
        return this.bulletSetting() == null ? Optional.empty() : Optional.ofNullable(this.bulletSetting().effect());
    }

    @Nullable
    public BulletSetting bulletSetting() {
        return this.setting;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.isParabola);
        buffer.writeDouble(this.pultHeight);
        if (this.setting != null) {
            BulletSetting.CODEC.encodeStart(NbtOps.INSTANCE, this.setting)
                    .resultOrPartial(msg -> OpenTD.log().error(msg + " [Bullet] "))
                    .ifPresent(tag -> buffer.writeNbt((CompoundTag) tag));
        } else {
            buffer.writeNbt(new CompoundTag());
        }
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        this.isParabola = additionalData.readBoolean();
        this.pultHeight = additionalData.readDouble();
        BulletSetting.CODEC.parse(NbtOps.INSTANCE, additionalData.readNbt())
                .resultOrPartial(msg -> OpenTD.log().error(msg + " [Bullet] "))
                .ifPresent(settings -> this.setting = settings);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("TickCount", this.tickCount);
        compound.putInt("HitCount", this.hitCount);
        this.lockTarget.ifPresent(entity -> compound.putInt("LockTargetEntity", entity.getId()));
        this.lockPos.ifPresent(pos -> compound.putLong("LockTargetPos", pos.asLong()));
        compound.putDouble("PultHeight", this.pultHeight);
        if (this.setting != null) {
            BulletSetting.CODEC.encodeStart(NbtOps.INSTANCE, this.setting)
                    .resultOrPartial(msg -> OpenTD.log().error(msg + " [Bullet] "))
                    .ifPresent(tag -> compound.put("BulletSettings", tag));
        }

    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("TickCount")) {
            this.tickCount = compound.getInt("TickCount");
        }
        if (compound.contains("HitCount")) {
            this.hitCount = compound.getInt("HitCount");
        }
        if (compound.contains("LockTargetEntity")) {
            this.lockTarget = Optional.ofNullable(level.getEntity(compound.getInt("LockTargetEntity")));
        }
        if (compound.contains("LockTargetPos")) {
            this.lockPos = Optional.of(BlockPos.of(compound.getLong("LockTargetPos")));
        }
        if (compound.contains("PultHeight")) {
            this.pultHeight = compound.getDouble("PultHeight");
        }
        if (compound.contains("BulletSettings")) {
            BulletSetting.CODEC.parse(NbtOps.INSTANCE, compound.get("BulletSettings"))
                    .resultOrPartial(msg -> OpenTD.log().error(msg + " [Bullet] "))
                    .ifPresent(settings -> this.setting = settings);
        }
    }

    public Optional<UUID> getOwnerUUID() {
        if (this.getOwner() != null) {
            return Optional.of(this.getOwner().getUUID());
        }
        return Optional.empty();
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController<>(
                this,
                "controller",
                0,
                this::predicateAnimation
        ));
    }

    protected PlayState predicateAnimation(AnimationEvent<?> event) {
        final AnimationBuilder builder = new AnimationBuilder();
        builder.addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP);
        event.getController().setAnimation(builder);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

}

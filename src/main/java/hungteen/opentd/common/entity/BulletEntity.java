package hungteen.opentd.common.entity;

import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.impl.tower.PVZPlantComponent;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-20 14:22
 **/
public class BulletEntity extends Projectile implements IEntityAdditionalSpawnData, IAnimatable {

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private PVZPlantComponent.BulletSettings settings;
    private IntOpenHashSet hitSet = new IntOpenHashSet();
    private int hitCount = 0;
    private boolean canExist = true;


    public BulletEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {

    }

    public void summonBy(Entity owner, PVZPlantComponent.BulletSettings bulletSettings) {
        this.setOwner(owner);
        this.settings = bulletSettings;
    }

    public void tick() {
        super.tick();
        this.noPhysics = this.ignoreBlock();
        if (this.getSettings() == null) {
            if (this.tickCount > 5) {
                this.discard();
            }
            return;
        }
        //reach alive time limit.
        if (!level.isClientSide) {
            if (this.tickCount >= this.getMaxLiveTick()) {
                this.discard();
            }
        }
        //on hit.
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
        double d2 = this.getX() + vec3.x;
        double d0 = this.getY() + vec3.y;
        double d1 = this.getZ() + vec3.z;
        ProjectileUtil.rotateTowardsMovement(this, 0.2F);
        float f = this.getSettings().slowDown();
        ;
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                float f1 = 0.25F;
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
            this.level.addParticle(this.getTrailParticle(), d2, d0 + 0.5D, d1, 0.0D, 0.0D, 0.0D);
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
        if (this.shouldHit(result.getEntity())) {
            this.getEffects().forEach(e -> e.effectTo(this, result.getEntity()));
            hitSet.add(result.getEntity().getId());
            if(++ this.hitCount > this.getMaxHitCount()) {
                this.canExist = false;
            }
        }
    }

    protected void onHitBlock(BlockHitResult result) {
        if(! this.ignoreBlock()){
            BlockState blockstate = this.level.getBlockState(result.getBlockPos());
            blockstate.onProjectileHit(this.level, blockstate, result, this);
            this.getEffects().forEach(e -> e.effectTo(this, result.getBlockPos()));
            this.canExist = false;
        }
    }

    protected boolean shouldHit(Entity target) {
        return (this.getSettings() == null || this.getSettings().targetFilter().match(this, target)) && ! this.hitSet.contains(target.getId());
    }

    /**
     * handle server to client event.
     */
    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {//die event.
            if (this.getHitParticle() != null) {
                for (int i = 0; i < 8; ++i) {
                    final float offsetX = this.random.nextFloat() * 0.4F;
                    this.level.addParticle(this.getHitParticle(), this.getX() + offsetX, this.getY() + offsetX, this.getZ() + offsetX, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }

    @Nullable
    protected ParticleOptions getHitParticle() {
        return null;
    }

    @Nullable
    protected ParticleOptions getTrailParticle() {
        return null;
    }


    /**
     * shoot bullet such as pea or spore
     */
    public void shootToTarget(Mob owner, PVZPlantComponent.BulletSettings bulletSettings, double dx, double dy, double dz, double angleOffset) {
        final double down = 0;
        final double dxz = Math.sqrt(dx * dx + dz * dz);
        if (down != 0) {
            dy = Mth.clamp(dy, -dxz / down, dxz / down);//fix dy by angle
        }
        final double degree = Mth.atan2(dz, dx) + Math.toRadians(angleOffset);
        dx = Math.cos(degree) * dxz;
        dz = Math.sin(degree) * dxz;

        final double totSpeed = Math.sqrt(dxz * dxz + dy * dy);
        this.setDeltaMovement(new Vec3(dx / totSpeed, dy / totSpeed, dz / totSpeed).scale(bulletSettings.bulletSpeed()));
        this.summonBy(owner, bulletSettings);
    }

    public void shootTo(Mob owner, PVZPlantComponent.BulletSettings bulletSettings, Vec3 vec, double angleOffset) {
        final double dxz = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        final double degree = Mth.atan2(vec.z, vec.x) + Math.toRadians(angleOffset);
        final double x = Math.cos(degree) * dxz;
        final double z = Math.sin(degree) * dxz;
        final double totSpeed = Math.sqrt(dxz * dxz + vec.y * vec.y);
        this.setDeltaMovement(new Vec3(x / totSpeed, vec.y / totSpeed, z / totSpeed).scale(bulletSettings.bulletSpeed()));
        this.summonBy(owner, bulletSettings);
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
        return this.getSettings() == null ? 0 : this.getSettings().maxExistTick();
    }

    protected float getGravity() {
        return this.getSettings() == null ? 0 : this.getSettings().gravity();
    }

    protected int getMaxHitCount(){
        return this.getSettings() == null ? 1 : this.getSettings().maxHitCount();
    }

    protected boolean ignoreBlock(){
        return this.getSettings() != null && this.getSettings().ignoreBlock();
    }

    protected List<IEffectComponent> getEffects(){
        return this.getSettings() == null ? Arrays.asList() : this.getSettings().effects();
    }

    public PVZPlantComponent.BulletSettings getSettings() {
        return this.settings;
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        if (this.settings != null) {
            PVZPlantComponent.BulletSettings.CODEC.encodeStart(NbtOps.INSTANCE, this.settings)
                    .resultOrPartial(msg -> OpenTD.log().error(msg + " [Bullet] "))
                    .ifPresent(tag -> buffer.writeNbt((CompoundTag) tag));
        } else {
            buffer.writeNbt(new CompoundTag());
        }
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        PVZPlantComponent.BulletSettings.CODEC.parse(NbtOps.INSTANCE, additionalData.readNbt())
                .resultOrPartial(msg -> OpenTD.log().error(msg + " [Bullet] "))
                .ifPresent(settings -> this.settings = settings);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("TickCount", this.tickCount);
        if (this.settings != null) {
            PVZPlantComponent.BulletSettings.CODEC.encodeStart(NbtOps.INSTANCE, this.settings)
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
        if (compound.contains("BulletSettings")) {
            PVZPlantComponent.BulletSettings.CODEC.parse(NbtOps.INSTANCE, compound.get("BulletSettings"))
                    .resultOrPartial(msg -> OpenTD.log().error(msg + " [Bullet] "))
                    .ifPresent(settings -> this.settings = settings);
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

    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}

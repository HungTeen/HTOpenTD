package hungteen.opentd.common.entity;

import com.mojang.datafixers.util.Pair;
import hungteen.opentd.common.codec.RenderSetting;
import hungteen.opentd.common.impl.tower.PVZPlantComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-14 21:10
 **/
public class PlantEntity extends TowerEntity {

    public static final int GROW_ANIM_CD = 20;
    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(PlantEntity.class, EntityDataSerializers.INT);
    private PVZPlantComponent component;
    protected BlockPos stayPos;
    protected int growTick = 0;
    protected int forcedAgeTimer;
    public int oldAge = 0;
    private int existTick = 0;

    public PlantEntity(EntityType<? extends TowerEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(AGE, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (accessor.equals(AGE)) {
            this.refreshDimensions();
            this.growAnimTick = GROW_ANIM_CD;
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        if (this.getComponent() != null) {
            if(this.getComponent().plantSetting().changeDirection()){
                this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
                this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
            }
            if(this.getComponent().plantSetting().canFloat()){
                this.goalSelector.addGoal(1, new FloatGoal(this));
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TowerEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.ATTACK_DAMAGE, 1D)
                .add(Attributes.ATTACK_KNOCKBACK, 0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1D)
                .add(Attributes.FOLLOW_RANGE, 40D)
                .add(Attributes.MOVEMENT_SPEED, 0D);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel) {
            // 植物生长。
            if (this.isAlive()) {
                if (this.canGrow()) {
                    if (this.growTick >= this.getGrowNeedTime()) {
                        this.onGrow();
                    }
                    ++this.growTick;
                }
            }
            // TODO 能量豆大招时。
//            if (EntityUtil.inEnergetic(this)) {
//                this.getShootSettings().stream().filter(PVZPlantComponent.ShootSettings::plantFoodOnly).forEach(this::performShoot);
//                this.getGenSettings().stream().filter(PVZPlantComponent.GenSettings::plantFoodOnly).forEach(this::gen);
//            }
        } else {
            // 长大的粒子效果。
            if (this.forcedAgeTimer > 0) {
                if (this.forcedAgeTimer % 4 == 0) {
                    this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
                }
                --this.forcedAgeTimer;
            }
            // 长大的缩放渐变动画。
            if (this.oldAge != this.getAge()) {
                if (this.growAnimTick > 0) {
                    --this.growAnimTick;
                } else {
                    this.oldAge = this.getAge();
                }
            } else if (this.growAnimTick != 0) {
                this.growAnimTick = 0;
            }
        }
    }

    @Override
    public void push(Entity target) {
        if (!this.isSleeping() && !this.isPassengerOfSameVehicle(target)) {
            if (!target.noPhysics && !this.noPhysics) {
                double d0 = target.getX() - this.getX();
                double d1 = target.getZ() - this.getZ();
                double d2 = Mth.absMax(d0, d1);
                if (d2 >= (double) 0.01F) {
                    d2 = Math.sqrt(d2);
                    d0 /= d2;
                    d1 /= d2;
                    double d3 = 1.0D / d2;
                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }

                    d0 *= d3;
                    d1 *= d3;
                    d0 *= (double) 0.05F;
                    d1 *= (double) 0.05F;
                    if (!this.isVehicle() && this.isPushable()) {
                        this.push(-d0, 0.0D, -d1);
                    }
                    //TODO Push Filter
                    if (!target.isVehicle() && target.isPushable()) {
                        target.push(d0, 0.0D, d1);
                    }
                }
            }
        }
    }

    @Override
    public void push(double xSpeed, double ySpeed, double zSpeed) {
        if (this.getComponent() == null || this.getComponent().plantSetting().pushable()) {
            super.push(xSpeed, ySpeed, zSpeed);
        }
    }

    public void onGrow() {
        this.setAge(this.getAge() + 1);
        this.growTick = 0;
        this.getGrowSettings().growSound().map(Holder::get).ifPresent(this::playSound);
        if (this.level() instanceof ServerLevel serverLevel) {
            this.getGrowSettings().growEffects().stream()
                    .filter(l -> l.getSecond() == this.getAge())
                    .map(Pair::getFirst)
                    .forEach(l -> l.get().effectTo(serverLevel, this, this.blockPosition()));
        }
    }

    @Override
    public void rangeEffect() {
        if(this.getComponent() != null){
            if (this.getComponent().plantSetting().maxExistTick() > 0) {
                if (++ this.existTick >= this.getComponent().plantSetting().maxExistTick()) {
                    this.discard();
                    return;
                }
            }
            super.rangeEffect();
        }

    }

    public int getMaxAge() {
        return this.getComponent() == null ? 1 : this.getComponent().plantSetting().growSetting().getMaxAge();
    }

    public boolean canGrow() {
        return this.getAge() < this.getMaxAge() - 1;
    }

    public int getGrowNeedTime() {
        return this.getComponent().plantSetting().growSetting().growDurations().get(this.getAge());
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
//        if(this.getComponent() != null && ! this.getComponent().plantSetting().changeDirection()){
//            if(entity.level != this.level){
//                return false;
//            } else{
//                Vec3 vec = entity.getEyePosition().subtract(this.getEyePosition());
//                if(vec.length() > 128 || vec.length() < 0.01){
//                    return false;
//                }
//                double cos = (Math.acos(vec.normalize().dot(this.getLookAngle().normalize())) + 180) % 180;
//                return cos < this.getComponent().plantSetting().senseAngle();
//            }
//        }
        return super.hasLineOfSight(entity);
    }

    @Override
    public PVZPlantComponent getComponent() {
        if (component == null || this.componentDirty) {
            this.parseComponent(PVZPlantComponent.CODEC, t -> this.component = t);
            this.componentDirty = false;
        }
        return component;
    }

    @Override
    public RenderSetting getRenderSetting() {
        return getComponent() != null ? getComponent().plantSetting().renderSetting() : RenderSetting.DEFAULT;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        final float width = getRenderSettings().width();
        final float height = getRenderSettings().height();
        final float scale = getGrowSettings().scales().get(this.getAge()) * getRenderSettings().scale();
        return EntityDimensions.scalable(width * scale, height * scale);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        super.writeSpawnData(buffer);
        buffer.writeInt(this.getAge());
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        super.readSpawnData(additionalData);
        this.oldAge = additionalData.readInt();
    }

    @Override
    public boolean canChangeDirection() {
        return this.component != null && this.getComponent().plantSetting().changeDirection();
    }

    @Override
    public boolean sameTeamWithOwner() {
        return getComponent() != null && getComponent().plantSetting().sameTeamWithOwner();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("CreatureAge", this.getAge());
        tag.putInt("CreatureGrowTick", this.growTick);
        if (this.stayPos != null) {
            tag.putLong("StayPos", this.stayPos.asLong());
        }
        tag.putInt("ExistTick", this.existTick);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // Refresh component when tag changed.
        this.getComponent();
        if (tag.contains("CreatureAge")) {
            this.setAge(tag.getInt("CreatureAge"));
        }
        if (tag.contains("CreatureGrowTick")) {
            this.growTick = tag.getInt("CreatureGrowTick");
        }
        if (tag.contains("StayPos")) {
            this.stayPos = BlockPos.of(tag.getLong("StayPos"));
        }
        if (tag.contains("ExistTick")) {
            this.existTick = tag.getInt("ExistTick");
        }
    }

    public PVZPlantComponent.GrowSettings getGrowSettings() {
        return this.getComponent() == null ? PVZPlantComponent.GrowSettings.DEFAULT : this.getComponent().plantSetting().growSetting();
    }

    public RenderSetting getRenderSettings() {
        return this.getComponent() == null ? RenderSetting.DEFAULT : this.getComponent().plantSetting().renderSetting();
    }

    public void setAge(int age) {
        entityData.set(AGE, age);
    }

    public int getAge() {
        return entityData.get(AGE);
    }

}

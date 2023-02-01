package hungteen.opentd.common.entity;

import com.mojang.datafixers.util.Pair;
import hungteen.htlib.util.helper.EntityHelper;
import hungteen.htlib.util.helper.RandomHelper;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.common.entity.ai.PlantAttackGoal;
import hungteen.opentd.common.entity.ai.PlantGenGoal;
import hungteen.opentd.common.entity.ai.PlantShootGoal;
import hungteen.opentd.common.entity.ai.PlantTargetGoal;
import hungteen.opentd.impl.tower.HTTowerComponents;
import hungteen.opentd.impl.tower.PVZPlantComponent;
import hungteen.opentd.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.TeamCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Arrays;
import java.util.List;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-14 21:10
 **/
public class PlantEntity extends TowerEntity {

    public static final String PLANT_SETTINGS = "PlantSettings";
    public static final String YROT = "YRot";
    public static final int GROW_ANIM_CD = 20;
    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(PlantEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> SHOOT_TICK = SynchedEntityData.defineId(PlantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> GEN_TICK = SynchedEntityData.defineId(PlantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(PlantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> INSTANT_TICK = SynchedEntityData.defineId(PlantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> RESTING = SynchedEntityData.defineId(PlantEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private CompoundTag componentTag = new CompoundTag();
    private PVZPlantComponent component;
    private PVZPlantComponent.GenSettings genSettings;
    protected BlockPos stayPos;
    protected int growTick = 0;
    public int preShootTick = 0;
    public int preGenTick = 0;
    public int preAttackTick = 0;
    protected int forcedAgeTimer;
    private boolean updated = false;
    protected int shootCount = 0;
    public int growAnimTick = 0;
    public int oldAge = 0;
    private int existTick = 0;

    public PlantEntity(EntityType<? extends TowerEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(AGE, 0);
        entityData.define(SHOOT_TICK, 0);
        entityData.define(GEN_TICK, 0);
        entityData.define(ATTACK_TICK, 0);
        entityData.define(INSTANT_TICK, 0);
        entityData.define(RESTING, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if (dataAccessor.equals(AGE)) {
            this.refreshDimensions();
            this.growAnimTick = GROW_ANIM_CD;
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance instance, MobSpawnType spawnType, @Nullable SpawnGroupData groupData, @Nullable CompoundTag compoundTag) {
        if (compoundTag != null) {
            this.componentTag = (CompoundTag) compoundTag.get(PLANT_SETTINGS);
            if (compoundTag.contains(YROT)) {
                this.setYRot(Direction.fromYRot(compoundTag.getFloat(YROT)).toYRot());
                this.yHeadRot = this.getYRot();
                this.yBodyRot = this.getYRot();
            }
        }
        return super.finalizeSpawn(accessor, instance, spawnType, groupData, compoundTag);
    }

    @Override
    protected void registerGoals() {
        if (this.getComponent() != null) {
            this.targetSelector.removeAllGoals();
            this.goalSelector.removeAllGoals();
            this.getComponent().targetSettings().forEach(targetSettings -> {
                this.targetSelector.addGoal(targetSettings.priority(), new PlantTargetGoal(this, targetSettings));
            });
            this.goalSelector.addGoal(2, new PlantShootGoal(this));
            this.goalSelector.addGoal(2, new PlantGenGoal(this));
            this.goalSelector.addGoal(1, new PlantAttackGoal(this));
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.ATTACK_DAMAGE, 1D)
                .add(Attributes.ATTACK_KNOCKBACK, 0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1D)
                .add(Attributes.FOLLOW_RANGE, 40D)
                .add(Attributes.MOVEMENT_SPEED, 0D);
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

    @Override
    public void tick() {
        super.tick();
        if (this.getComponent() != null && this.tickCount <= 5) {
            this.refreshDimensions();
        }
        if (this.level instanceof ServerLevel) {
            // 延迟更新植物的行为。
            if (!this.updated && this.getComponent() != null) {
                this.updated = true;
                this.registerGoals();
            }
            // 植物生长。
            if (this.isAlive()) {
                if (this.canGrow()) {
                    if (this.growTick >= this.getGrowNeedTime()) {
                        this.onGrow();
                    }
                    ++this.growTick;
                }
            }
            // 距离灰烬植物。
            if (this.getComponent() != null && this.getTarget() != null) {
                this.getComponent().instantEffectSetting().ifPresent(l -> {
                    if (l.targetFilter().match((ServerLevel) this.level, this, this.getTarget()) && this.distanceTo(this.getTarget()) < l.closeRange()) {
                        if (this.getInstantTick() >= l.instantTick()) {
                            l.effects().forEach(e -> e.effectTo((ServerLevel) this.level, this, this.getTarget()));
                            this.discard();
                        } else {
                            this.setInstantTick(this.getInstantTick() + 1);
                        }
                    } else {
                        this.setInstantTick(0);
                    }
                });
            }
            // 范围作用植物。
            if (this.getComponent() != null) {
                if(this.getComponent().plantSetting().maxExistTick() > 0){
                    if(++ this.existTick >= this.getComponent().plantSetting().maxExistTick()){
                        this.discard();
                        return;
                    }
                }
                this.getComponent().constantAffectSettings().forEach(setting -> {
                    if (this.tickCount % setting.cd() == 0) {
                        setting.targetFinder().getTargets((ServerLevel) this.level, this).forEach(target -> {
                            setting.effectSettings().stream().filter(e -> e.targetFilter().match((ServerLevel) this.level, this, target)).forEach(e -> {
                                e.effects().forEach(l -> l.effectTo((ServerLevel) this.level, this, target));
                            });
                        });
                    }
                });
            }
            // 大招时。
            if (EntityUtil.inEnergetic(this)) {
                this.getShootSettings().stream().filter(PVZPlantComponent.ShootSettings::plantFoodOnly).forEach(this::performShoot);
                this.getGenSettings().stream().filter(PVZPlantComponent.GenSettings::plantFoodOnly).forEach(this::gen);
            }
            // 处理植物射击行为的子弹。
            if (this.getTarget() != null) {
                if (!this.getShootSettings().isEmpty() && this.shootCount > 0 && !EntityUtil.inEnergetic(this)) {
                    final int count = this.getComponent().shootGoalSetting().get().shootCount();
                    this.getShootSettings().stream().filter(l -> !l.plantFoodOnly() && l.shootTick() == count - this.shootCount).forEach(this::performShoot);
                    --this.shootCount;
                }
            }
        } else {
            // 长大的粒子效果。
            if (this.forcedAgeTimer > 0) {
                if (this.forcedAgeTimer % 4 == 0) {
                    this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
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

    protected PlayState predicateAnimation(AnimationEvent<?> event) {
        final AnimationBuilder builder = new AnimationBuilder();
        if (this.getShootTick() > 0) {
            builder.addAnimation("shoot", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
        } else if (this.getGenTick() > 0) {
            builder.addAnimation("gen", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
        } else if (this.getAttackTick() > 0) {
            builder.addAnimation("attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
        } else if (this.getInstantTick() > 0) {
            builder.addAnimation("instant", ILoopType.EDefaultLoopTypes.PLAY_ONCE);
        } else {
            if (this.isResting()) {
                builder.addAnimation("rest", ILoopType.EDefaultLoopTypes.LOOP);
            } else {
                builder.addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP);
            }
        }
        event.getController().setAnimation(builder);
        return PlayState.CONTINUE;
    }

    public void startShootAttack(LivingEntity target) {
        if (this.getComponent() != null) {
            this.getComponent().shootGoalSetting().ifPresent(l -> this.shootCount = l.shootCount());
            this.getComponent().shootGoalSetting().flatMap(PVZPlantComponent.ShootGoalSetting::shootSound).ifPresent(this::playSound);
        }
    }

    /**
     * attack pea with offsets.
     */
    public void performShoot(PVZPlantComponent.ShootSettings shootSettings) {
        final BulletEntity bullet = OpenTDEntities.BULLET_ENTITY.get().create(this.level);
        if (bullet != null) {
            final Vec3 vec = this.getViewVector(1F);
            final double deltaY = this.getDimensions(getPose()).height * 0.7F + shootSettings.offset().y;
            final double deltaX = shootSettings.offset().x * vec.x - shootSettings.offset().z * vec.z;
            final double deltaZ = shootSettings.offset().x * vec.z + shootSettings.offset().z * vec.x;
            bullet.setPos(this.getX() + deltaX, this.getY() + deltaY, this.getZ() + deltaZ);
            if (this.getTarget() != null && shootSettings.bulletSettings().lockToTarget()) {
                bullet.shootToTarget(this, shootSettings, this.getTarget(), this.getTarget().getX() - bullet.getX(), this.getTarget().getY() + this.getTarget().getBbHeight() - bullet.getY(), this.getTarget().getZ() - bullet.getZ());
            } else {
                bullet.shootTo(this, shootSettings, vec);
            }
            this.level.addFreshEntity(bullet);
        }
    }

    public void gen(PVZPlantComponent.GenSettings genSettings) {
        if (genSettings != null && this.getComponent() != null && this.getComponent().genGoalSetting().isPresent()) {
            if (this.level instanceof ServerLevel serverlevel && Level.isInSpawnableBounds(this.blockPosition())) {
                CompoundTag compoundtag = genSettings.nbt().copy();
                compoundtag.putString("id", EntityHelper.getKey(genSettings.entityType()).toString());
                final Vec3 position = this.getEyePosition().add(genSettings.offset());
                Entity entity = EntityType.loadEntityRecursive(compoundtag, serverlevel, (e) -> {
                    e.moveTo(position.x(), position.y(), position.z(), this.getYRot(), this.getXRot());
                    return e;
                });
                if (entity != null) {
                    if (entity instanceof Mob) {
                        ((Mob) entity).finalizeSpawn(serverlevel, serverlevel.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    }
                    final double dx = RandomHelper.floatRange(this.getRandom());
                    final double dz = RandomHelper.floatRange(this.getRandom());
                    entity.setDeltaMovement(new Vec3(dx, 0, dz).scale(genSettings.horizontalSpeed()).add(0, genSettings.verticalSpeed(), 0));
                    serverlevel.tryAddFreshEntityWithPassengers(entity);
                    this.getComponent().genGoalSetting().get().genSound().ifPresent(this::playSound);
                }
            }
        }
    }

    public void attack() {
        if (this.getComponent() != null && this.getComponent().attackGoalSetting().isPresent() && this.level instanceof ServerLevel) {
            this.getComponent().attackGoalSetting().get().effects().forEach(effect -> {
                if (this.getTarget() != null) {
                    effect.effectTo((ServerLevel) this.level, this, this.getTarget());
                } else {
                    effect.effectTo((ServerLevel) this.level, this, this.blockPosition());
                }
            });
            this.getComponent().attackGoalSetting().get().attackSound().ifPresent(this::playSound);
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
        this.getGrowSettings().growSound().ifPresent(this::playSound);
        if(this.level instanceof ServerLevel){
            this.getGrowSettings().growEffects().stream()
                    .filter(l -> l.getSecond() == this.getAge())
                    .map(Pair::getFirst)
                    .forEach(l -> l.effectTo((ServerLevel) this.level, this, this.blockPosition()));
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (super.hurt(source, amount)) {
            if (this.getComponent() != null && this.level instanceof ServerLevel) {
                this.getComponent().hurtSettings().forEach(settings -> {
                    if (source.getEntity() != null && settings.targetFilter().match((ServerLevel) this.level, this, source.getEntity())) {
                        settings.effects().forEach(effect -> {
                            effect.effectTo((ServerLevel) this.level, this, source.getEntity());
                        });
                    } else {
                        settings.effects().forEach(effect -> {
                            effect.effectTo((ServerLevel) this.level, this, this.blockPosition());
                        });
                    }
                });
            }
            return true;
        }
        return false;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (this.getComponent() != null && this.level instanceof ServerLevel) {
            this.getComponent().dieSettings().forEach(settings -> {
                if (source.getEntity() != null && settings.targetFilter().match((ServerLevel) this.level, this, source.getEntity())) {
                    settings.effects().forEach(effect -> {
                        effect.effectTo((ServerLevel) this.level, this, source.getEntity());
                    });
                } else {
                    settings.effects().forEach(effect -> {
                        effect.effectTo((ServerLevel) this.level, this, this.blockPosition());
                    });
                }
            });
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
    public AnimationFactory getFactory() {
        return this.factory;
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

    public PVZPlantComponent getComponent() {
        if (component == null) {
            PVZPlantComponent.CODEC.parse(NbtOps.INSTANCE, this.componentTag)
                    .resultOrPartial(msg -> {
                        if (this.tickCount > 0) {
                            OpenTD.log().error(msg);
                        }
                    })
                    .ifPresent(c -> this.component = c);
            if (this.component == null && this.tickCount >= 5) {
                this.discard();
            }
        }
        return component;
    }

    public int getCurrentShootCD() {
        if (this.getComponent() != null && this.getComponent().shootGoalSetting().isPresent()) {
            return this.getComponent().shootGoalSetting().get().coolDown();
        }
        return 1000000;
    }

    public int getStartShootTick() {
        if (this.getComponent() != null && this.getComponent().shootGoalSetting().isPresent()) {
            return this.getComponent().shootGoalSetting().get().startTick();
        }
        return 1000000;
    }

    public int getCurrentGenCD() {
        if (this.getComponent() != null && this.getComponent().genGoalSetting().isPresent()) {
            return this.getComponent().genGoalSetting().get().coolDown();
        }
        return 1000000;
    }

    public int getStartGenTick() {
        if (this.getComponent() != null && this.getComponent().genGoalSetting().isPresent()) {
            return this.getComponent().genGoalSetting().get().startTick();
        }
        return 1000000;
    }

    public int getCurrentAttackCD() {
        if (this.getComponent() != null && this.getComponent().attackGoalSetting().isPresent()) {
            return this.getComponent().attackGoalSetting().get().coolDown();
        }
        return 1000000;
    }

    public int getStartAttackTick() {
        if (this.getComponent() != null && this.getComponent().attackGoalSetting().isPresent()) {
            return this.getComponent().attackGoalSetting().get().startTick();
        }
        return 1000000;
    }

    public List<PVZPlantComponent.ShootSettings> getShootSettings() {
        if (this.getComponent() != null && this.getComponent().shootGoalSetting().isPresent()) {
            return this.getComponent().shootGoalSetting().get().shootSettings();
        }
        return Arrays.asList();
    }

    public List<PVZPlantComponent.GenSettings> getGenSettings() {
        if (this.getComponent() != null && this.getComponent().genGoalSetting().isPresent()) {
            return this.getComponent().genGoalSetting().get().genSettings();
        }
        return Arrays.asList();
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
        buffer.writeNbt(this.componentTag);
        buffer.writeInt(this.getAge());
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        this.componentTag = additionalData.readNbt();
        this.oldAge = additionalData.readInt();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("ComponentTag", this.componentTag);
        tag.putInt("CreatureAge", this.getAge());
        tag.putInt("CreatureGrowTick", this.growTick);
        tag.putInt("ShootTick", this.getShootTick());
        tag.putInt("PreShootTick", this.preShootTick);
        tag.putInt("ShootCount", this.shootCount);
        tag.putInt("GenTick", this.getGenTick());
        tag.putInt("PreGenTick", this.preGenTick);
        tag.putInt("AttackTick", this.getAttackTick());
        tag.putInt("PreAttackTick", this.preAttackTick);
        tag.putInt("InstantTick", this.getInstantTick());
        tag.putInt("ExistTick", this.existTick);
        tag.putBoolean("Resting", this.isResting());
        if (this.stayPos != null) {
            tag.putLong("StayPos", this.stayPos.asLong());
        }
        if (this.genSettings != null) {
            PVZPlantComponent.GenSettings.CODEC.encodeStart(NbtOps.INSTANCE, this.genSettings)
                    .resultOrPartial(msg -> OpenTD.log().error(msg + " [Plant Gen]"))
                    .ifPresent(nbt -> tag.put("Production", nbt));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("ComponentTag")) {
            this.componentTag = tag.getCompound("ComponentTag");
        }
        // 专门用于NBT召唤特定植物。
        if (tag.contains("ComponentLocation")) {
            final ResourceLocation location = new ResourceLocation(tag.getString("ComponentLocation"));
            HTTowerComponents.TOWERS.getValue(location).ifPresent(l -> {
                if (l instanceof PVZPlantComponent) {
                    PVZPlantComponent.CODEC.encodeStart(NbtOps.INSTANCE, ((PVZPlantComponent) l))
                            .resultOrPartial(msg -> OpenTD.log().error(msg + " [Read Tower]"))
                            .ifPresent(nbt -> this.componentTag = (CompoundTag) nbt);
                }
            });
        }
        // Refresh component when tag changed.
        this.getComponent();
        if (tag.contains("CreatureAge")) {
            this.setAge(tag.getInt("CreatureAge"));
        }
        if (tag.contains("CreatureGrowTick")) {
            this.growTick = tag.getInt("CreatureGrowTick");
        }
        if (tag.contains("ShootTick")) {
            this.setShootTick(tag.getInt("ShootTick"));
        }
        if (tag.contains("PreShootTick")) {
            this.preShootTick = tag.getInt("PreShootTick");
        }
        if (tag.contains("ShootCount")) {
            this.shootCount = tag.getInt("ShootCount");
        }
        if (tag.contains("GenTick")) {
            this.setGenTick(tag.getInt("GenTick"));
        }
        if (tag.contains("PreGenTick")) {
            this.preGenTick = tag.getInt("PreGenTick");
        }
        if (tag.contains("AttackTick")) {
            this.setAttackTick(tag.getInt("AttackTick"));
        }
        if (tag.contains("PreAttackTick")) {
            this.preAttackTick = tag.getInt("PreAttackTick");
        }
        if (tag.contains("InstantTick")) {
            this.setInstantTick(tag.getInt("InstantTick"));
        }
        if(tag.contains("ExistTick")){
            this.existTick = tag.getInt("ExistTick");
        }
        if (tag.contains("Resting")) {
            this.setResting(tag.getBoolean("Resting"));
        }
        if (tag.contains("StayPos")) {
            this.stayPos = BlockPos.of(tag.getLong("StayPos"));
        }
        if (tag.contains("Production")) {
            PVZPlantComponent.GenSettings.CODEC.parse(NbtOps.INSTANCE, tag.get("Production"))
                    .resultOrPartial(msg -> OpenTD.log().error(msg + " [Plant Gen]"))
                    .ifPresent(settings -> this.genSettings = settings);
        }
    }

    public PVZPlantComponent.GrowSettings getGrowSettings() {
        return this.getComponent() == null ? PVZPlantComponent.GrowSettings.DEFAULT : this.getComponent().plantSetting().growSetting();
    }

    public PVZPlantComponent.RenderSettings getRenderSettings() {
        return this.getComponent() == null ? PVZPlantComponent.RenderSettings.DEFAULT : this.getComponent().plantSetting().renderSetting();
    }

    @Nullable
    public PVZPlantComponent.GenSettings getProduction() {
        return this.genSettings;
    }

    public void setProduction(PVZPlantComponent.GenSettings settings) {
        this.genSettings = settings;
    }

    public void setAge(int age) {
        entityData.set(AGE, age);
    }

    public int getAge() {
        return entityData.get(AGE);
    }

    public void setShootTick(int tick) {
        entityData.set(SHOOT_TICK, tick);
    }

    public int getShootTick() {
        return entityData.get(SHOOT_TICK);
    }


    public void setGenTick(int tick) {
        entityData.set(GEN_TICK, tick);
    }

    public int getGenTick() {
        return entityData.get(GEN_TICK);
    }

    public void setAttackTick(int tick) {
        entityData.set(ATTACK_TICK, tick);
    }

    public int getAttackTick() {
        return entityData.get(ATTACK_TICK);
    }

    public void setInstantTick(int tick) {
        entityData.set(INSTANT_TICK, tick);
    }

    public int getInstantTick() {
        return entityData.get(INSTANT_TICK);
    }

    public void setResting(boolean resting) {
        entityData.set(RESTING, resting);
    }

    public boolean isResting() {
        return entityData.get(RESTING);
    }
}

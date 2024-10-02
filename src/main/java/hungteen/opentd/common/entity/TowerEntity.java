package hungteen.opentd.common.entity;

import com.mojang.serialization.Codec;
import hungteen.htlib.util.helper.CodecHelper;
import hungteen.htlib.util.helper.JavaHelper;
import hungteen.htlib.util.helper.RandomHelper;
import hungteen.htlib.util.helper.registry.EntityHelper;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.common.codec.*;
import hungteen.opentd.common.entity.ai.*;
import hungteen.opentd.common.event.events.ShootBulletEvent;
import hungteen.opentd.common.impl.tower.OTDTowerComponents;
import hungteen.opentd.util.EntityUtil;
import hungteen.opentd.util.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-14 21:12
 **/
public abstract class TowerEntity extends PathfinderMob implements IOTDEntity {

    public static final String TOWER_SETTING = "TowerSettings";
    public static final String YROT = "YRot";
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(TowerEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> SHOOT_TICK = SynchedEntityData.defineId(TowerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> GEN_TICK = SynchedEntityData.defineId(TowerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(TowerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> INSTANT_TICK = SynchedEntityData.defineId(TowerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> RESTING = SynchedEntityData.defineId(TowerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ATTACK_TARGET = SynchedEntityData.defineId(TowerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ClientEntityResource> CLIENT_RES = SynchedEntityData.defineId(TowerEntity.class, OTDSerializers.CLIENT_ENTITY_RES.get());
    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);
    private CompoundTag componentTag = new CompoundTag();
    private ServerBossEvent bossEvent;
    public int preShootTick = 0;
    public int preGenTick = 0;
    public int preAttackTick = 0;
    public int preLaserTick = 0;
    protected int shootCount = 0;
    public int growAnimTick = 0;
    private boolean ignoreWorkAnimation = false;
    protected boolean componentDirty = false;
    private GenGoalSetting.GenSetting genSetting;
    private boolean updated = false;
    @javax.annotation.Nullable
    private LivingEntity clientSideCachedAttackTarget;
    private int clientSideAttackTime;

    public TowerEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.refreshDimensions();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.ATTACK_DAMAGE, 1D)
                .add(Attributes.ATTACK_KNOCKBACK, 0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1D)
                .add(Attributes.FOLLOW_RANGE, 40D)
                .add(Attributes.MOVEMENT_SPEED, 0D)
                .add(Attributes.FLYING_SPEED);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, Optional.empty());
        entityData.define(SHOOT_TICK, 0);
        entityData.define(GEN_TICK, 0);
        entityData.define(ATTACK_TICK, 0);
        entityData.define(INSTANT_TICK, 0);
        entityData.define(RESTING, false);
        entityData.define(ATTACK_TARGET, 0);
        entityData.define(CLIENT_RES, new ClientEntityResource());
    }

    @Override
    protected void registerGoals() {
        if (this.getComponent() != null) {
            this.targetSelector.removeAllGoals(JavaHelper::alwaysTrue);
            this.goalSelector.removeAllGoals(JavaHelper::alwaysTrue);
            this.getComponent().targetSettings().forEach(targetSettings -> {
                this.targetSelector.addGoal(targetSettings.priority(), new TowerTargetGoal(this, targetSettings));
            });
            this.goalSelector.addGoal(2, new TowerShootGoal(this));
            this.goalSelector.addGoal(2, new TowerGenGoal(this));
            this.goalSelector.addGoal(1, new TowerAttackGoal(this));
            this.goalSelector.addGoal(2, new TowerLaserAttackGoal(this));
            this.getComponent().followGoalSetting().ifPresent(setting -> {
                this.goalSelector.addGoal(4, new TowerFollowGoal(this, setting));
            });
            this.getComponent().movementSetting().ifPresent(movementSetting -> {
                movementSetting.navigationSetting().ifPresent(t -> {
                    if (t.canFloat()) { // 可以漂浮在水面上。
                        this.goalSelector.addGoal(1, new FloatGoal(this));
                    }
                });
                if (movementSetting.canRandomMove()) {
                    if (this.navigation instanceof WaterBoundPathNavigation) {
                        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
                    } else {
                        if (movementSetting.avoidWater()) {
                            this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
                        } else {
                            this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0D));
                        }
                    }
                }
                if (movementSetting.keepDistance()) {
                    this.goalSelector.addGoal(1, new KeepDistanceWithTargetGoal(this, movementSetting.speedModifier(), movementSetting.backwardPercent(), movementSetting.upwardPercent()));
                } else {
                    this.goalSelector.addGoal(1, new MeleeMoveToTargetGoal(this, movementSetting.speedModifier(), false));
                }
            });
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance instance, MobSpawnType spawnType, @Nullable SpawnGroupData groupData, @Nullable CompoundTag compoundTag) {
        if (compoundTag != null) {
            this.componentTag = compoundTag.getCompound(TOWER_SETTING);
            if (compoundTag.contains(YROT)) {
                this.setYRot(Direction.fromYRot(compoundTag.getFloat(YROT)).toYRot());
                this.yHeadRot = this.getYRot();
                this.yBodyRot = this.getYRot();
            }
        }
        return super.finalizeSpawn(accessor, instance, spawnType, groupData, compoundTag);
    }

    /**
     * 塔防组件改变时，需要更新。
     */
    public void updateComponent() {
        this.updateMovement();
        this.registerGoals();
    }

    public void updateMovement() {
        this.getComponent().movementSetting().ifPresent(l -> {
            l.navigationSetting().ifPresent(t -> {
                this.navigation = t.getNavigator(this.level(), this);
                t.nodeWeightList().forEach(pair -> {
                    try {
                        this.setPathfindingMalus(BlockPathTypes.valueOf(pair.getFirst()), pair.getSecond());
                    } catch (IllegalArgumentException e) {
                        Util.error("Unable to find path type for {}", pair.getFirst());
                    }
                });
            });
            l.moveComponent().ifPresent(t -> this.moveControl = t.get().create(this));
        });
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeNbt(this.componentTag);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        this.componentTag = additionalData.readNbt();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getComponent() != null && this.tickCount <= 5) {
            this.refreshDimensions();
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            // 延迟更新防御塔的行为。
            if (!this.updated && this.getComponent() != null) {
                this.updated = true;
                this.updateComponent();
            }
            // NoAI 不能有效果。
            if(!this.isNoAi()){
                // 距离灰烬植物。
                if (this.getComponent() != null && this.getTarget() != null) {
                    this.getComponent().instantEffectSetting().ifPresent(l -> {
                        if (l.targetFilter().get().match(serverLevel, this, this.getTarget()) && this.distanceTo(this.getTarget()) < l.closeRange()) {
                            if (this.getInstantTick() >= l.instantTick()) {
                                l.effect().get().effectTo(serverLevel, this, this.getTarget());
                                this.discard();
                            } else {
                                this.setInstantTick(this.getInstantTick() + 1);
                            }
                        } else {
                            this.setInstantTick(0);
                        }
                    });
                }
                this.rangeEffect();
                // 处理植物射击行为的子弹。
                if (this.getTarget() != null) {
                    if (!this.getShootSettings().isEmpty() && this.shootCount > 0 && !EntityUtil.inEnergetic(this)) {
                        final int count = this.getComponent().shootGoalSetting().get().shootCount();
                        this.getShootSettings().stream().filter(l -> !l.plantFoodOnly() && l.shootTick() == count - this.shootCount).forEach(this::performShoot);
                        --this.shootCount;
                    }
                }
            }
        }
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.getBossEventOpt().ifPresent(bossEvent -> bossEvent.setProgress(this.getHealth() / this.getMaxHealth()));
    }

    @Override
    public void aiStep() {
        if (this.level().isClientSide && this.isAlive() && this.hasActiveAttackTarget() && this.getLaserSetting() != null) {
            if (this.clientSideAttackTime < this.getLaserSetting().duration()) {
                ++this.clientSideAttackTime;
            }
        }

        super.aiStep();
    }

    public void startShootAttack(LivingEntity target) {
        if (this.getComponent() != null) {
            this.getComponent().shootGoalSetting().ifPresent(l -> this.shootCount = l.shootCount());
            this.getComponent().shootGoalSetting().flatMap(ShootGoalSetting::shootSound).map(Holder::get).ifPresent(this::playSound);
        }
    }

    /**
     * attack pea with offsets.
     */
    public void performShoot(ShootGoalSetting.ShootSetting shootSetting) {
        final BulletEntity bullet = OpenTDEntities.BULLET_ENTITY.get().create(this.level());
        if (bullet != null) {
            final Vec3 vec = this.getViewVector(1F);
            final double deltaY = this.getDimensions(getPose()).height * 0.7F + shootSetting.offset().y;
            final double deltaX = shootSetting.offset().x * vec.x - shootSetting.offset().z * vec.z;
            final double deltaZ = shootSetting.offset().x * vec.z + shootSetting.offset().z * vec.x;
            bullet.setPos(this.getX() + deltaX, this.getY() + deltaY, this.getZ() + deltaZ);
            if (this.getTarget() != null) {
                bullet.shootToTarget(this, shootSetting, this.getTarget(), this.getTarget().getX() - bullet.getX(), this.getTarget().getY() + this.getTarget().getBbHeight() - bullet.getY(), this.getTarget().getZ() - bullet.getZ());
            } else {
                bullet.shootTo(this, shootSetting, vec);
            }
            if (!MinecraftForge.EVENT_BUS.post(new ShootBulletEvent(this, bullet))) {
                this.level().addFreshEntity(bullet);
            }
        }
    }

    public void gen(GenGoalSetting.GenSetting genSetting) {
        if (genSetting != null && this.getComponent() != null && this.getComponent().genGoalSetting().isPresent()) {
            for(int i = 0; i < genSetting.count(); ++ i)
            if (this.level() instanceof ServerLevel serverlevel && Level.isInSpawnableBounds(this.blockPosition())) {
                CompoundTag compoundtag = genSetting.nbt().copy();
                compoundtag.putString("id", EntityHelper.get().getKey(genSetting.entityType()).toString());
                final Vec3 position = this.getEyePosition().add(genSetting.offset());
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
                    entity.setDeltaMovement(new Vec3(dx, 0, dz).scale(genSetting.horizontalSpeed()).add(0, genSetting.verticalSpeed(), 0));
                    serverlevel.tryAddFreshEntityWithPassengers(entity);
                    this.getComponent().genGoalSetting().get().genSound().map(Holder::get).ifPresent(this::playSound);
                }
            }
        }
    }

    public void attack() {
        if (this.getComponent() != null && this.getComponent().attackGoalSetting().isPresent() && this.level() instanceof ServerLevel serverLevel) {
            if (this.getTarget() != null) {
                this.getComponent().attackGoalSetting().get().effect().get().effectTo(serverLevel, this, this.getTarget());
            } else {
                this.getComponent().attackGoalSetting().get().effect().get().effectTo(serverLevel, this, this.blockPosition());
            }
            this.getComponent().attackGoalSetting().get().attackSound().map(Holder::get).ifPresent(this::playSound);
        }
    }

    public void rangeEffect() {
        // 范围作用植物。
        if (this.getComponent() != null && this.level() instanceof ServerLevel serverLevel) {
            this.getComponent().constantAffectSettings().forEach(setting -> {
                if (this.tickCount % setting.cd() == 0) {
                    setting.targetFinder().get().getTargets(serverLevel, this).forEach(target -> {
                        setting.effect().get().effectTo(serverLevel, this, target);
                    });
                }
            });
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (super.hurt(source, amount)) {
            if (this.getComponent() != null  && this.level() instanceof ServerLevel serverLevel) {
                this.getComponent().hurtEffect().ifPresent(effect -> {
                    if (source.getEntity() != null) {
                        effect.get().effectTo(serverLevel, this, source.getEntity());
                    } else {
                        effect.get().effectTo(serverLevel, this, this.blockPosition());
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
        if (this.getComponent() != null  && this.level() instanceof ServerLevel serverLevel) {
            this.getComponent().dieEffect().ifPresent(effect -> {
                if (source.getEntity() != null) {
                    effect.get().effectTo(serverLevel, this, source.getEntity());
                } else {
                    effect.get().effectTo(serverLevel, this, this.blockPosition());
                }
            });
        }
    }

    protected PlayState idleOrMove(AnimationState<?> state) {
        if (this.isResting()) {
            return state.setAndContinue(OTDAnimations.REST);
        } else if (state.isMoving() && this.getComponent() != null && this.getComponent().movementSetting().isPresent()) {
            return state.setAndContinue(OTDAnimations.MOVE);
        } else {
            return state.setAndContinue(OTDAnimations.IDLE);
        }
    }

    protected PlayState predicateWorks(AnimationState<?> state) {
        if(this.getCurrentAnimation().isPresent() && this.ignoreWorkAnimation()){
            state.resetCurrentAnimation();
            return PlayState.STOP;
        } else {
            if(this.getComponent() != null && this.getComponent().towerSetting().customDeath() && this.isDeadOrDying()){
                return state.setAndContinue(OTDAnimations.DEAD);
            } else if (this.getShootTick() > 0 || this.hasActiveAttackTarget()) {
                return state.setAndContinue(OTDAnimations.SHOOT);
            } else if (this.getGenTick() > 0) {
                return state.setAndContinue(OTDAnimations.GEN);
            } else if (this.getAttackTick() > 0) {
                return state.setAndContinue(OTDAnimations.ATTACK);
            } else if (this.getInstantTick() > 0) {
                return state.setAndContinue(OTDAnimations.INSTANT);
            }
        }
        state.resetCurrentAnimation();
        return PlayState.STOP;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (ATTACK_TARGET.equals(accessor)) {
            this.clientSideAttackTime = 0;
            this.clientSideCachedAttackTarget = null;
        }
    }

    @Override
    protected void tickDeath() {
        if(this.getComponent() != null && this.getComponent().towerSetting().customDeath()){
            if(!this.level().isClientSide()){
                ++ this.deathTime; // Escape vanilla death effects.
                if (this.deathTime == this.getComponent().towerSetting().deathDuration()) {
                    this.level().broadcastEntityEvent(this, (byte)60);
                    this.remove(Entity.RemovalReason.KILLED);
                }
            }
        } else {
            super.tickDeath();
        }
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return getRenderSetting().dimension();
    }

    public abstract TowerComponent getComponent();

    public boolean canChangeDirection() {
        return true;
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

    public List<ShootGoalSetting.ShootSetting> getShootSettings() {
        if (this.getComponent() != null && this.getComponent().shootGoalSetting().isPresent()) {
            return this.getComponent().shootGoalSetting().get().shootSettings();
        }
        return List.of();
    }

    public List<GenGoalSetting.GenSetting> getGenSettings() {
        if (this.getComponent() != null && this.getComponent().genGoalSetting().isPresent()) {
            return this.getComponent().genGoalSetting().get().genSettings();
        }
        return List.of();
    }

    public LaserGoalSetting getLaserSetting() {
        return this.getComponent() == null ? null : this.getComponent().laserGoalSetting().orElse(null);
    }

    public <T> void parseComponent(Codec<T> codec, Consumer<T> consumer) {
        parseComponent(codec, consumer, msg -> {
            if (this.tickCount > 3) {
                Util.error(msg);
            }
        }, () -> {
            if (this.tickCount >= 5) {
                this.discard();
            }
        });
    }

    public float getAttackAnimationScale(LaserGoalSetting setting, float partialTick) {
        return ((float) this.clientSideAttackTime + partialTick) / (float) setting.duration();
    }

    @Override
    public void startSeenByPlayer(ServerPlayer serverPlayer) {
        super.startSeenByPlayer(serverPlayer);
        this.getBossEventOpt().ifPresent(bossEvent -> bossEvent.addPlayer(serverPlayer));
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer serverPlayer) {
        super.stopSeenByPlayer(serverPlayer);
        this.getBossEventOpt().ifPresent(bossEvent -> bossEvent.removePlayer(serverPlayer));
    }

    public Optional<ServerBossEvent> getBossEventOpt() {
        if (this.getComponent() != null && this.getComponent().bossBarSetting().isPresent()) {
            if (this.bossEvent == null) {
                BossBarSetting setting = this.getComponent().bossBarSetting().get();
                this.bossEvent = new ServerBossEvent(
                        setting.title().map(Component::translatable).map(Component.class::cast).orElse(this.getDisplayName()),
                        BossEvent.BossBarColor.byName(setting.color()),
                        BossEvent.BossBarOverlay.PROGRESS
                );
                this.bossEvent.setDarkenScreen(setting.darkenScreen());
                this.bossEvent.setPlayBossMusic(setting.playBossMusic());
                this.bossEvent.setCreateWorldFog(setting.createWorldFog());
            }
            return Optional.of(this.bossEvent);
        }
        return Optional.empty();
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @javax.annotation.Nullable
    public LivingEntity getOwner() {
        try {
            return this.getOwnerUUID().isEmpty() ? null : this.level().getPlayerByUUID(this.getOwnerUUID().get());
        } catch (IllegalArgumentException illegalargumentexception) {
            return null;
        }
    }

    @Override
    public Team getTeam() {
        if (this.shouldSyncTeam()) {
            LivingEntity livingentity = this.getOwner();
            if (livingentity != null) {
                return livingentity.getTeam();
            }
        }

        return super.getTeam();
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (this.shouldSyncTeam()) {
            LivingEntity owner = this.getOwner();
            if (entity == owner) {
                return true;
            }

            if (owner != null) {
                return owner.isAlliedTo(entity);
            }
        }

        return super.isAlliedTo(entity);
    }

    protected boolean shouldSyncTeam(){
        return this.getOwnerUUID().isPresent() && this.sameTeamWithOwner();
    }

    public abstract boolean sameTeamWithOwner();

    @Override
    public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
        if(this.getComponent() != null){
            if (type == ForgeMod.WATER_TYPE.get()){
                return this.getComponent().towerSetting().canRideInWater();
            }
        }
        return super.canBeRiddenUnderFluidType(type, rider);
    }

    @Override
    public boolean canDrownInFluidType(FluidType type) {
        if(this.getComponent() != null && type == ForgeMod.WATER_TYPE.get()){
            return ! this.getComponent().towerSetting().canBreathInWater();
        }
        return super.canDrownInFluidType(type);
    }

    @Override
    public boolean isPersistenceRequired() {
        return this.getComponent() != null ? this.getComponent().towerSetting().persistent() : super.isPersistenceRequired();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        {// save owner uuid.
            if (this.getOwnerUUID().isPresent()) {
                tag.putUUID("OwnerUUID", this.getOwnerUUID().get());
            }
        }
        tag.put("ComponentTag", this.componentTag);
        tag.putInt("ShootTick", this.getShootTick());
        tag.putInt("PreShootTick", this.preShootTick);
        tag.putInt("ShootCount", this.shootCount);
        tag.putInt("GenTick", this.getGenTick());
        tag.putInt("PreGenTick", this.preGenTick);
        tag.putInt("AttackTick", this.getAttackTick());
        tag.putInt("PreAttackTick", this.preAttackTick);
        tag.putInt("PreLaserTick", this.preLaserTick);
        tag.putInt("InstantTick", this.getInstantTick());
        tag.putBoolean("Resting", this.isResting());
        tag.putBoolean("IgnoreWorkAnimation", this.ignoreWorkAnimation);
        if (this.genSetting != null) {
            GenGoalSetting.GenSetting.CODEC.encodeStart(NbtOps.INSTANCE, this.genSetting)
                    .resultOrPartial(msg -> Util.error("Tower Entity write error : " + msg))
                    .ifPresent(nbt -> tag.put("Production", nbt));
        }
        this.getClientResource().saveTo(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        {// owner uuid.
            UUID ownerUuid;
            if (tag.hasUUID("OwnerUUID")) {
                ownerUuid = tag.getUUID("OwnerUUID");
            } else {
                String s1 = tag.getString("OwnerUUID");
                ownerUuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s1);
            }
            if (ownerUuid != null) {
                try {
                    this.setOwnerUUID(ownerUuid);
                } catch (Throwable var4) {
                }
            }
        }
        if (tag.contains("ComponentTag")) {
            this.componentTag = tag.getCompound("ComponentTag");
        }
        // 专门用于NBT召唤特定防御塔。
        if (tag.contains("ComponentLocation")) {
            final ResourceLocation location = new ResourceLocation(tag.getString("ComponentLocation"));
            final ResourceKey<ITowerComponent> resourceKey = OTDTowerComponents.registry().createKey(location);
            OTDTowerComponents.registry().getOptValue(level(), resourceKey).flatMap(l -> CodecHelper.encodeNbt(OTDTowerComponents.getDirectCodec(), l)
                    .resultOrPartial(msg -> Util.error("Tower entity read error : " + msg))
            ).ifPresent(nbt -> this.componentTag = (CompoundTag) nbt);
        }
        if (this.getComponent() != null) {
            tag.merge(this.getComponent().getExtraNBT());
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
        if (tag.contains("PreLaserTick")) {
            this.preLaserTick = tag.getInt("PreLaserTick");
        }
        if (tag.contains("InstantTick")) {
            this.setInstantTick(tag.getInt("InstantTick"));
        }
        if (tag.contains("Resting")) {
            this.setResting(tag.getBoolean("Resting"));
        }
        if(tag.contains("IgnoreWorkAnimation")) {
            this.setIgnoreWorkAnimation(tag.getBoolean("IgnoreWorkAnimation"));
        }
        if (tag.contains("Production")) {
            GenGoalSetting.GenSetting.CODEC.parse(NbtOps.INSTANCE, tag.get("Production"))
                    .resultOrPartial(msg -> Util.error("Tower entity read error : " + msg))
                    .ifPresent(settings -> this.genSetting = settings);
        }
        this.setClientResource(this.getClientResource().readFrom(tag));
    }

    public Optional<UUID> getOwnerUUID() {
        return this.entityData.get(OWNER_UUID);
    }

    public void setOwnerUUID(UUID uuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uuid));
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

    public void setActiveAttackTarget(int id) {
        this.entityData.set(ATTACK_TARGET, id);
    }

    public boolean hasActiveAttackTarget() {
        return this.entityData.get(ATTACK_TARGET) != 0;
    }

    @Override
    public ClientEntityResource getClientResource() {
        return entityData.get(CLIENT_RES);
    }

    @Override
    public void setClientResource(ClientEntityResource clientTowerResource) {
        entityData.set(CLIENT_RES, clientTowerResource);
    }

    public void setIgnoreWorkAnimation(boolean ignoreWorkAnimation) {
        this.ignoreWorkAnimation = ignoreWorkAnimation;
    }

    public boolean ignoreWorkAnimation() {
        return ignoreWorkAnimation;
    }

    @javax.annotation.Nullable
    public LivingEntity getActiveAttackTarget() {
        if (!this.hasActiveAttackTarget()) {
            return null;
        } else if (this.level().isClientSide) {
            if (this.clientSideCachedAttackTarget != null) {
                return this.clientSideCachedAttackTarget;
            } else {
                Entity entity = this.level().getEntity(this.entityData.get(ATTACK_TARGET));
                if (entity instanceof LivingEntity) {
                    this.clientSideCachedAttackTarget = (LivingEntity) entity;
                    return this.clientSideCachedAttackTarget;
                } else {
                    return null;
                }
            }
        } else {
            return this.getTarget();
        }
    }

    @Nullable
    public GenGoalSetting.GenSetting getProduction() {
        return this.genSetting;
    }

    public void setProduction(GenGoalSetting.GenSetting setting) {
        this.genSetting = setting;
    }

    @Override
    public void updateTowerComponent(CompoundTag tag) {
        this.componentTag.merge(tag);
        this.componentDirty = true;
        this.getComponent();
        this.updateComponent();
    }

    @Override
    public CompoundTag getComponentTag() {
        return componentTag;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                new AnimationController<>(
                        this,
                        "move_or_idle",
                        0,
                        this::idleOrMove
                ),
                new AnimationController<>(
                        this,
                        "works",
                        0,
                        this::predicateWorks
                ),
                new AnimationController<>(
                        this,
                        "specific",
                        0,
                        this::specificAnimation
                )
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


}

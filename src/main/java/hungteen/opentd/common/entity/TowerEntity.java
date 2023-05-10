package hungteen.opentd.common.entity;

import com.mojang.serialization.Codec;
import hungteen.htlib.util.helper.RandomHelper;
import hungteen.htlib.util.helper.registry.EntityHelper;
import hungteen.opentd.OpenTD;
import hungteen.opentd.common.codec.GenGoalSetting;
import hungteen.opentd.common.codec.ShootGoalSetting;
import hungteen.opentd.common.codec.TowerComponent;
import hungteen.opentd.common.entity.ai.*;
import hungteen.opentd.common.event.events.ShootBulletEvent;
import hungteen.opentd.common.impl.tower.HTTowerComponents;
import hungteen.opentd.util.EntityUtil;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-14 21:12
 **/
public abstract class TowerEntity extends PathfinderMob implements IAnimatable, IEntityAdditionalSpawnData {

    public static final String TOWER_SETTING = "TowerSettings";
    public static final String YROT = "YRot";
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(TowerEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> SHOOT_TICK = SynchedEntityData.defineId(TowerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> GEN_TICK = SynchedEntityData.defineId(TowerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ATTACK_TICK = SynchedEntityData.defineId(TowerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> INSTANT_TICK = SynchedEntityData.defineId(TowerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> RESTING = SynchedEntityData.defineId(TowerEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private CompoundTag componentTag = new CompoundTag();
    public int preShootTick = 0;
    public int preGenTick = 0;
    public int preAttackTick = 0;
    protected int shootCount = 0;
    public int growAnimTick = 0;
    private GenGoalSetting.GenSetting genSetting;
    private boolean updated = false;

    public TowerEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.refreshDimensions();
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
    }

    @Override
    protected void registerGoals() {
        if (this.getComponent() != null) {
            this.targetSelector.removeAllGoals();
            this.goalSelector.removeAllGoals();
            this.getComponent().targetSettings().forEach(targetSettings -> {
                this.targetSelector.addGoal(targetSettings.priority(), new TowerTargetGoal(this, targetSettings));
            });
            this.goalSelector.addGoal(2, new TowerShootGoal(this));
            this.goalSelector.addGoal(2, new TowerGenGoal(this));
            this.goalSelector.addGoal(1, new TowerAttackGoal(this));
            this.getComponent().movementSetting().ifPresent(movementSetting -> {
                if (movementSetting.canRandomMove()) {
                    this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
                }
                this.goalSelector.addGoal(1, new MoveToTargetGoal(this, movementSetting.speedModifier(), movementSetting.backwardPercent(), movementSetting.upwardPercent()));
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
        if (this.level instanceof ServerLevel) {
            // 延迟更新防御塔的行为。
            if (!this.updated && this.getComponent() != null) {
                this.updated = true;
                this.registerGoals();
            }
            // 距离灰烬植物。
            if (this.getComponent() != null && this.getTarget() != null) {
                this.getComponent().instantEffectSetting().ifPresent(l -> {
                    if (l.targetFilter().match((ServerLevel) this.level, this, this.getTarget()) && this.distanceTo(this.getTarget()) < l.closeRange()) {
                        if (this.getInstantTick() >= l.instantTick()) {
                            l.effect().effectTo((ServerLevel) this.level, this, this.getTarget());
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

    public void startShootAttack(LivingEntity target) {
        if (this.getComponent() != null) {
            this.getComponent().shootGoalSetting().ifPresent(l -> this.shootCount = l.shootCount());
            this.getComponent().shootGoalSetting().flatMap(ShootGoalSetting::shootSound).ifPresent(this::playSound);
        }
    }

    /**
     * attack pea with offsets.
     */
    public void performShoot(ShootGoalSetting.ShootSetting shootSetting) {
        final BulletEntity bullet = OpenTDEntities.BULLET_ENTITY.get().create(this.level);
        if (bullet != null) {
            final Vec3 vec = this.getViewVector(1F);
            final double deltaY = this.getDimensions(getPose()).height * 0.7F + shootSetting.offset().y;
            final double deltaX = shootSetting.offset().x * vec.x - shootSetting.offset().z * vec.z;
            final double deltaZ = shootSetting.offset().x * vec.z + shootSetting.offset().z * vec.x;
            bullet.setPos(this.getX() + deltaX, this.getY() + deltaY, this.getZ() + deltaZ);
            if (this.getTarget() != null && shootSetting.bulletSetting().lockToTarget()) {
                bullet.shootToTarget(this, shootSetting, this.getTarget(), this.getTarget().getX() - bullet.getX(), this.getTarget().getY() + this.getTarget().getBbHeight() - bullet.getY(), this.getTarget().getZ() - bullet.getZ());
            } else {
                bullet.shootTo(this, shootSetting, vec);
            }
            if (!MinecraftForge.EVENT_BUS.post(new ShootBulletEvent(this, bullet))) {
                this.level.addFreshEntity(bullet);
            }
        }
    }

    public void gen(GenGoalSetting.GenSetting genSetting) {
        if (genSetting != null && this.getComponent() != null && this.getComponent().genGoalSetting().isPresent()) {
            if (this.level instanceof ServerLevel serverlevel && Level.isInSpawnableBounds(this.blockPosition())) {
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
                    this.getComponent().genGoalSetting().get().genSound().ifPresent(this::playSound);
                }
            }
        }
    }

    public void attack() {
        if (this.getComponent() != null && this.getComponent().attackGoalSetting().isPresent() && this.level instanceof ServerLevel) {
            if (this.getTarget() != null) {
                this.getComponent().attackGoalSetting().get().effect().effectTo((ServerLevel) this.level, this, this.getTarget());
            } else {
                this.getComponent().attackGoalSetting().get().effect().effectTo((ServerLevel) this.level, this, this.blockPosition());
            }
            this.getComponent().attackGoalSetting().get().attackSound().ifPresent(this::playSound);
        }
    }

    public void rangeEffect() {
        // 范围作用植物。
        if (this.getComponent() != null) {
            this.getComponent().constantAffectSettings().forEach(setting -> {
                if (this.tickCount % setting.cd() == 0) {
                    setting.targetFinder().getTargets((ServerLevel) this.level, this).forEach(target -> {
                        setting.effect().effectTo((ServerLevel) this.level, this, target);
                    });
                }
            });
        }
    }

    protected PlayState predicateAnimation(AnimationEvent<?> event) {
        final AnimationBuilder builder = new AnimationBuilder();
        if (event.isMoving() && this.getComponent() != null && this.getComponent().movementSetting().isPresent()) {
            builder.addAnimation("move", ILoopType.EDefaultLoopTypes.LOOP);
        }
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

    public <T> void parseComponent(Codec<T> codec, Consumer<T> consumer) {
        codec.parse(NbtOps.INSTANCE, this.componentTag)
                .resultOrPartial(msg -> {
                    if (this.tickCount > 0) {
                        OpenTD.log().error(msg);
                    }
                })
                .ifPresentOrElse(consumer, () -> {
                    if (this.tickCount >= 5) {
                        this.discard();
                    }
                });
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
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
        tag.putInt("InstantTick", this.getInstantTick());
        tag.putBoolean("Resting", this.isResting());
        if (this.genSetting != null) {
            GenGoalSetting.GenSetting.CODEC.encodeStart(NbtOps.INSTANCE, this.genSetting)
                    .resultOrPartial(msg -> OpenTD.log().error(msg + " [Plant Gen]"))
                    .ifPresent(nbt -> tag.put("Production", nbt));
        }
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
            HTTowerComponents.TOWERS.getValue(location).flatMap(l -> HTTowerComponents.getCodec().encodeStart(NbtOps.INSTANCE, l)
                    .resultOrPartial(msg -> OpenTD.log().error(msg + " [Read Tower]"))).ifPresent(nbt -> this.componentTag = (CompoundTag) nbt);
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
        if (tag.contains("InstantTick")) {
            this.setInstantTick(tag.getInt("InstantTick"));
        }
        if (tag.contains("Resting")) {
            this.setResting(tag.getBoolean("Resting"));
        }
        if (tag.contains("Production")) {
            GenGoalSetting.GenSetting.CODEC.parse(NbtOps.INSTANCE, tag.get("Production"))
                    .resultOrPartial(msg -> OpenTD.log().error(msg + " [Plant Gen]"))
                    .ifPresent(settings -> this.genSetting = settings);
        }
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

    @Nullable
    public GenGoalSetting.GenSetting getProduction() {
        return this.genSetting;
    }

    public void setProduction(GenGoalSetting.GenSetting setting) {
        this.genSetting = setting;
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
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}

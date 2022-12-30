package hungteen.opentd.common.entity;

import hungteen.htlib.util.helper.EntityHelper;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.RandomHelper;
import hungteen.opentd.OpenTD;
import hungteen.opentd.common.entity.ai.PlantShootGoal;
import hungteen.opentd.common.entity.ai.PlantTargetGoal;
import hungteen.opentd.impl.HTSummonItems;
import hungteen.opentd.impl.tower.HTTowerComponents;
import hungteen.opentd.impl.tower.PVZPlantComponent;
import hungteen.opentd.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;
import sun.security.provider.Sun;

import java.util.Arrays;
import java.util.List;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-14 21:10
 **/
public class PlantEntity extends TowerEntity {

    public static final String YROT = "YRot";
    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(PlantEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> SHOOT_TICK = SynchedEntityData.defineId(PlantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> GEN_TICK = SynchedEntityData.defineId(PlantEntity.class, EntityDataSerializers.INT);
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private CompoundTag componentTag = new CompoundTag();
    private PVZPlantComponent component;
    private PVZPlantComponent.GenSettings genSettings;
    protected int growTick = 0;
    public int preGenTick = 0;
    protected int forcedAgeTimer;
    private boolean updated = false;
    protected int shootCount = 0;

    public PlantEntity(EntityType<? extends TowerEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(AGE, 1);
        entityData.define(SHOOT_TICK, 0);
        entityData.define(GEN_TICK, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if(dataAccessor.equals(AGE)) {
            this.refreshDimensions();
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance instance, MobSpawnType spawnType, @Nullable SpawnGroupData groupData, @Nullable CompoundTag compoundTag) {
        if (compoundTag != null) {
            HTSummonItems.SUMMON_ITEMS.getCodec().parse(NbtOps.INSTANCE, compoundTag.getCompound("SummonEntry"))
                    .resultOrPartial(msg -> OpenTD.log().error(msg))
                    .flatMap(entry -> HTTowerComponents.getCodec()
                            .encodeStart(NbtOps.INSTANCE, entry.towerSettings())
                            .resultOrPartial(msg -> OpenTD.log().error(msg))
                    ).ifPresent(tag -> {
                        this.componentTag = (CompoundTag) tag;
                    });
            if(compoundTag.contains(YROT)){
                this.setYRot(Direction.fromYRot(compoundTag.getFloat(YROT)).toYRot());
                this.yHeadRot = this.getYRot();
                this.yBodyRot = this.getYRot();
            }
        }
        return super.finalizeSpawn(accessor, instance, spawnType, groupData, compoundTag);
    }

    @Override
    protected void registerGoals() {
        if(this.getComponent() != null){
            this.targetSelector.removeAllGoals();
            this.goalSelector.removeAllGoals();
            this.getComponent().targetSettings().forEach(targetSettings -> {
                this.targetSelector.addGoal(targetSettings.priority(), new PlantTargetGoal(this, targetSettings));
            });
            this.goalSelector.addGoal(1, new PlantShootGoal(this));
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
        if(this.getComponent() != null && this.tickCount <= 5){
            this.refreshDimensions();
        }
        if (this.level.isClientSide) {
            if (this.forcedAgeTimer > 0) {
                if (this.forcedAgeTimer % 4 == 0) {
                    this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
                }
                --this.forcedAgeTimer;
            }
        } else{
            if(! this.updated && this.getComponent() != null){
                this.updated = true;
                this.registerGoals();
            }
            if(this.isAlive()){
                if(this.canGrow()){
                    if(this.growTick >= this.getGrowNeedTime()){
                        this.onGrow();
                    }
                    ++ this.growTick;
                }
            }
            if(EntityUtil.inEnergetic(this)){
                this.getShootSettings().stream().filter(PVZPlantComponent.ShootSettings::plantFoodOnly).forEach(this::performShoot);
            }
            if(this.getTarget() != null) {
                if (! this.getShootSettings().isEmpty() && this.shootCount > 0 && ! EntityUtil.inEnergetic(this)) {
                    final int count = this.getComponent().shootGoalSettings().get().shootCount();
                    this.getShootSettings().stream().filter(l -> !l.plantFoodOnly() && l.shootTick() == count - this.shootCount).forEach(this::performShoot);
                    -- this.shootCount;
                }
            }
        }
    }

    protected PlayState predicateAnimation(AnimationEvent<?> event){
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.opentd.pea_shooter.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    public void startShootAttack(LivingEntity target){
        if(this.getComponent() != null){
            this.getComponent().shootGoalSettings().ifPresent(l -> this.shootCount = l.shootCount());
            this.getComponent().shootGoalSettings().flatMap(PVZPlantComponent.ShootGoalSettings::shootSound).ifPresent(this::playSound);
        }
    }

    /**
     * shoot pea with offsets.
     */
    public void performShoot(PVZPlantComponent.ShootSettings shootSettings) {
        final BulletEntity bullet = OpenTDEntities.BULLET_ENTITY.get().create(this.level);
        if(bullet != null){
            final Vec3 vec = this.getViewVector(1F);
            final double deltaY = this.getDimensions(getPose()).height * 0.7F + shootSettings.offset().y;
            final double deltaX = shootSettings.offset().x * vec.x - shootSettings.offset().z * vec.z;
            final double deltaZ = shootSettings.offset().x * vec.z + shootSettings.offset().z * vec.x;
            bullet.setPos(this.getX() + deltaX, this.getY() + deltaY, this.getZ() + deltaZ);
            if(this.getTarget() != null){
                bullet.shootToTarget(this, shootSettings, this.getTarget(), this.getTarget().getX() - bullet.getX(), this.getTarget().getY() + this.getTarget().getBbHeight() - bullet.getY(), this.getTarget().getZ() - bullet.getZ());
            } else{
                bullet.shootTo(this, shootSettings, vec);
            }
            this.level.addFreshEntity(bullet);
        }
    }

    public void gen(PVZPlantComponent.GenSettings genSettings){
        if(genSettings != null){
            if (this.level instanceof ServerLevel serverlevel && Level.isInSpawnableBounds(this.blockPosition())) {
                CompoundTag compoundtag = genSettings.nbt().copy();
                compoundtag.putString("id", genSettings.entityType().toString());
                final Vec3 position = this.getEyePosition().add(genSettings.offset());
                Entity entity = EntityType.loadEntityRecursive(compoundtag, serverlevel, (e) -> {
                    e.moveTo(position.x() , position.y(), position.z(), this.getYRot(), this.getXRot());
                    return e;
                });
                if (entity != null) {
                    if (entity instanceof Mob) {
                        ((Mob)entity).finalizeSpawn(serverlevel, serverlevel.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                    }
                    final double dx = RandomHelper.floatRange(this.getRandom());
                    final double dz = RandomHelper.floatRange(this.getRandom());
                    entity.setDeltaMovement(new Vec3(dx, 0, dz).scale(genSettings.horizontalSpeed()).add(0, genSettings.verticalSpeed(), 0));
                    serverlevel.tryAddFreshEntityWithPassengers(entity);
                }
            }
        }
    }

    public void onGrow(){
        this.setAge(this.getAge() + 1);
        this.growTick = 0;
    }

    public int getMaxAge(){
        return this.getComponent() == null ? 1 : this.getComponent().plantSettings().growSettings().getMaxAge();
    }

    public boolean canGrow(){
        return this.getAge() < this.getMaxAge();
    }

    public int getGrowNeedTime(){
        return this.getComponent().plantSettings().growSettings().growDurations().get(this.getAge() - 1);
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
//        if(this.getComponent() != null && ! this.getComponent().plantSettings().changeDirection()){
//            if(entity.level != this.level){
//                return false;
//            } else{
//                Vec3 vec = entity.getEyePosition().subtract(this.getEyePosition());
//                if(vec.length() > 128 || vec.length() < 0.01){
//                    return false;
//                }
//                double cos = (Math.acos(vec.normalize().dot(this.getLookAngle().normalize())) + 180) % 180;
//                return cos < this.getComponent().plantSettings().senseAngle();
//            }
//        }
        return super.hasLineOfSight(entity);
    }

    public PVZPlantComponent getComponent() {
        if (component == null) {
            PVZPlantComponent.CODEC.parse(NbtOps.INSTANCE, this.componentTag)
                    .resultOrPartial(msg -> {
                        if(this.tickCount > 0) {
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

    public int getCurrentShootCD(){
        if(this.getComponent() != null && this.getComponent().shootGoalSettings().isPresent()) {
            return this.getComponent().shootGoalSettings().get().coolDown();
        }
        return 1000000;
    }

    public int getStartShootTick(){
        if(this.getComponent() != null && this.getComponent().shootGoalSettings().isPresent()) {
            return this.getComponent().shootGoalSettings().get().startTick();
        }
        return 1000000;
    }

    public int getCurrentGenCD(){
        if(this.getComponent() != null && this.getComponent().genGoalSettings().isPresent()) {
            return this.getComponent().genGoalSettings().get().coolDown();
        }
        return 1000000;
    }

    public int getStartGenTick(){
        if(this.getComponent() != null && this.getComponent().genGoalSettings().isPresent()) {
            return this.getComponent().genGoalSettings().get().startTick();
        }
        return 1000000;
    }

    public List<PVZPlantComponent.ShootSettings> getShootSettings() {
        if(this.getComponent() != null && this.getComponent().shootGoalSettings().isPresent()) {
            return this.getComponent().shootGoalSettings().get().shootSettings();
        }
        return Arrays.asList();
    }

    public List<PVZPlantComponent.GenSettings> getGenSettings() {
        if(this.getComponent() != null && this.getComponent().genGoalSettings().isPresent()) {
            return this.getComponent().genGoalSettings().get().genSettings();
        }
        return Arrays.asList();
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        final float width = getRenderSettings().width();
        final float height = getRenderSettings().height();
        final float scale = getGrowSettings().scales().get(this.getAge() - 1) * getRenderSettings().scale();
        return EntityDimensions.scalable(width * scale, height * scale);
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
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("ComponentTag", this.componentTag);
        tag.putInt("CreatureAge", this.getAge());
        tag.putInt("CreatureGrowTick", this.growTick);
        tag.putInt("ShootTick", this.getShootTick());
        tag.putInt("ShootCount", this.shootCount);
        tag.putInt("GenTick", this.getGenTick());
        tag.putInt("PreGenTick", this.preGenTick);
        if(this.genSettings != null){
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
        if(tag.contains("CreatureAge")){
            this.setAge(tag.getInt("CreatureAge"));
        }
        if(tag.contains("CreatureGrowTick")){
            this.growTick = tag.getInt("CreatureGrowTick");
        }
        if(tag.contains("ShootTick")){
            this.setShootTick(tag.getInt("ShootTick"));
        }
        if(tag.contains("ShootCount")) {
            this.shootCount = tag.getInt("ShootCount");
        }
        if(tag.contains("GenTick")){
            this.setGenTick(tag.getInt("GenTick"));
        }
        if(tag.contains("PreGenTick")){
            this.preGenTick = tag.getInt("PreGenTick");
        }
        if(tag.contains("Production")){
            PVZPlantComponent.GenSettings.CODEC.parse(NbtOps.INSTANCE, tag.get("Production"))
                    .resultOrPartial(msg -> OpenTD.log().error(msg + " [Plant Gen]"))
                    .ifPresent(settings -> this.genSettings = settings);
        }
    }

    public PVZPlantComponent.GrowSettings getGrowSettings() {
        return this.getComponent() == null ? PVZPlantComponent.GrowSettings.DEFAULT : this.getComponent().plantSettings().growSettings();
    }

    public PVZPlantComponent.RenderSettings getRenderSettings() {
        return this.getComponent() == null ? PVZPlantComponent.RenderSettings.DEFAULT : this.getComponent().plantSettings().renderSettings();
    }

    @Nullable
    public PVZPlantComponent.GenSettings getProduction() {
        return this.genSettings;
    }

    public void setProduction(PVZPlantComponent.GenSettings settings){
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
}

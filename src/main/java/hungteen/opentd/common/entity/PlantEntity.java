package hungteen.opentd.common.entity;

import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.IRangeAttackEntity;
import hungteen.opentd.common.entity.ai.TowerTargetGoal;
import hungteen.opentd.common.item.OpenTDItems;
import hungteen.opentd.common.item.SummonTowerItem;
import hungteen.opentd.impl.HTSummonItems;
import hungteen.opentd.impl.tower.HTTowerComponents;
import hungteen.opentd.impl.tower.PVZPlantComponent;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-14 21:10
 **/
public class PlantEntity extends TowerEntity implements IRangeAttackEntity {

    public static final String YROT = "YRot";
    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(PlantEntity.class, EntityDataSerializers.INT);

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private CompoundTag componentTag = new CompoundTag();
    private PVZPlantComponent component;
    protected int growTick = 0;
    protected int forcedAgeTimer;
    private boolean updated = false;

    public PlantEntity(EntityType<? extends TowerEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(AGE, 1);
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
                this.targetSelector.addGoal(targetSettings.priority(), new TowerTargetGoal(this, targetSettings));
            });
            this.getComponent().workSettings().forEach(workSettings -> {
                Optional.ofNullable(workSettings.createGoal(this)).ifPresent(l -> this.goalSelector.addGoal(1, l));
            });
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
        }
    }

    protected PlayState predicateAnimation(AnimationEvent<?> event){
        event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.opentd.pea_shooter.idle", ILoopType.EDefaultLoopTypes.LOOP));
        return PlayState.CONTINUE;
    }

    /**
     * shoot pea with offsets.
     */
    public void performShoot(double forwardOffset, double rightOffset, double heightOffset, boolean needSound, double angleOffset) {
        Optional.ofNullable(this.getTarget()).ifPresent(target -> {
            final Vec3 vec = MathUtil.getHorizontalVec(this.position(), target.position()).normalize();
            final double deltaY = this.getDimensions(getPose()).height * 0.7F + heightOffset;
            final double deltaX = forwardOffset * vec.x - rightOffset * vec.z;
            final double deltaZ = forwardOffset * vec.z + rightOffset * vec.x;
            final PVZProjectile bullet = this.createBullet();
            bullet.setPos(this.getX() + deltaX, this.getY() + deltaY, this.getZ() + deltaZ);
            bullet.shootPea(target.getX() - bullet.getX(), target.getY() + target.getBbHeight() - bullet.getY(), target.getZ() - bullet.getZ(), this.getBulletSpeed(), angleOffset);
            if(needSound) {
                EntityUtil.playSound(this, this.getShootSound());
            }
            bullet.summonByOwner(this);
            bullet.setAttackDamage(this.getAttackDamage());
            this.level.addFreshEntity(bullet);
        });
    }

    public void onGrow(){
        this.setAge(this.getAge() + 1);
        this.growTick = 0;
    }

    public int getMaxAge(){
        return this.getComponent().plantSettings().growSettings().getMaxAge();
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

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        final float width = getGrowSettings().width();
        final float height = getGrowSettings().height();
        final float scale = getGrowSettings().scales().get(this.getAge() - 1);
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
    }

    public PVZPlantComponent.GrowSettings getGrowSettings() {
        return this.getComponent() == null ? PVZPlantComponent.GrowSettings.DEFAULT : this.getComponent().plantSettings().growSettings();
    }

    public void setAge(int age) {
        entityData.set(AGE, age);
    }

    public int getAge() {
        return entityData.get(AGE);
    }
}

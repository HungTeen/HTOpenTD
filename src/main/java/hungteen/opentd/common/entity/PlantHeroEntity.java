package hungteen.opentd.common.entity;

import hungteen.opentd.common.codec.RenderSetting;
import hungteen.opentd.common.entity.ai.TowerMoveToGoal;
import hungteen.opentd.common.impl.tower.PlantHeroComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-05-10 21:46
 **/
public class PlantHeroEntity extends TowerEntity {

    private PlantHeroComponent component;
    private BlockPos moveTo = null;

    public PlantHeroEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TowerEntity.createAttributes()
                .add(Attributes.MAX_HEALTH, 50D)
                .add(Attributes.ATTACK_DAMAGE, 4D)
                .add(Attributes.ATTACK_KNOCKBACK, 0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0D)
                .add(Attributes.FOLLOW_RANGE, 40D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new TowerMoveToGoal(this));
    }

    @Override
    public PlantHeroComponent getComponent() {
        if (component == null || this.componentDirty) {
            this.parseComponent(PlantHeroComponent.CODEC, t -> this.component = t);
            this.componentDirty = false;
        }
        return component;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.getMoveTo().ifPresent(pos -> tag.putLong("MoveToPos", pos.asLong()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if(tag.contains("MoveToPos")){
            this.setMoveTo(BlockPos.of(tag.getLong("MoveToPos")));
        }
    }

    @Override
    public boolean sameTeamWithOwner() {
        return getComponent() != null && getComponent().heroSetting().sameTeamWithOwner();
    }

    @Override
    public RenderSetting getRenderSetting() {
        return getComponent() != null ? getComponent().heroSetting().renderSetting() : RenderSetting.DEFAULT;
    }

    public Optional<BlockPos> getMoveTo() {
        return Optional.ofNullable(moveTo);
    }

    public void setMoveTo(BlockPos moveTo) {
        this.moveTo = moveTo;
    }

}

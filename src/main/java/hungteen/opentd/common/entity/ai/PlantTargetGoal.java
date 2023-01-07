package hungteen.opentd.common.entity.ai;

import hungteen.htlib.util.helper.EntityHelper;
import hungteen.opentd.common.entity.PlantEntity;
import hungteen.opentd.impl.tower.PVZPlantComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.List;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 18:44
 **/
public class PlantTargetGoal extends Goal {

    protected final PlantEntity plantEntity;
    private final PVZPlantComponent.TargetSetting targetSettings;
    protected LivingEntity targetMob;

    public PlantTargetGoal(PlantEntity towerEntity, PVZPlantComponent.TargetSetting targetSettings) {
        this.plantEntity = towerEntity;
        this.targetSettings = targetSettings;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.plantEntity.getRandom().nextFloat() < this.targetSettings.chance()) {
            return false;
        }
        List<LivingEntity> targets = this.targetSettings.targetFinder().getLivings(this.plantEntity.level, this.plantEntity);
        final LivingEntity target = this.chooseTarget(targets);
        if (target != null) {
            this.targetMob = target;
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        this.plantEntity.setTarget(this.targetMob);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity entity = this.plantEntity.getTarget();
        //alternative target not exist.
        if (!EntityHelper.isEntityValid(this.targetMob)) {
            return false;
        }
        if (!EntityHelper.isEntityValid(entity)) {
            entity = this.targetMob;
        } else if (!entity.is(this.targetMob)) {
            this.targetMob = entity;
        }
        if (!EntityHelper.isEntityValid(entity)) {
            return false;
        }
        //already out range.
        if (this.targetSettings.targetFinder().stillValid(this.plantEntity.level, this.plantEntity, entity)) {
            this.plantEntity.setTarget(entity);
            return true;
        }
        return false;
    }

    @Override
    public void stop() {
        this.plantEntity.setTarget(null);
    }

    protected LivingEntity chooseTarget(List<LivingEntity> list) {
        return list.isEmpty() ? null : list.get(0);
    }

}

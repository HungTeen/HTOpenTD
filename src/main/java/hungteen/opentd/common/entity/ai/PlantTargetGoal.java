package hungteen.opentd.common.entity.ai;

import hungteen.htlib.util.algorithm.SortHelper;
import hungteen.htlib.util.helper.EntityHelper;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.RandomHelper;
import hungteen.opentd.common.entity.PlantEntity;
import hungteen.opentd.impl.tower.PVZPlantComponent;
import net.minecraft.server.level.ServerLevel;
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
    protected final SortHelper.EntitySorter sorter;
    protected LivingEntity targetMob;
    private long refreshTick = 0;


    public PlantTargetGoal(PlantEntity towerEntity, PVZPlantComponent.TargetSetting targetSettings) {
        this.plantEntity = towerEntity;
        this.targetSettings = targetSettings;
        this.sorter = new SortHelper.EntitySorter(towerEntity);
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.plantEntity.getRandom().nextFloat() < this.targetSettings.chance()) {
            return false;
        }
        List<LivingEntity> targets = this.targetSettings.targetFinder().getLivings((ServerLevel) this.plantEntity.level, this.plantEntity);
        final LivingEntity target = this.chooseTarget(targets);
        if (target != null) {
            this.targetMob = target;
            this.refreshTick = this.targetMob.level.getGameTime() + this.targetSettings.refreshCD();
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
        if (this.targetSettings.targetFinder().stillValid((ServerLevel) this.plantEntity.level, this.plantEntity, entity)) {
            if(this.refreshTick < this.plantEntity.level.getGameTime()){
                return false;
            }
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
        if(this.targetSettings.closest()){
            list.sort(this.sorter);
            return list.isEmpty() ? null : list.get(0);
        } else{
            if(list.size() > 0){
                final int pos = this.plantEntity.getRandom().nextInt(list.size());
                return list.get(pos);
            }
        }
        return null;
    }

}

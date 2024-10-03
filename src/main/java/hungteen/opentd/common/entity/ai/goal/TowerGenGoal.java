package hungteen.opentd.common.entity.ai.goal;

import hungteen.htlib.util.WeightedList;
import hungteen.opentd.common.codec.GenGoalSetting;
import hungteen.opentd.common.entity.TowerEntity;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-29 23:26
 **/
public class TowerGenGoal extends HTGoal {

    private final TowerEntity towerEntity;

    public TowerGenGoal(TowerEntity entity) {
        this.towerEntity = entity;
    }

    @Override
    public boolean canUse() {
        if (this.towerEntity.getComponent() == null || this.towerEntity.getGenSettings().isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
        if (this.towerEntity.preGenTick <= 0) {
            if (this.towerEntity.getProduction() != null) {
                this.produce();
            } else {
                this.chooseProduction();
            }
        } else {
            --this.towerEntity.preGenTick;
        }
        if(this.towerEntity.getComponent().genGoalSetting().get().needRest()){
            this.towerEntity.setResting(this.towerEntity.preGenTick > 0);
        }
    }

    protected void produce(){
        final int time = this.towerEntity.getGenTick();
        if(time >= this.towerEntity.getCurrentGenCD()){
            this.towerEntity.setGenTick(0);
            this.chooseProduction();
        } else{
            if (time == this.towerEntity.getStartGenTick()) {
                this.towerEntity.gen(this.towerEntity.getProduction());
            }
            this.towerEntity.setGenTick(time + 1);
        }
    }

    protected void chooseProduction(){
        final int weight = this.towerEntity.getComponent().genGoalSetting().get().totalWeight();
        final WeightedList.Builder<GenGoalSetting.GenSetting> builder = new WeightedList.Builder<>();
        this.towerEntity.getGenSettings().stream()
                .filter(l -> !l.plantFoodOnly()).forEach(builder::add);
        builder.weight(weight);
        builder.build().getRandomItem(this.towerEntity.getRandom()).ifPresent(this.towerEntity::setProduction);
        if(this.towerEntity.getProduction() != null){
            this.towerEntity.preGenTick = this.towerEntity.getProduction().cooldown();
        } else{
            this.towerEntity.preGenTick = this.towerEntity.getComponent().genGoalSetting().get().emptyCD();
        }
    }
}

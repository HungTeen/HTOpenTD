package hungteen.opentd.common.entity.ai;

import hungteen.htlib.util.WeightList;
import hungteen.opentd.common.entity.PlantEntity;
import hungteen.opentd.impl.tower.PVZPlantComponent;
import hungteen.opentd.util.EntityUtil;

import java.util.stream.Collectors;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-29 23:26
 **/
public class PlantGenGoal extends HTGoal {

    private final PlantEntity plantEntity;

    public PlantGenGoal(PlantEntity entity) {
        this.plantEntity = entity;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return true;
    }

    @Override
    public void stop() {
    }

    @Override
    public void tick() {
        if (this.plantEntity.getComponent() == null || this.plantEntity.getGenSettings().isEmpty()) {
            return;
        }
        if (this.plantEntity.preGenTick <= 0) {
            if (this.plantEntity.getProduction() != null) {
                this.produce();
            } else {
                this.chooseProduction();
            }
        } else {
            --this.plantEntity.preGenTick;
        }
    }

    protected void produce(){
        final int time = this.plantEntity.getGenTick();
        if(time >= this.plantEntity.getCurrentGenCD()){
            this.plantEntity.setGenTick(0);
            this.chooseProduction();
        } else{
            if (time == this.plantEntity.getStartGenTick()) {
                this.plantEntity.gen(this.plantEntity.getProduction());
            }
            this.plantEntity.setGenTick(time + 1);
        }
    }

    protected void chooseProduction(){
        final int weight = this.plantEntity.getComponent().genGoalSettings().get().totalWeight();
        final int totalWeight = this.plantEntity.getGenSettings().stream().filter(l -> !l.plantFoodOnly()).map(PVZPlantComponent.GenSettings::weight).reduce(0, Integer::sum);
        WeightList<PVZPlantComponent.GenSettings> list = new WeightList<>(this.plantEntity.getGenSettings().stream().filter(l -> !l.plantFoodOnly()).collect(Collectors.toList()), PVZPlantComponent.GenSettings::weight);
        list.setTotalWeight(Math.max(weight, totalWeight));
        list.getRandomItem(this.plantEntity.getRandom()).ifPresent(this.plantEntity::setProduction);
        if(this.plantEntity.getProduction() != null){
            this.plantEntity.preGenTick = this.plantEntity.getProduction().cooldown();
        } else{
            this.plantEntity.preGenTick = this.plantEntity.getComponent().genGoalSettings().get().emptyCD();
        }
    }
}

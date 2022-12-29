package hungteen.opentd.common.entity.ai;

import hungteen.opentd.common.entity.PlantEntity;
import hungteen.opentd.util.EntityUtil;

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
        if(this.plantEntity.getComponent() == null || this.plantEntity.getGenSettings().isEmpty()){
            return ;
        }
//        final int time = this.plantEntity.getGenTick();
//        if(time >= this.plantEntity.getCurrentWorkCD()){
//            this.plantEntity.genSomething();
//            this.plantEntity.setGenTick(0);
//        } else{
//            this.plantEntity.setGenTick(time + 1);
//        }
    }
}

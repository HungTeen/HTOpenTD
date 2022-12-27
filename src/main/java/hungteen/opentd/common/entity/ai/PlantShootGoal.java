package hungteen.opentd.common.entity.ai;

import hungteen.htlib.util.helper.EntityHelper;
import hungteen.opentd.common.entity.PlantEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-20 12:46
 **/
public class PlantShootGoal extends HTGoal {

    protected final PlantEntity plantEntity;
    protected LivingEntity target;

    public PlantShootGoal(PlantEntity attacker) {
        this.plantEntity = attacker;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if(this.plantEntity.getComponent() == null || this.plantEntity.getShootSettings().isEmpty()){
            return false;
        }
//        if (!this.plantEntity.canAttack()) {//can not shoot because of the attacker itself.
//            this.plantEntity.setAttackTick(0);
//            return false;
//        }
        this.target = this.plantEntity.getTarget();
        if (!this.checkTarget()) {//can not shoot because of its target(such as height limit).
            this.target = null;
            this.plantEntity.setTarget(null);
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void stop() {
        this.plantEntity.setShootTick(0);
    }

    @Override
    public void tick() {
        if(this.plantEntity.getComponent().plantSettings().changeDirection()){
            this.plantEntity.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        }
        final int time = this.plantEntity.getShootTick();
        if (time >= this.plantEntity.getCurrentShootCD()) {
            this.plantEntity.setShootTick(0);
        } else {
            if (time == this.plantEntity.getStartShootTick()) {
                this.plantEntity.startShootAttack(this.target);
            }
            this.plantEntity.setShootTick(time + 1);
        }
    }

    protected boolean checkTarget() {
        return EntityHelper.isEntityValid(this.target) && this.plantEntity.canAttack(this.target) && this.plantEntity.getSensing().hasLineOfSight(this.target);
    }

}

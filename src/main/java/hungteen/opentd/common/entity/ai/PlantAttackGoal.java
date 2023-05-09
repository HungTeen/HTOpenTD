package hungteen.opentd.common.entity.ai;

import hungteen.htlib.util.helper.registry.EntityHelper;
import hungteen.opentd.common.entity.PlantEntity;
import hungteen.opentd.common.impl.tower.PVZPlantComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Objects;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-07 13:48
 **/
public class PlantAttackGoal  extends HTGoal {

    protected final PlantEntity plantEntity;
    protected LivingEntity target;

    public PlantAttackGoal(PlantEntity attacker) {
        this.plantEntity = attacker;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.setting() == null) {
            return false;
        }
//        if (!this.plantEntity.canAttack()) {//can not attack because of the attacker itself.
//            this.plantEntity.setAttackTick(0);
//            return false;
//        }
        this.target = this.plantEntity.getTarget();
        if (!this.checkTarget()) {//can not attack because of its target(such as height limit).
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
        this.plantEntity.setAttackTick(0);
    }

    @Override
    public void tick() {
        if (this.plantEntity.getComponent().plantSetting().changeDirection()) {
            this.plantEntity.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        }
        if (this.plantEntity.preAttackTick <= 0) {
            this.attack();
        } else {
            --this.plantEntity.preAttackTick;
        }
        if (Objects.requireNonNull(this.setting()).needRest()) {
            this.plantEntity.setResting(this.plantEntity.preAttackTick > 0);
        }
    }

    protected void attack() {
        final int time = this.plantEntity.getAttackTick();
        if (time >= this.plantEntity.getCurrentAttackCD()) {
            this.plantEntity.setAttackTick(0);
            this.plantEntity.preAttackTick = Objects.requireNonNull(this.setting()).duration();
        } else {
            if (time == this.plantEntity.getStartAttackTick()) {
                this.plantEntity.attack();
            }
            this.plantEntity.setAttackTick(time + 1);
        }
    }

    private PVZPlantComponent.AttackGoalSetting setting(){
        return this.plantEntity.getComponent() == null ? null : this.plantEntity.getComponent().attackGoalSetting().orElse(null);
    }

    protected boolean checkTarget() {
        return EntityHelper.isEntityValid(this.target)
                && this.plantEntity.canAttack(this.target)
                && this.plantEntity.getSensing().hasLineOfSight(this.target)
                && this.plantEntity.distanceTo(this.target) <= Objects.requireNonNull(this.setting()).distance();
    }
}
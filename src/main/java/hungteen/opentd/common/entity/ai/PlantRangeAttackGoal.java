package hungteen.opentd.common.entity.ai;

import hungteen.opentd.api.interfaces.IRangeAttackEntity;
import hungteen.opentd.common.entity.PlantEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-20 12:46
 **/
public class PlantRangeAttackGoal extends HTGoal {

    protected final PlantEntity attacker;
    protected LivingEntity target;

    public PlantRangeAttackGoal(PlantEntity attacker) {
        this.attacker = attacker;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        //can not shoot because of the attacker itself.
        if (!this.attacker.canAttack()) {
            this.attacker.setAttackTick(0);
            return false;
        }
        this.target = this.attacker.getTarget();
        if (!this.checkTarget()) {//can not shoot because of its target(such as height limit).
            this.target = null;
            this.attacker.setTarget(null);
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
        this.attacker.setAttackTick(0);
    }

    @Override
    public void tick() {
//            if(! (this.shooter instanceof StarFruitEntity)) {//star fruit don't need to look at target.
        this.attacker.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
//            }
        final int time = this.attacker.getAttackTick();
        if (time >= this.attacker.getCurrentAttackCD()) {
            this.attacker.setAttackTick(0);
        } else {
            if (time == this.attacker.getStartAttackTick()) {
                this.attacker.startAttack(this.target);
            }
            this.attacker.setAttackTick(time + 1);
        }
    }

    protected boolean checkTarget() {
        if (EntityUtil.canAttackEntity(this.attacker, this.target)) {
//                if(this.shooter instanceof CatTailEntity) {
//                    return EntityUtil.canSeeEntity(this.shooter, this.target);
//                }
            return this.attacker.getSensing().hasLineOfSight(this.target);
        }
        return false;
    }
}

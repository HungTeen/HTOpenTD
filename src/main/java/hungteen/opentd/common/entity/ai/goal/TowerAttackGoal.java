package hungteen.opentd.common.entity.ai.goal;

import hungteen.htlib.util.helper.registry.EntityHelper;
import hungteen.opentd.common.codec.AttackGoalSetting;
import hungteen.opentd.common.entity.TowerEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Objects;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-07 13:48
 **/
public class TowerAttackGoal extends HTGoal {

    protected final TowerEntity towerEntity;
    protected LivingEntity target;

    public TowerAttackGoal(TowerEntity attacker) {
        this.towerEntity = attacker;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.setting() == null) {
            return false;
        }
        this.target = this.towerEntity.getTarget();
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
        this.towerEntity.setAttackTick(0);
    }

    @Override
    public void tick() {
        if (this.towerEntity.canChangeDirection()) {
            this.towerEntity.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        }
        final int time = this.towerEntity.getAttackTick();
        // 冷却结束 或者 动画已经开始。
        if (this.towerEntity.preAttackTick <= 0 || time != 0) {
            if (time >= this.towerEntity.getCurrentAttackCD()) {
                this.towerEntity.setAttackTick(0);
            } else {
                // 攻击帧。
                if (time == this.towerEntity.getStartAttackTick()) {
                    this.towerEntity.attack();
                    this.towerEntity.preAttackTick = Objects.requireNonNull(this.setting()).duration();
                }
                this.towerEntity.setAttackTick(time + 1);
            }
        } else {
            --this.towerEntity.preAttackTick;
        }
        if (Objects.requireNonNull(this.setting()).needRest()) {
            this.towerEntity.setResting(this.towerEntity.preAttackTick > 0);
        }
    }

    private AttackGoalSetting setting(){
        return this.towerEntity.getComponent() == null ? null : this.towerEntity.getComponent().attackGoalSetting().orElse(null);
    }

    protected boolean checkTarget() {
        return EntityHelper.isEntityValid(this.target)
                && this.towerEntity.canAttack(this.target)
                && this.towerEntity.getSensing().hasLineOfSight(this.target)
                && this.towerEntity.distanceTo(this.target) <= Objects.requireNonNull(this.setting()).distance();
    }
}
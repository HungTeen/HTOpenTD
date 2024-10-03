package hungteen.opentd.common.entity.ai.goal;

import hungteen.htlib.util.helper.registry.EntityHelper;
import hungteen.opentd.common.codec.ShootGoalSetting;
import hungteen.opentd.common.entity.TowerEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Objects;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-20 12:46
 **/
public class TowerShootGoal extends HTGoal {

    protected final TowerEntity towerEntity;
    protected LivingEntity target;

    public TowerShootGoal(TowerEntity attacker) {
        this.towerEntity = attacker;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if(this.towerEntity.getComponent() == null || this.towerEntity.getShootSettings().isEmpty()){
            return false;
        }
        this.target = this.towerEntity.getTarget();
        if (!this.checkTarget()) {//can not attack because of its target(such as height limit).
            this.target = null;
            this.towerEntity.setTarget(null);
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
        this.towerEntity.setShootTick(0);
    }

    @Override
    public void tick() {
        if(this.towerEntity.canChangeDirection()){
            this.towerEntity.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        }
        final int time = this.towerEntity.getShootTick();
        // 冷却结束 或者 动画已经开始。
        if (this.towerEntity.preShootTick <= 0) {
            if (time >= this.towerEntity.getCurrentShootCD()) {
                this.towerEntity.setShootTick(0);
            } else {
                // 攻击帧。
                if (time == this.towerEntity.getStartShootTick()) {
                    this.towerEntity.startShootAttack(this.target);
                    this.towerEntity.preShootTick = Objects.requireNonNull(this.setting()).duration();
                }
                this.towerEntity.setShootTick(time + 1);
            }
        } else {
            --this.towerEntity.preShootTick;
        }
        if(Objects.requireNonNull(this.setting()).needRest()){
            this.towerEntity.setResting(this.towerEntity.preShootTick > 0);
        }
    }

    private ShootGoalSetting setting(){
        return this.towerEntity.getComponent() == null ? null : this.towerEntity.getComponent().shootGoalSetting().orElse(null);
    }

    protected boolean checkTarget() {
        if(EntityHelper.isEntityValid(this.target) && this.towerEntity.canAttack(this.target)){
            ShootGoalSetting setting = setting();
            if(setting != null && setting.mustSeeTarget() && !this.towerEntity.getSensing().hasLineOfSight(this.target)){
                return false;
            }
            return true;
        }
        return false;
    }

}

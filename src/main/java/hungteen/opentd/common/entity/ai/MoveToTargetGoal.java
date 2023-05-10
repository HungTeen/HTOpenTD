package hungteen.opentd.common.entity.ai;

import hungteen.opentd.common.entity.TowerEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-02 19:24
 **/
public class MoveToTargetGoal extends HTGoal {

    private final TowerEntity towerEntity;
    private final double speedModifier;
    private final double followRange;
    private final double backwardDistance;
    private final double upwardDistance;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public MoveToTargetGoal(TowerEntity towerEntity, double speedModifier, double backwardPercent, double upwardPercent) {
        this.towerEntity = towerEntity;
        this.speedModifier = speedModifier;
        this.followRange = towerEntity.getAttributeValue(Attributes.FOLLOW_RANGE);
        this.backwardDistance = followRange * backwardPercent;
        this.upwardDistance = followRange * upwardPercent;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canUse() {
        return this.towerEntity.getTarget() != null;
    }

    public boolean canContinueToUse() {
        return (this.canUse() || !this.towerEntity.getNavigation().isDone());
    }

    public void start() {
        super.start();
        this.towerEntity.setAggressive(true);
    }

    public void stop() {
        super.stop();
        this.towerEntity.setAggressive(false);
        this.seeTime = 0;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingentity = this.towerEntity.getTarget();
        if (livingentity != null) {
            double distanceSqr = this.towerEntity.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
            boolean flag = this.towerEntity.getSensing().hasLineOfSight(livingentity);
            boolean flag1 = this.seeTime > 0;
            if (flag != flag1) {
                this.seeTime = 0;
            }

            if (flag) {
                ++this.seeTime;
            } else {
                --this.seeTime;
            }

            if (!(distanceSqr > this.followRange) && this.seeTime >= 20) {
                this.towerEntity.getNavigation().stop();
                ++this.strafingTime;
            } else {
                this.towerEntity.getNavigation().moveTo(livingentity, this.speedModifier);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
                if ((double)this.towerEntity.getRandom().nextFloat() < 0.3D) {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double)this.towerEntity.getRandom().nextFloat() < 0.3D) {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1) {
                if (distanceSqr > this.upwardDistance) {
                    this.strafingBackwards = false;
                } else if (distanceSqr < this.backwardDistance) {
                    this.strafingBackwards = true;
                }

                this.towerEntity.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                this.towerEntity.lookAt(livingentity, 30.0F, 30.0F);
            } else {
                this.towerEntity.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            }
        }
    }
}

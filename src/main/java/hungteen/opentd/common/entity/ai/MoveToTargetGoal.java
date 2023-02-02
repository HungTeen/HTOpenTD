package hungteen.opentd.common.entity.ai;

import hungteen.opentd.common.entity.PlantEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;

import java.util.EnumSet;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-02 19:24
 **/
public class MoveToTargetGoal extends HTGoal {

    private final PlantEntity plantEntity;
    private final double speedModifier;
    private final double followRange;
    private final double backwardDistance;
    private final double upwardDistance;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public MoveToTargetGoal(PlantEntity plantEntity, double speedModifier, double backwardPercent, double upwardPercent) {
        this.plantEntity = plantEntity;
        this.speedModifier = speedModifier;
        this.followRange = plantEntity.getAttributeValue(Attributes.FOLLOW_RANGE);
        this.backwardDistance = followRange * backwardPercent;
        this.upwardDistance = followRange * upwardPercent;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canUse() {
        return this.plantEntity.getTarget() != null;
    }

    public boolean canContinueToUse() {
        return (this.canUse() || !this.plantEntity.getNavigation().isDone());
    }

    public void start() {
        super.start();
        this.plantEntity.setAggressive(true);
    }

    public void stop() {
        super.stop();
        this.plantEntity.setAggressive(false);
        this.seeTime = 0;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        LivingEntity livingentity = this.plantEntity.getTarget();
        if (livingentity != null) {
            double distanceSqr = this.plantEntity.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
            boolean flag = this.plantEntity.getSensing().hasLineOfSight(livingentity);
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
                this.plantEntity.getNavigation().stop();
                ++this.strafingTime;
            } else {
                this.plantEntity.getNavigation().moveTo(livingentity, this.speedModifier);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
                if ((double)this.plantEntity.getRandom().nextFloat() < 0.3D) {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double)this.plantEntity.getRandom().nextFloat() < 0.3D) {
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

                this.plantEntity.getMoveControl().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
                this.plantEntity.lookAt(livingentity, 30.0F, 30.0F);
            } else {
                this.plantEntity.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            }
        }
    }
}

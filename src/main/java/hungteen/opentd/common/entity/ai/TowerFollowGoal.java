package hungteen.opentd.common.entity.ai;

import hungteen.opentd.common.codec.FollowGoalSetting;
import hungteen.opentd.common.entity.TowerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

import java.util.EnumSet;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/6/9 9:16
 */
public class TowerFollowGoal extends Goal {

    private final TowerEntity tower;
    private final FollowGoalSetting setting;
    private final LevelReader level;
    private final PathNavigation navigation;
    private LivingEntity owner;
    private int timeToRecalcPath;
    private float oldWaterCost;

    public TowerFollowGoal(TowerEntity tower, FollowGoalSetting setting) {
        this.tower = tower;
        this.level = tower.level();
        this.setting = setting;
        this.navigation = tower.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(tower.getNavigation() instanceof GroundPathNavigation) && !(tower.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    public boolean canUse() {
        LivingEntity livingentity = this.tower.getOwner();
        if (livingentity == null) {
            return false;
        } else if (livingentity.isSpectator()) {
            return false;
//        } else if (this.tower.isOrderedToSit()) {
//            return false;
        } else if (this.tower.distanceTo(livingentity) < setting.startDistance()) {
            return false;
        } else {
            this.owner = livingentity;
            return true;
        }
    }

    public boolean canContinueToUse() {
        if (this.navigation.isDone()) {
            return false;
//        } else if (this.tower.isOrderedToSit()) {
//            return false;
        } else {
            return this.tower.distanceTo(this.owner) > setting.stopDistance();
        }
    }

    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.tower.getPathfindingMalus(BlockPathTypes.WATER);
        this.tower.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.tower.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    public void tick() {
        if(this.tower.canChangeDirection()){
            this.tower.getLookControl().setLookAt(this.owner, 10.0F, (float)this.tower.getMaxHeadXRot());
        }
        if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = this.adjustedTickDelay(10);
            if (!this.tower.isLeashed() && !this.tower.isPassenger()) {
                if (this.tower.distanceTo(this.owner) >= setting.teleportDistance()) {
                    this.teleportToOwner();
                } else {
                    this.navigation.moveTo(this.owner, setting.speedModifier());
                }

            }
        }
    }

    private void teleportToOwner() {
        BlockPos blockpos = this.owner.blockPosition();

        for(int i = 0; i < 10; ++i) {
            int j = this.randomIntInclusive(-3, 3);
            int k = this.randomIntInclusive(-1, 1);
            int l = this.randomIntInclusive(-3, 3);
            boolean flag = this.maybeTeleportTo(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
                return;
            }
        }

    }

    private boolean maybeTeleportTo(int p_25304_, int p_25305_, int p_25306_) {
        if (Math.abs((double)p_25304_ - this.owner.getX()) < 2.0D && Math.abs((double)p_25306_ - this.owner.getZ()) < 2.0D) {
            return false;
        } else if (!this.canTeleportTo(new BlockPos(p_25304_, p_25305_, p_25306_))) {
            return false;
        } else {
            this.tower.moveTo((double)p_25304_ + 0.5D, (double)p_25305_, (double)p_25306_ + 0.5D, this.tower.getYRot(), this.tower.getXRot());
            this.navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos p_25308_) {
        BlockPathTypes blockpathtypes = WalkNodeEvaluator.getBlockPathTypeStatic(this.level, p_25308_.mutable());
        if (blockpathtypes != BlockPathTypes.WALKABLE) {
            return false;
        } else {
            BlockState blockstate = this.level.getBlockState(p_25308_.below());
            if (!setting.canFly() && blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = p_25308_.subtract(this.tower.blockPosition());
                return this.level.noCollision(this.tower, this.tower.getBoundingBox().move(blockpos));
            }
        }
    }

    private int randomIntInclusive(int p_25301_, int p_25302_) {
        return this.tower.getRandom().nextInt(p_25302_ - p_25301_ + 1) + p_25301_;
    }
}

package hungteen.opentd.common.entity.ai;

import hungteen.opentd.common.entity.PlantHeroEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-06-23 11:16
 **/
public class TowerMoveToGoal extends Goal {

    protected final PlantHeroEntity mob;
    protected int interval;

    public TowerMoveToGoal(PlantHeroEntity entity) {
        this(entity, 120);
    }

    public TowerMoveToGoal(PlantHeroEntity p_25741_, int interval) {
        this.mob = p_25741_;
        this.interval = interval;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canUse() {
        if (this.mob.isVehicle() || this.mob.getMoveTo().isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone() && !this.mob.isVehicle();
    }

    public void start() {
        this.mob.getMoveTo().ifPresent(pos -> {
            this.mob.getNavigation().moveTo(pos.getX(), pos.getY(), pos.getZ(), 1F);
        });
    }

    public void stop() {
        this.mob.getNavigation().stop();
        super.stop();
    }

}
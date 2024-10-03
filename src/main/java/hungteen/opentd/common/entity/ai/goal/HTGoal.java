package hungteen.opentd.common.entity.ai.goal;

import net.minecraft.world.entity.ai.goal.Goal;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-20 20:08
 **/
public abstract class HTGoal extends Goal {

    private final boolean alwaysTick;

    public HTGoal(){
        this(true);
    }

    public HTGoal(boolean alwaysTick){
        this.alwaysTick = alwaysTick;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return this.alwaysTick;
    }
}

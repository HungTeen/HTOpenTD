package hungteen.opentd.api.interfaces;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.control.MoveControl;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/12 10:57
 */
public interface IMoveComponent {

    MoveControl create(Mob mob);

    /**
     * Get the type of move component.
     * @return Move type.
     */
    IMoveComponentType<?> getType();
}

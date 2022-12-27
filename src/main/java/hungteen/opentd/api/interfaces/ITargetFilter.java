package hungteen.opentd.api.interfaces;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 18:01
 **/
public interface ITargetFilter {

    boolean match(Entity owner, Entity target);

    /**
     * Get the type of filter.
     * @return Filter type.
     */
    ITargetFilterType<?> getType();

}

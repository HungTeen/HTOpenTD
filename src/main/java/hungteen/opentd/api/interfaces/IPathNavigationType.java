package hungteen.opentd.api.interfaces;

import hungteen.htlib.api.interfaces.ISimpleEntry;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/12 10:31
 */
public interface IPathNavigationType extends ISimpleEntry {

    PathNavigation create(Level level, PathfinderMob mob);

}

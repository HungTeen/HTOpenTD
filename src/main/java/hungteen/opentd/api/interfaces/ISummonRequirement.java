package hungteen.opentd.api.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 11:42
 **/
public interface ISummonRequirement {

    boolean allowOn(ServerLevel level, Player player, Entity entity, boolean sendMessage);

    boolean allowOn(ServerLevel level, Player player, BlockState state, BlockPos pos, boolean sendMessage);

    void consume(ServerLevel level, Player player);

    /**
     * Get the type of summon requirement.
     * @return Requirement type.
     */
    ISummonRequirementType<?> getType();
}

package hungteen.opentd.impl.requirement;

import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;
import hungteen.opentd.api.interfaces.ITargetFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-07 20:30
 **/
public record EntityRequirement(ITargetFilter filter) implements ISummonRequirement {
    @Override
    public boolean allowOn(Level level, Player player, Entity entity) {
        return false;
    }

    @Override
    public boolean allowOn(Level level, Player player, BlockState state, BlockPos pos) {
        return false;
    }

    @Override
    public void consume(Level level, Player player) {

    }

    @Override
    public ISummonRequirementType<?> getType() {
        return null;
    }
}

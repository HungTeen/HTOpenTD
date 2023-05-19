package hungteen.opentd.common.impl.requirement;

import com.mojang.serialization.Codec;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-07 20:24
 **/
public class NoRequirement implements ISummonRequirement {

    public static final NoRequirement INSTANCE = new NoRequirement();

    public static final Codec<NoRequirement> CODEC = Codec.unit(() -> {
        return NoRequirement.INSTANCE;
    });

    @Override
    public boolean allowOn(ServerLevel level, Player player, Entity entity, boolean sendMessage) {
        return true;
    }

    @Override
    public boolean allowOn(ServerLevel level, Player player, BlockState state, BlockPos pos, boolean sendMessage) {
        return true;
    }

    @Override
    public void consume(ServerLevel level, Player player) {

    }

    @Override
    public ISummonRequirementType<?> getType() {
        return HTSummonRequirements.NO_REQUIREMENT;
    }

}

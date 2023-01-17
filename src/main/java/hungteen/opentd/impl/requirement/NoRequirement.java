package hungteen.opentd.impl.requirement;

import com.mojang.serialization.Codec;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;

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
    public boolean allowOn(Level level, Player player, Entity entity) {
        return true;
    }

    @Override
    public boolean allowOn(Level level, Player player, BlockState state, BlockPos pos) {
        return true;
    }

    @Override
    public void consume(Level level, Player player) {

    }

    @Override
    public ISummonRequirementType<?> getType() {
        return HTSummonRequirements.NO_REQUIREMENT;
    }

}

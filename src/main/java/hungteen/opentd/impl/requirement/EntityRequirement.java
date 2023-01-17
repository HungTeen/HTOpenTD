package hungteen.opentd.impl.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.impl.filter.HTTargetFilters;
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

    public static final Codec<EntityRequirement> CODEC = RecordCodecBuilder.<EntityRequirement>mapCodec(instance -> instance.group(
            HTTargetFilters.getCodec().fieldOf("filter").forGetter(EntityRequirement::filter)
            ).apply(instance, EntityRequirement::new)).codec();

    @Override
    public boolean allowOn(Level level, Player player, Entity entity) {
        return filter().match(player, entity);
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
        return HTSummonRequirements.ENTITY_REQUIREMENT;
    }
}

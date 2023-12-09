package hungteen.opentd.common.impl.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.helper.PlayerHelper;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.common.impl.filter.OTDTargetFilters;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-07 20:30
 **/
public record EntityRequirement(Optional<String> tip, Holder<ITargetFilter> filter) implements ISummonRequirement {

    public static final Codec<EntityRequirement> CODEC = RecordCodecBuilder.<EntityRequirement>mapCodec(instance -> instance.group(
            Codec.optionalField("tip", Codec.STRING).forGetter(EntityRequirement::tip),
            OTDTargetFilters.getCodec().fieldOf("filter").forGetter(EntityRequirement::filter)
    ).apply(instance, EntityRequirement::new)).codec();

    @Override
    public boolean allowOn(ServerLevel level, Player player, Entity entity, boolean sendMessage) {
        if (!filter().get().match(level, player, entity)) {
            if (sendMessage) PlayerHelper.sendTipTo(player, getTip());
            return false;
        }
        return true;
    }

    @Override
    public boolean allowOn(ServerLevel level, Player player, BlockState state, BlockPos pos, boolean sendMessage) {
        return false;
    }

    @Override
    public void consume(ServerLevel level, Player player) {
    }

    public Component getTip() {
        return this.tip().map(Component::translatable).orElse(Component.translatable("tip.opentd.wrong_entity"));
    }

    @Override
    public ISummonRequirementType<?> getType() {
        return OTDRequirementTypes.ENTITY_REQUIREMENT;
    }
}

package hungteen.opentd.common.impl.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.helper.PlayerHelper;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-17 11:22
 **/
public record OrRequirement(List<Holder<ISummonRequirement>> requirements, Optional<String> tip) implements ISummonRequirement {

    public static final Codec<OrRequirement> CODEC = RecordCodecBuilder.<OrRequirement>mapCodec(instance -> instance.group(
            OTDSummonRequirements.getCodec().listOf().fieldOf("requirements").forGetter(OrRequirement::requirements),
            Codec.optionalField("tip", Codec.STRING).forGetter(OrRequirement::tip)
    ).apply(instance, OrRequirement::new)).codec();

    @Override
    public boolean allowOn(ServerLevel level, Player player, Entity entity, boolean sendMessage) {
        if(requirements().stream().noneMatch(r -> r.get().allowOn(level, player, entity, false))){
            if(sendMessage) PlayerHelper.sendTipTo(player, getTip());
            return false;
        }
        return true;
    }

    @Override
    public boolean allowOn(ServerLevel level, Player player, BlockState state, BlockPos pos, boolean sendMessage) {
        if(requirements().stream().noneMatch(r -> r.get().allowOn(level, player, state, pos, false))){
            if(sendMessage) PlayerHelper.sendTipTo(player, getTip());
            return false;
        }
        return true;
    }

    @Override
    public void consume(ServerLevel level, Player player) {
        // No consume.
    }

    public Component getTip() {
        return this.tip().map(Component::translatable).orElse(Component.empty());
    }

    @Override
    public ISummonRequirementType<?> getType() {
        return OTDRequirementTypes.OR_REQUIREMENT;
    }
}

package hungteen.opentd.common.impl.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.helper.PlayerHelper;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;
import net.minecraft.core.BlockPos;
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
public record NotRequirement(ISummonRequirement requirement, Optional<String> tip) implements ISummonRequirement {

    public static final Codec<NotRequirement> CODEC = RecordCodecBuilder.<NotRequirement>mapCodec(instance -> instance.group(
            HTSummonRequirements.getCodec().fieldOf("requirement").forGetter(NotRequirement::requirement),
            Codec.optionalField("tip", Codec.STRING).forGetter(NotRequirement::tip)
    ).apply(instance, NotRequirement::new)).codec();

    @Override
    public boolean allowOn(ServerLevel level, Player player, Entity entity, boolean sendMessage) {
        if(requirement().allowOn(level, player, entity, false)){
            if(sendMessage) PlayerHelper.sendTipTo(player, getTip());
            return false;
        }
        return true;
    }

    @Override
    public boolean allowOn(ServerLevel level, Player player, BlockState state, BlockPos pos, boolean sendMessage) {
        if(requirement().allowOn(level, player, state, pos, false)){
            if(sendMessage) PlayerHelper.sendTipTo(player, getTip());
            return false;
        }
        return true;
    }

    @Override
    public void consume(ServerLevel level, Player player) {
    }

    public Component getTip() {
        return this.tip().map(Component::translatable).orElse(Component.empty());
    }

    @Override
    public ISummonRequirementType<?> getType() {
        return HTSummonRequirements.NOT_REQUIREMENT;
    }
}

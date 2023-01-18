package hungteen.opentd.impl.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 19:44
 **/
public record ExperienceRequirement(Optional<String> tip, int experience, int level, int costExperience,
                                    int costLevel) implements ISummonRequirement {

    public static final Codec<ExperienceRequirement> CODEC = RecordCodecBuilder.<ExperienceRequirement>mapCodec(instance -> instance.group(
            Codec.optionalField("tip", Codec.STRING).forGetter(ExperienceRequirement::tip),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("xp", 0).forGetter(ExperienceRequirement::experience),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("level", 0).forGetter(ExperienceRequirement::level),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("cost_xp", 0).forGetter(ExperienceRequirement::costExperience),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("cost_level", 0).forGetter(ExperienceRequirement::costLevel)
    ).apply(instance, ExperienceRequirement::new)).codec();

    @Override
    public boolean allowOn(ServerLevel level, Player player, Entity entity) {
        return enoughXp(player);
    }

    @Override
    public boolean allowOn(ServerLevel level, Player player, BlockState state, BlockPos pos) {
        return enoughXp(player);
    }

    @Override
    public void consume(ServerLevel level, Player player) {
        player.giveExperiencePoints(-costExperience());
        player.giveExperienceLevels(-costLevel());
    }

    public boolean enoughXp(Player player) {
        return player.experienceLevel >= level() && player.totalExperience >= experience() && player.experienceLevel >= costLevel() && player.totalExperience >= costExperience();
    }

    public Component getTip() {
        return Component.translatable(this.tip().orElse("tip.opentd.require_xp"), this.level(), this.experience());
    }

    @Override
    public ISummonRequirementType<?> getType() {
        return HTSummonRequirements.EXPERIENCE_REQUIREMENT;
    }
}

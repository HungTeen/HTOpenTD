package hungteen.opentd.impl.requirement;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.impl.placement.AbsoluteAreaPlacement;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.commands.ExperienceCommand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 19:44
 **/
public class ExperienceRequirement implements ISummonRequirement {

    public static final Codec<ExperienceRequirement> CODEC = RecordCodecBuilder.<ExperienceRequirement>mapCodec(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("xp", 0).forGetter(ExperienceRequirement::getExperience),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("level", 0).forGetter(ExperienceRequirement::getLevel),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("cost_xp", 0).forGetter(ExperienceRequirement::getCostExperience),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("cost_level", 0).forGetter(ExperienceRequirement::getCostLevel)
    ).apply(instance, ExperienceRequirement::new)).codec();
    private final int experience;
    private final int level;
    private final int costExperience;
    private final int costLevel;

    public ExperienceRequirement(int experience, int level, int costExperience, int costLevel) {
        this.experience = experience;
        this.level = level;
        this.costExperience = costExperience;
        this.costLevel = costLevel;
    }

    @Override
    public boolean allowOn(Level level, Player player, Entity entity) {
        return enoughXp(player);
    }

    @Override
    public boolean allowOn(Level level, Player player, BlockState state, BlockPos pos) {
        return enoughXp(player);
    }

    @Override
    public void consume(Level level, Player player) {
        player.giveExperiencePoints(- getCostExperience());
        player.giveExperienceLevels(- getCostLevel());
    }

    public boolean enoughXp(Player player){
        return player.experienceLevel >= getLevel() && player.totalExperience >= getExperience() && player.experienceLevel >= getCostLevel() && player.totalExperience >= getCostExperience();
    }

    public int getExperience() {
        return experience;
    }

    public int getLevel() {
        return level;
    }

    public int getCostExperience() {
        return costExperience;
    }

    public int getCostLevel() {
        return costLevel;
    }

    @Override
    public ISummonRequirementType<?> getType() {
        return HTSummonRequirements.EXPERIENCE_REQUIREMENT;
    }
}

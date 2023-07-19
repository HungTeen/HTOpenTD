package hungteen.opentd.common.impl.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.scores.Team;

import java.util.Optional;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/6/8 18:10
 */
public record TeamFilter(Optional<String> teamName, boolean same, boolean selfEmpty, boolean targetEmpty) implements ITargetFilter {

    public static final Codec<TeamFilter> CODEC = RecordCodecBuilder.<TeamFilter>mapCodec(instance -> instance.group(
            Codec.optionalField("team_name", Codec.STRING).forGetter(TeamFilter::teamName),
            Codec.BOOL.optionalFieldOf("same", true).forGetter(TeamFilter::same),
            Codec.BOOL.optionalFieldOf("self_empty", false).forGetter(TeamFilter::selfEmpty),
            Codec.BOOL.optionalFieldOf("target_empty", false).forGetter(TeamFilter::targetEmpty)
    ).apply(instance, TeamFilter::new)).codec();

    @Override
    public boolean match(ServerLevel level, Entity owner, Entity target) {
        final Team team = teamName().isPresent() ? level.getScoreboard().getPlayerTeam(teamName().get()) : owner.getTeam();
        if(team == null){
            return selfEmpty();
        } else if(target.getTeam() == null){
            return targetEmpty();
        }
        return team.isAlliedTo(target.getTeam()) == same();
    }

    @Override
    public ITargetFilterType<?> getType() {
        return HTTargetFilters.TEAM_FILTER;
    }
}

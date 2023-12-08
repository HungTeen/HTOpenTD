package hungteen.opentd.common.impl.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.List;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 18:14
 **/
public record AndTargetFilter(List<ITargetFilter> filters) implements ITargetFilter {

    public static final Codec<AndTargetFilter> CODEC = RecordCodecBuilder.<AndTargetFilter>mapCodec(instance -> instance.group(
            OTDTargetFilterTypes.getCodec().listOf().fieldOf("filters").forGetter(AndTargetFilter::filters)
    ).apply(instance, AndTargetFilter::new)).codec();

    @Override
    public boolean match(ServerLevel level, Entity owner, Entity target) {
        return this.filters().stream().allMatch(l -> l.match(level, owner, target));
    }

    @Override
    public ITargetFilterType<?> getType() {
        return OTDTargetFilterTypes.AND_FILTER;
    }
}

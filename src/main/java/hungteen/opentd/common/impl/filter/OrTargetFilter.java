package hungteen.opentd.common.impl.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.List;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 18:14
 **/
public record OrTargetFilter(List<Holder<ITargetFilter>> filters) implements ITargetFilter {

    public static final Codec<OrTargetFilter> CODEC = RecordCodecBuilder.<OrTargetFilter>mapCodec(instance -> instance.group(
            OTDTargetFilters.getCodec().listOf().fieldOf("filters").forGetter(OrTargetFilter::filters)
    ).apply(instance, OrTargetFilter::new)).codec();

    @Override
    public boolean match(ServerLevel level, Entity owner, Entity target) {
        return this.filters().stream().anyMatch(l -> l.get().match(level, owner, target));
    }

    @Override
    public ITargetFilterType<?> getType() {
        return OTDTargetFilterTypes.OR_FILTER;
    }
}

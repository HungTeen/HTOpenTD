package hungteen.opentd.common.impl.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 18:14
 **/
public record NotTargetFilter(Holder<ITargetFilter> filter) implements ITargetFilter {

    public static final Codec<NotTargetFilter> CODEC = RecordCodecBuilder.<NotTargetFilter>mapCodec(instance -> instance.group(
            OTDTargetFilters.getCodec().fieldOf("filter").forGetter(NotTargetFilter::filter)
    ).apply(instance, NotTargetFilter::new)).codec();

    @Override
    public boolean match(ServerLevel level, Entity owner, Entity target) {
        return ! this.filter().get().match(level, owner, target);
    }

    @Override
    public ITargetFilterType<?> getType() {
        return OTDTargetFilterTypes.NOT_FILTER;
    }
}

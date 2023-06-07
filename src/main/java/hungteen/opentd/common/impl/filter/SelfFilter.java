package hungteen.opentd.common.impl.filter;

import com.mojang.serialization.Codec;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/6/7 14:58
 */
public class SelfFilter implements ITargetFilter {

    public static final SelfFilter INSTANCE = new SelfFilter();

    public static final Codec<SelfFilter> CODEC = Codec.unit(() -> {
        return SelfFilter.INSTANCE;
    });

    @Override
    public boolean match(ServerLevel level, Entity owner, Entity target) {
        return owner.equals(target);
    }

    @Override
    public ITargetFilterType<?> getType() {
        return HTTargetFilters.SELF_FILTER;
    }
}

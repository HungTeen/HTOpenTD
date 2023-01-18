package hungteen.opentd.impl.filter;

import com.mojang.serialization.Codec;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import hungteen.opentd.impl.requirement.NoRequirement;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-27 13:41
 **/
public class AlwaysTrueFilter implements ITargetFilter {

    public static final AlwaysTrueFilter INSTANCE = new AlwaysTrueFilter();

    public static final Codec<AlwaysTrueFilter> CODEC = Codec.unit(() -> {
        return AlwaysTrueFilter.INSTANCE;
    });

    @Override
    public boolean match(ServerLevel level, Entity owner, Entity target) {
        return true;
    }

    @Override
    public ITargetFilterType<?> getType() {
        return HTTargetFilters.ALWAYS_TRUE;
    }
}

package hungteen.opentd.impl.filter;

import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import net.minecraft.world.entity.Entity;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-27 13:41
 **/
public class AlwaysTrueFilter implements ITargetFilter {

//    public static final Codec<AlwaysTrueFilter> CODEC = RecordCodecBuild.apply(instance, AlwaysTrueFilter::new)).codec();

    @Override
    public boolean match(Entity owner, Entity target) {
        return true;
    }

    @Override
    public ITargetFilterType<?> getType() {
        return null;
    }
}

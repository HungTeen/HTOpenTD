package hungteen.opentd.common.impl.finder;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ITargetFinder;
import hungteen.opentd.api.interfaces.ITargetFinderType;
import hungteen.opentd.util.Util;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 18:04
 **/
public interface OTDTargetFinderTypes {

    HTSimpleRegistry<ITargetFinderType<?>> FINDER_TYPES = HTRegistryManager.createSimple(Util.prefix("target_finder_type"));

    ITargetFinderType<RangeFinder> RANGE_FINDER = register(new DefaultFinder<>("range",  RangeFinder.CODEC));
    ITargetFinderType<LineFinder> LINE_FINDER = register(new DefaultFinder<>("line",  LineFinder.CODEC));

    static IHTSimpleRegistry<ITargetFinderType<?>> registry(){
        return FINDER_TYPES;
    }

    static <T extends ITargetFinder> ITargetFinderType<T> register(ITargetFinderType<T> type){
        return registry().register(type);
    }

    record DefaultFinder<P extends ITargetFinder>(String name, Codec<P> codec) implements ITargetFinderType<P> {

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getModID() {
            return OpenTD.MOD_ID;
        }
    }
    
}

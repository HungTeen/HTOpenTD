package hungteen.opentd.impl.finder;

import com.mojang.serialization.Codec;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ITargetFinder;
import hungteen.opentd.api.interfaces.ITargetFinderType;
import java.util.Arrays;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 18:04
 **/
public class HTTargetFinders {

    public static final HTSimpleRegistry<ITargetFinderType<?>> FINDER_TYPES = HTRegistryManager.create(OpenTD.prefix("target_finder_type"));
    public static final HTCodecRegistry<ITargetFinder> FINDERS = HTRegistryManager.create(ITargetFinder.class, "tower_defence/target_finders", HTTargetFinders::getCodec);

    /* Target Finder types */

    public static final ITargetFinderType<RangeFinder> RANGE_FINDER = new DefaultFinder<>("range",  RangeFinder.CODEC);
    public static final ITargetFinderType<LineFinder> LINE_FINDER = new DefaultFinder<>("line",  LineFinder.CODEC);


    /* Target Finders */

    /**
     * {@link OpenTD#OpenTD()}
     */
    public static void registerStuffs(){
        Arrays.asList(RANGE_FINDER, LINE_FINDER).forEach(HTTargetFinders::registerFinderType);
    }

    public static void registerFinderType(ITargetFinderType<?> type){
        FINDER_TYPES.register(type);
    }

    public static Codec<ITargetFinder> getCodec(){
        return FINDER_TYPES.byNameCodec().dispatch(ITargetFinder::getType, ITargetFinderType::codec);
    }

    protected record DefaultFinder<P extends ITargetFinder>(String name, Codec<P> codec) implements ITargetFinderType<P> {

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

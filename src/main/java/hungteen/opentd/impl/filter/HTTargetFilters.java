package hungteen.opentd.impl.filter;

import com.mojang.serialization.Codec;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;

import java.util.Arrays;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 18:04
 **/
public class HTTargetFilters {

    public static final HTSimpleRegistry<ITargetFilterType<?>> FILTER_TYPES = HTRegistryManager.create(OpenTD.prefix("target_filter_type"));
    public static final HTCodecRegistry<ITargetFilter> FILTERS = HTRegistryManager.create(ITargetFilter.class, "tower_defence/target_filters", HTTargetFilters::getCodec, true);

    /* Tower types */

    public static final ITargetFilterType<TagTargetFilter> TAG_FILTER = new DefaultFilter<>("tag",  TagTargetFilter.CODEC);
    public static final ITargetFilterType<OrTargetFilter> OR_FILTER = new DefaultFilter<>("or",  OrTargetFilter.CODEC);
    public static final ITargetFilterType<TypeTargetFilter> TYPE_FILTER = new DefaultFilter<>("types",  TypeTargetFilter.CODEC);
    public static final ITargetFilterType<AndTargetFilter> AND_FILTER = new DefaultFilter<>("and",  AndTargetFilter.CODEC);

    //    public static final ITargetFilterType<AlwaysTrueFilter> ALWAYS_TRUE = new DefaultFilter<>("always_true",  Codec.EMPTY);

    /* Towers */

//    public static final HTRegistryHolder<ITargetFilter> PEA_SHOOTER = TOWERS.innerRegister(
//            OpenTD.prefix("pea_shooter"), new PVZPlantComponent(
//                    new PVZPlantComponent.PlantSettings(
//                            PVZPlantComponent.GrowSettings.DEFAULT,
//                            OpenTD.prefix("geo/pea_shooter.geo.json"),
//                            OpenTD.prefix("textures/entity/pea_shooter.png"),
//                            OpenTD.prefix("animations/pea_shooter.animation.json")
//                    )
//            )
//    );

    /**
     * {@link OpenTD#OpenTD()}
     */
    public static void registerStuffs(){
        Arrays.asList(TAG_FILTER, TYPE_FILTER, OR_FILTER).forEach(HTTargetFilters::registerFilterType);
    }

    public static void registerFilterType(ITargetFilterType<?> type){
        FILTER_TYPES.register(type);
    }

    public static Codec<ITargetFilter> getCodec(){
        return FILTER_TYPES.byNameCodec().dispatch(ITargetFilter::getType, ITargetFilterType::codec);
    }

    protected record DefaultFilter<P extends ITargetFilter>(String name, Codec<P> codec) implements ITargetFilterType<P> {

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

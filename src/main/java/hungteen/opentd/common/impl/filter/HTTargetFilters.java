package hungteen.opentd.common.impl.filter;

import com.mojang.serialization.Codec;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.Arrays;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 18:04
 **/
public class HTTargetFilters {

    public static final HTSimpleRegistry<ITargetFilterType<?>> FILTER_TYPES = HTRegistryManager.create(OpenTD.prefix("target_filter_type"));
    public static final HTCodecRegistry<ITargetFilter> FILTERS = HTRegistryManager.create(ITargetFilter.class, "tower_defence/target_filters", HTTargetFilters::getCodec, OpenTD.MOD_ID);

    /* Tower types */

    public static final ITargetFilterType<TagTargetFilter> TAG_FILTER = new DefaultFilter<>("tag",  TagTargetFilter.CODEC);
    public static final ITargetFilterType<OrTargetFilter> OR_FILTER = new DefaultFilter<>("or",  OrTargetFilter.CODEC);
    public static final ITargetFilterType<TypeTargetFilter> TYPE_FILTER = new DefaultFilter<>("types",  TypeTargetFilter.CODEC);
    public static final ITargetFilterType<AndTargetFilter> AND_FILTER = new DefaultFilter<>("and",  AndTargetFilter.CODEC);
    public static final ITargetFilterType<EntityPredicateFilter> ENTITY_PREDICATE_FILTER = new DefaultFilter<>("entity_predicate",  EntityPredicateFilter.CODEC);
    public static final ITargetFilterType<AlwaysTrueFilter> ALWAYS_TRUE = new DefaultFilter<>("true",  AlwaysTrueFilter.CODEC);
    public static final ITargetFilterType<NotTargetFilter> NOT_FILTER = new DefaultFilter<>("not",  NotTargetFilter.CODEC);
    public static final ITargetFilterType<ClassFilter> CLASS_FILTER = new DefaultFilter<>("class",  ClassFilter.CODEC);
    public static final ITargetFilterType<NBTTargetFilter> NBT_FILTER = new DefaultFilter<>("nbt",  NBTTargetFilter.CODEC);
    public static final ITargetFilterType<EventFilter> EVENT_FILTER = new DefaultFilter<>("event",  EventFilter.CODEC);
    public static final ITargetFilterType<SelfFilter> SELF_FILTER = new DefaultFilter<>("self",  SelfFilter.CODEC);
    public static final ITargetFilterType<TeamFilter> TEAM_FILTER = new DefaultFilter<>("team",  TeamFilter.CODEC);

    /* Towers */

    /**
     * {@link OpenTD#setUp(FMLCommonSetupEvent)} ()}
     */
    public static void registerStuffs(){
        Arrays.asList(TAG_FILTER, TYPE_FILTER, OR_FILTER, AND_FILTER, ENTITY_PREDICATE_FILTER, ALWAYS_TRUE, NOT_FILTER, CLASS_FILTER, NBT_FILTER, EVENT_FILTER, SELF_FILTER, TEAM_FILTER).forEach(HTTargetFilters::registerFilterType);
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

package hungteen.opentd.common.impl.filter;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import hungteen.opentd.util.Util;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 18:04
 **/
public interface OTDTargetFilterTypes {

    HTSimpleRegistry<ITargetFilterType<?>> FILTER_TYPES = HTRegistryManager.createSimple(Util.prefix("target_filter_type"));

    ITargetFilterType<AlwaysTrueFilter> ALWAYS_TRUE = register(new DefaultFilter<>("true", AlwaysTrueFilter.CODEC));
    ITargetFilterType<AndTargetFilter> AND_FILTER = register(new DefaultFilter<>("and", AndTargetFilter.CODEC));
    ITargetFilterType<OrTargetFilter> OR_FILTER = register(new DefaultFilter<>("or", OrTargetFilter.CODEC));
    ITargetFilterType<NotTargetFilter> NOT_FILTER = register(new DefaultFilter<>("not", NotTargetFilter.CODEC));
    ITargetFilterType<TypeTargetFilter> TYPE_FILTER = register(new DefaultFilter<>("types", TypeTargetFilter.CODEC));
    ITargetFilterType<TagTargetFilter> TAG_FILTER = register(new DefaultFilter<>("tag", TagTargetFilter.CODEC));
    ITargetFilterType<ClassFilter> CLASS_FILTER = register(new DefaultFilter<>("class", ClassFilter.CODEC));
    ITargetFilterType<NBTTargetFilter> NBT_FILTER = register(new DefaultFilter<>("nbt", NBTTargetFilter.CODEC));
    ITargetFilterType<EventFilter> EVENT_FILTER = register(new DefaultFilter<>("event", EventFilter.CODEC));
    ITargetFilterType<SelfFilter> SELF_FILTER = register(new DefaultFilter<>("self", SelfFilter.CODEC));
    ITargetFilterType<TeamFilter> TEAM_FILTER = register(new DefaultFilter<>("team", TeamFilter.CODEC));


    static IHTSimpleRegistry<ITargetFilterType<?>> registry() {
        return FILTER_TYPES;
    }

    static <T extends ITargetFilter> ITargetFilterType<T> register(ITargetFilterType<T> type) {
        return registry().register(type);
    }

    record DefaultFilter<P extends ITargetFilter>(String name, Codec<P> codec) implements ITargetFilterType<P> {

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

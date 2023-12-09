package hungteen.opentd.common.impl.finder;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFinder;
import hungteen.opentd.api.interfaces.ITargetFinderType;
import hungteen.opentd.common.impl.filter.OTDTargetFilters;
import hungteen.opentd.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

/**
 * @program: HTOpenTD
 * @author: PangTeen
 * @create: 2023/12/8 23:23
 **/
public interface OTDTargetFinders {

    HTCodecRegistry<ITargetFinder> FINDERS = HTRegistryManager.create(Util.prefix("target_finders"), OTDTargetFinders::getDirectCodec);

    ResourceKey<ITargetFinder> RANGE_SKELETONS = create("range_skeletons");
    ResourceKey<ITargetFinder> RANGE_CREEPER = create("range_creeper");
    ResourceKey<ITargetFinder> AROUND_ENEMIES = create("around_enemies");

    static void register(BootstapContext<ITargetFinder> context){
        final HolderGetter<ITargetFilter> filters = OTDTargetFilters.registry().helper().lookup(context);
        context.register(RANGE_SKELETONS, new RangeFinder(true, 40, 40,
                filters.getOrThrow(OTDTargetFilters.SKELETON_TAGS)
        ));
        context.register(RANGE_CREEPER, new RangeFinder(true, 40, 40,
                filters.getOrThrow(OTDTargetFilters.CREEPER_ONLY)
        ));
        context.register(AROUND_ENEMIES, new RangeFinder(true, 10, 10,
                filters.getOrThrow(OTDTargetFilters.ENEMY_CLASS)
        ));
    }

    static ResourceKey<ITargetFinder> create(String name) {
        return registry().createKey(Util.prefix(name));
    }

    static Codec<Holder<ITargetFinder>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    static Codec<ITargetFinder> getDirectCodec(){
        return OTDTargetFinderTypes.registry().byNameCodec().dispatch(ITargetFinder::getType, ITargetFinderType::codec);
    }

    static IHTCodecRegistry<ITargetFinder> registry(){
        return FINDERS;
    }

}

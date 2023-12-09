package hungteen.opentd.common.impl.filter;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import hungteen.opentd.util.NBTUtil;
import hungteen.opentd.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: PangTeen
 * @create: 2023/12/8 23:17
 **/
public interface OTDTargetFilters {

    HTCodecRegistry<ITargetFilter> FILTERS = HTRegistryManager.create(Util.prefix("target_filters"), OTDTargetFilters::getDirectCodec);

    ResourceKey<ITargetFilter> ALL = create("all");
    ResourceKey<ITargetFilter> CREEPER_ONLY = create("creeper_only");
    ResourceKey<ITargetFilter> SKELETON_TAGS = create("skeleton_tags");
    ResourceKey<ITargetFilter> ENEMY_CLASS = create("enemy_class");
    ResourceKey<ITargetFilter> SUN_FLOWER_NBT = create("sun_flower_nbt");

    static void register(BootstapContext<ITargetFilter> context){
        context.register(ALL, AlwaysTrueFilter.INSTANCE);
        context.register(CREEPER_ONLY, new TypeTargetFilter(List.of(
                EntityType.CREEPER
        )));
        context.register(SKELETON_TAGS, new TagTargetFilter(
                Optional.of(EntityTypeTags.SKELETONS),
                Optional.empty()
        ));
        context.register(ENEMY_CLASS, new ClassFilter(
                ClassFilter.ENEMY
        ));
//        context.register(SUN_FLOWER_NBT, new EntityPredicateFilter(
//                EntityPredicate.ANY,
//                EntityPredicate.Builder.entity().nbt(new NbtPredicate(NBTUtil.sunflowerPredicate())).build()
//        ));
        context.register(SUN_FLOWER_NBT, new NBTTargetFilter(
                new CompoundTag(),
                NBTUtil.sunflowerPredicate()
        ));
    }

    static ResourceKey<ITargetFilter> create(String name) {
        return registry().createKey(Util.prefix(name));
    }

    static Codec<ITargetFilter> getDirectCodec(){
        return OTDTargetFilterTypes.registry().byNameCodec().dispatch(ITargetFilter::getType, ITargetFilterType::codec);
    }

    static Codec<Holder<ITargetFilter>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    static IHTCodecRegistry<ITargetFilter> registry() {
        return FILTERS;
    }
}

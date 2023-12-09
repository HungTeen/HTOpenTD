package hungteen.opentd.common.impl.requirement;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.common.impl.filter.OTDTargetFilters;
import hungteen.opentd.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:28
 **/
public interface OTDSummonRequirements {

    HTCodecRegistry<ISummonRequirement> REQUIREMENTS = HTRegistryManager.create(Util.prefix("summon_requirements"), OTDSummonRequirements::getDirectCodec);

    ResourceKey<ISummonRequirement> ON_CREEPER = create("on_creeper");
    ResourceKey<ISummonRequirement> ON_GRASS = create("on_grass");
    ResourceKey<ISummonRequirement> CREEPER_OR_GRASS = create("creeper_or_grass");
    ResourceKey<ISummonRequirement> AROUND_SUN_FLOWER = create("around_sun_flower");

    static void register(BootstapContext<ISummonRequirement> context) {
        final HolderGetter<ITargetFilter> filters = OTDTargetFilters.registry().helper().lookup(context);
        final HolderGetter<ISummonRequirement> requirements = registry().helper().lookup(context);
        context.register(ON_CREEPER, new EntityRequirement(
                Optional.of("Entity Not Fit !"),
                filters.getOrThrow(OTDTargetFilters.CREEPER_ONLY)
        ));
        context.register(ON_GRASS, new BlockRequirement(
                Optional.of("Block Not Fit !"),
                Optional.empty(),
                Optional.empty(),
                Optional.of(List.of(
                        Blocks.GRASS_BLOCK
                ))
        ));
        context.register(CREEPER_OR_GRASS, new OrRequirement(
                List.of(
                        requirements.getOrThrow(ON_CREEPER),
                        requirements.getOrThrow(ON_GRASS)
                ),
                Optional.empty()
        ));
        context.register(AROUND_SUN_FLOWER, new AroundEntityRequirement(
                3, 3, 1, 10, Optional.empty(),
                filters.getOrThrow(OTDTargetFilters.SUN_FLOWER_NBT)
        ));
    }

    static ResourceKey<ISummonRequirement> create(String name) {
        return registry().createKey(Util.prefix(name));
    }

    static Codec<ISummonRequirement> getDirectCodec() {
        return OTDRequirementTypes.registry().byNameCodec().dispatch(ISummonRequirement::getType, ISummonRequirementType::codec);
    }

    static Codec<Holder<ISummonRequirement>> getCodec() {
        return registry().getHolderCodec(getDirectCodec());
    }

    static IHTCodecRegistry<ISummonRequirement> registry() {
        return REQUIREMENTS;
    }

}

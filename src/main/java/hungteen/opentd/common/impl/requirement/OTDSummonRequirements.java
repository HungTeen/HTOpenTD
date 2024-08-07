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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

    ResourceKey<ISummonRequirement> NOTHING = create("nothing");
    ResourceKey<ISummonRequirement> TEN_POINTS = create("ten_points");
    ResourceKey<ISummonRequirement> ON_GRASS = create("on_grass");
    ResourceKey<ISummonRequirement> ON_CREEPER = create("on_creeper");
    ResourceKey<ISummonRequirement> AROUND_SUN_FLOWER = create("around_sun_flower");
    ResourceKey<ISummonRequirement> NEED_DIAMOND = create("need_diamond");
    ResourceKey<ISummonRequirement> CREEPER_OR_GRASS = create("creeper_or_grass");

    static void register(BootstapContext<ISummonRequirement> context) {
        final HolderGetter<ITargetFilter> filters = OTDTargetFilters.registry().helper().lookup(context);
        final HolderGetter<ISummonRequirement> requirements = registry().helper().lookup(context);
        context.register(NOTHING, NoRequirement.INSTANCE);
        context.register(TEN_POINTS, new ExperienceRequirement(
                Optional.of("Require 10 points xp !"),
                10, 0, 10, 0
        ));
        context.register(ON_GRASS, new BlockRequirement(
                Optional.of("Block Not Fit !"),
                Optional.empty(),
                Optional.empty(),
                Optional.of(List.of(
                        Blocks.GRASS_BLOCK
                ))
        ));
        context.register(ON_CREEPER, new EntityRequirement(
                Optional.of("Entity Not Fit !"),
                filters.getOrThrow(OTDTargetFilters.CREEPER_ONLY)
        ));
        context.register(AROUND_SUN_FLOWER, new AroundEntityRequirement(
                3, 3, 1, 10, Optional.empty(),
                filters.getOrThrow(OTDTargetFilters.SUN_FLOWER_NBT)
        ));
        context.register(NEED_DIAMOND, new InventoryRequirement(
                Optional.of("Cost 1 diamond !"),
                List.of(new ItemStack(Items.DIAMOND)),
                List.of(new ItemStack(Items.DIAMOND))
        ));
        context.register(CREEPER_OR_GRASS, new OrRequirement(
                List.of(
                        requirements.getOrThrow(ON_CREEPER),
                        requirements.getOrThrow(ON_GRASS)
                ),
                Optional.empty()
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

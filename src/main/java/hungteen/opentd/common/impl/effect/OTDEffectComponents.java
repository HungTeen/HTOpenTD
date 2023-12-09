package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.common.impl.filter.OTDTargetFilters;
import hungteen.opentd.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: PangTeen
 * @create: 2023/12/8 23:32
 **/
public interface OTDEffectComponents {

    HTCodecRegistry<IEffectComponent> EFFECTS = HTRegistryManager.create(Util.prefix("effects"), OTDEffectComponents::getDirectCodec);

    ResourceKey<IEffectComponent> SUMMON_XP_AROUND = create("summon_xp_around");
    ResourceKey<IEffectComponent> ATTRACT = create("attract");
    ResourceKey<IEffectComponent> PEA_DAMAGE = create("pea_damage");

    static void register(BootstapContext<IEffectComponent> context){
        final HolderGetter<ITargetFilter> filters = OTDTargetFilters.registry().helper().lookup(context);
        context.register(SUMMON_XP_AROUND, new SummonEffectComponent(
                1, 5, Optional.of(5), 16, true, true, EntityType.EXPERIENCE_ORB, new CompoundTag()
        ));
        context.register(ATTRACT, new AttractEffectComponent(
                filters.getOrThrow(OTDTargetFilters.ALL),
                Optional.of(filters.getOrThrow(OTDTargetFilters.ALL))
        ));
        context.register(PEA_DAMAGE, new DamageEffectComponent(false, 5F, 0));
    }

    static ResourceKey<IEffectComponent> create(String name) {
        return registry().createKey(Util.prefix(name));
    }

    static Codec<Holder<IEffectComponent>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    static Codec<IEffectComponent> getDirectCodec(){
        return OTDEffectComponentTypes.registry().byNameCodec().dispatch(IEffectComponent::getType, IEffectComponentType::codec);
    }

    static IHTCodecRegistry<IEffectComponent> registry(){
        return EFFECTS;
    }
}

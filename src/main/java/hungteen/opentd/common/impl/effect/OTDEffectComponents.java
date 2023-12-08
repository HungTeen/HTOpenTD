package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

/**
 * @program: HTOpenTD
 * @author: PangTeen
 * @create: 2023/12/8 23:32
 **/
public interface OTDEffectComponents {

    HTCodecRegistry<IEffectComponent> EFFECTS = HTRegistryManager.create(Util.prefix("effects"), OTDEffectComponents::getDirectCodec);

    static void register(BootstapContext<IEffectComponent> context){

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

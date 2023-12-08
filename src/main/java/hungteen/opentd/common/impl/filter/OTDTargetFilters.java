package hungteen.opentd.common.impl.filter;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import hungteen.opentd.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

/**
 * @program: HTOpenTD
 * @author: PangTeen
 * @create: 2023/12/8 23:17
 **/
public interface OTDTargetFilters {

    HTCodecRegistry<ITargetFilter> FILTERS = HTRegistryManager.create(Util.prefix("target_filters"), OTDTargetFilters::getDirectCodec);

    static void register(BootstapContext<ITargetFilter> context){

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

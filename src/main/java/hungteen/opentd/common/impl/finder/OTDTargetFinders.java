package hungteen.opentd.common.impl.finder;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.api.interfaces.ITargetFinder;
import hungteen.opentd.api.interfaces.ITargetFinderType;
import hungteen.opentd.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

/**
 * @program: HTOpenTD
 * @author: PangTeen
 * @create: 2023/12/8 23:23
 **/
public interface OTDTargetFinders {

    HTCodecRegistry<ITargetFinder> FINDERS = HTRegistryManager.create(Util.prefix("target_finders"), OTDTargetFinders::getDirectCodec);

    static void register(BootstapContext<ITargetFinder> context){

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

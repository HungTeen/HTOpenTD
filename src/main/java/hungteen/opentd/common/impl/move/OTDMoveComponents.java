package hungteen.opentd.common.impl.move;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.opentd.api.interfaces.IMoveComponent;
import hungteen.opentd.api.interfaces.IMoveComponentType;
import hungteen.opentd.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;

/**
 * @program: HTOpenTD
 * @author: PangTeen
 * @create: 2023/12/9 16:05
 **/
public interface OTDMoveComponents {

    HTCodecRegistry<IMoveComponent> MOVES = HTRegistryManager.create(Util.prefix("move_controllers"), OTDMoveComponents::getDirectCodec);

    ResourceKey<IMoveComponent> SHOOTER_FLYING = create("shooter_flying");

    static void register(BootstapContext<IMoveComponent> context){
        context.register(SHOOTER_FLYING, new FlyingMoveComponent(20, false));
    }

    static ResourceKey<IMoveComponent> create(String name) {
        return registry().createKey(Util.prefix(name));
    }

    static Codec<Holder<IMoveComponent>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    static Codec<IMoveComponent> getDirectCodec(){
        return OTDMoveTypes.registry().byNameCodec().dispatch(IMoveComponent::getType, IMoveComponentType::codec);
    }

    static IHTCodecRegistry<IMoveComponent> registry(){
        return MOVES;
    }

}

package hungteen.opentd.common.impl.move;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.IMoveComponent;
import hungteen.opentd.api.interfaces.IMoveComponentType;
import hungteen.opentd.util.Util;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/12 10:59
 */
public interface OTDMoveTypes {

    HTSimpleRegistry<IMoveComponentType<?>> MOVE_TYPES = HTRegistryManager.createSimple(Util.prefix("move_types"));

    IMoveComponentType<SwimmingMoveComponent> SWIMMING = register(new DefaultMoveType<>("swimming", SwimmingMoveComponent.CODEC));
    IMoveComponentType<FlyingMoveComponent> FLYING = register(new DefaultMoveType<>("flying", FlyingMoveComponent.CODEC));
    IMoveComponentType<OTDFlyingMoveComponent> OTD_FLYING = register(new DefaultMoveType<>("otd_flying", OTDFlyingMoveComponent.CODEC));

    static <T extends IMoveComponent> IMoveComponentType<T> register(IMoveComponentType<T> type){
        return registry().register(type);
    }

    static IHTSimpleRegistry<IMoveComponentType<?>> registry(){
        return MOVE_TYPES;
    }


    record DefaultMoveType<P extends IMoveComponent>(String name, Codec<P> codec) implements IMoveComponentType<P> {

        @Override
        public String getName() {
            return name();
        }

        @Override
        public String getModID() {
            return OpenTD.MOD_ID;
        }
    }
}

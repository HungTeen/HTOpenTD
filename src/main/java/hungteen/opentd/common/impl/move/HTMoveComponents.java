package hungteen.opentd.common.impl.move;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.IMoveComponent;
import hungteen.opentd.api.interfaces.IMoveComponentType;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/12 10:59
 */
public class HTMoveComponents {

    public static final HTSimpleRegistry<IMoveComponentType<?>> MOVE_TYPES = HTRegistryManager.create(OpenTD.prefix("move_types"));
    public static final HTCodecRegistry<IMoveComponent> MOVES = HTRegistryManager.create(IMoveComponent.class, "tower_defence/move_controllers", HTMoveComponents::getCodec, OpenTD.MOD_ID);

    /* Tower types */

    public static final IMoveComponentType<SwimmingMoveComponent> SWIMMING = register(new DefaultMoveType<>("swimming", SwimmingMoveComponent.CODEC));
    public static final IMoveComponentType<FlyingMoveComponent> FLYING = register(new DefaultMoveType<>("flying", FlyingMoveComponent.CODEC));
    public static final IMoveComponentType<OTDFlyingMoveComponent> OTD_FLYING = register(new DefaultMoveType<>("otd_flying", OTDFlyingMoveComponent.CODEC));

    public static <T extends IMoveComponent> IMoveComponentType<T> register(IMoveComponentType<T> type){
        registry().register(type);
        return type;
    }

    public static IHTSimpleRegistry<IMoveComponentType<?>> registry(){
        return MOVE_TYPES;
    }

    public static Codec<IMoveComponent> getCodec(){
        return MOVE_TYPES.byNameCodec().dispatch(IMoveComponent::getType, IMoveComponentType::codec);
    }

    protected record DefaultMoveType<P extends IMoveComponent>(String name, Codec<P> codec) implements IMoveComponentType<P> {

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

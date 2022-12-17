package hungteen.opentd.impl.tower;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.api.interfaces.ITowerComponentType;

import java.util.Arrays;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:28
 **/
public class HTTowerComponents {

    public static final HTSimpleRegistry<ITowerComponentType<?>> TOWER_TYPES = HTRegistryManager.create(HTLib.prefix("tower_type"));
    public static final HTCodecRegistry<ITowerComponent> TOWERS = HTRegistryManager.create(ITowerComponent.class, "tower_defence/tower_settings", HTTowerComponents::getCodec);

    /* Tower types */

    public static final ITowerComponentType<PVZPlantTower> PVZ_PLANT_TOWER = new DefaultTower<>("pvz_plant",  CenterAreaTowerment.CODEC);

    /* Towers */

//    public static final HTRegistryHolder<ITowerComponent> DEFAULT = TowerMENTS.innerRegister(
//            HTLib.prefix("default"), new CenterAreaTower(
//                    Vec3.ZERO, 0, 1, true, 0, true
//            )
//    );

    /**
     * {@link OpenTD#OpenTD()}
     */
    public static void registerStuffs(){
        Arrays.asList(PVZ_PLANT_TOWER).forEach(HTTowerComponents::registerTowerType);
    }

    public static void registerTowerType(ITowerComponentType<?> type){
        TOWER_TYPES.register(type);
    }

    public static Codec<ITowerComponent> getCodec(){
        return TOWER_TYPES.byNameCodec().dispatch(ITowerComponent::getType, ITowerComponentType::codec);
    }

    protected record DefaultTower<P extends ITowerComponent>(String name, Codec<P> codec) implements ITowerComponentType<P> {

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getModID() {
            return OpenTD.MOD_ID;
        }
    }
}

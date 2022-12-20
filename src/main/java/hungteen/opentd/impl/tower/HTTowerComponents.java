package hungteen.opentd.impl.tower;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryHolder;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.api.interfaces.ITowerComponentType;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:28
 **/
public class HTTowerComponents {

    public static final HTSimpleRegistry<ITowerComponentType<?>> TOWER_TYPES = HTRegistryManager.create(OpenTD.prefix("tower_type"));
    public static final HTCodecRegistry<ITowerComponent> TOWERS = HTRegistryManager.create(ITowerComponent.class, "tower_defence/tower_settings", HTTowerComponents::getCodec);

    /* Tower types */

    public static final ITowerComponentType<PVZPlantComponent> PVZ_PLANT_TOWER = new DefaultTower<>("pvz_plant",  PVZPlantComponent.CODEC);

    /* Towers */

    public static final HTRegistryHolder<ITowerComponent> PEA_SHOOTER = TOWERS.innerRegister(
            OpenTD.prefix("pea_shooter"), new PVZPlantComponent(
                    new PVZPlantComponent.PlantSettings(
                            PVZPlantComponent.GrowSettings.DEFAULT,
                            OpenTD.prefix("geo/pea_shooter.geo.json"),
                            OpenTD.prefix("textures/entity/pea_shooter.png"),
                            OpenTD.prefix("animations/pea_shooter.animation.json")
                    )
            )
    );

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

package hungteen.opentd.common.impl.tower;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.api.interfaces.ITowerComponentType;
import hungteen.opentd.util.Util;

public interface OTDTowerTypes {

    HTSimpleRegistry<ITowerComponentType<?>> TOWER_TYPES = HTRegistryManager.createSimple(Util.prefix("tower_type"));

    ITowerComponentType<PVZPlantComponent> PVZ_PLANT = register(new DefaultTower<>("pvz_plant", PVZPlantComponent.CODEC));
    ITowerComponentType<PlantHeroComponent> PLANT_HERO = register(new DefaultTower<>("plant_hero", PlantHeroComponent.CODEC));

    static IHTSimpleRegistry<ITowerComponentType<?>> registry(){
        return TOWER_TYPES;
    }

    static <T extends ITowerComponent> ITowerComponentType<T> register(ITowerComponentType<T> type){
        return registry().register(type);
    }

    record DefaultTower<P extends ITowerComponent>(String name, Codec<P> codec) implements ITowerComponentType<P> {

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

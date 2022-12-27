package hungteen.opentd.impl.tower;

import com.mojang.serialization.Codec;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryHolder;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.api.interfaces.ITowerComponentType;
import hungteen.opentd.impl.filter.TypeTargetFilter;
import hungteen.opentd.impl.finder.RangeFinder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Optional;

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
                            true,
                            OpenTD.prefix("geo/pea_shooter.geo.json"),
                            OpenTD.prefix("textures/entity/pea_shooter.png"),
                            OpenTD.prefix("animations/pea_shooter.animation.json")
                    ),
                    Arrays.asList(new PVZPlantComponent.TargetSettings(
                            1, 10,
                            new RangeFinder(true, 40, 40, new TypeTargetFilter(Arrays.asList(EntityType.CREEPER)))
                    )),
                    Optional.of(new PVZPlantComponent.ShootGoalSettings(
                            30, 20, 4, Optional.of(SoundEvents.SNOW_GOLEM_SHOOT),
                            Arrays.asList(
                                    new PVZPlantComponent.ShootSettings(
                                            false, 0, Vec3.ZERO, 0,
                                            new PVZPlantComponent.BulletSettings(
                                                    new TypeTargetFilter(Arrays.asList(EntityType.CREEPER)),
                                                    2F, 0.4F, 50, 0.01F, 0.99F, 0.2F,
                                                    OpenTD.prefix("geo/pea_shooter.geo.json"),
                                                    OpenTD.prefix("textures/entity/pea_shooter.png"),
                                                    OpenTD.prefix("animations/pea_shooter.animation.json")
                                            )
                                    )
                            )
                    ))
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

package hungteen.opentd.impl.tower;

import com.mojang.serialization.Codec;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryHolder;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.api.interfaces.ITowerComponentType;
import hungteen.opentd.impl.effect.DamageEffectComponent;
import hungteen.opentd.impl.effect.SplashEffectComponent;
import hungteen.opentd.impl.filter.OrTargetFilter;
import hungteen.opentd.impl.filter.TypeTargetFilter;
import hungteen.opentd.impl.finder.RangeFinder;
import net.minecraft.nbt.CompoundTag;
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
    public static final HTCodecRegistry<ITowerComponent> TOWERS = HTRegistryManager.create(ITowerComponent.class, "tower_defence/tower_settings", HTTowerComponents::getCodec, true);

    /* Tower types */

    public static final ITowerComponentType<PVZPlantComponent> PVZ_PLANT_TOWER = new DefaultTower<>("pvz_plant",  PVZPlantComponent.CODEC);

    /* Towers */

    public static final HTRegistryHolder<ITowerComponent> PEA_SHOOTER = TOWERS.innerRegister(
            OpenTD.prefix("pea_shooter"), new PVZPlantComponent(
                    new PVZPlantComponent.PlantSettings(
                            PVZPlantComponent.GrowSettings.DEFAULT,
                            true,
                            PVZPlantComponent.RenderSettings.DEFAULT
                    ),
                    Arrays.asList(new PVZPlantComponent.TargetSettings(
                            1, 10,
                            new RangeFinder(true, 40, 40, new TypeTargetFilter(Arrays.asList(EntityType.CREEPER)))
                    )),
                    Optional.of(new PVZPlantComponent.ShootGoalSettings(
                            30, 20, 4, Optional.of(SoundEvents.SNOW_GOLEM_SHOOT),
                            Arrays.asList(
                                    new PVZPlantComponent.ShootSettings(
                                            false, false, 0, Vec3.ZERO, 10, 15, 10,
                                            new PVZPlantComponent.BulletSettings(
                                                    new TypeTargetFilter(Arrays.asList(EntityType.CREEPER)),
                                                    Arrays.asList(
                                                            new DamageEffectComponent(5F),
                                                            new SplashEffectComponent(5, 5, true, 1F, new OrTargetFilter(Arrays.asList()))
                                                    ),
                                                    1F, 1, 30, 0.0001F, 0.99999F, false, false,
                                                    PVZPlantComponent.RenderSettings.DEFAULT
                                            )
                                    )
                            )
                    )),
                    Optional.empty()
            )
    );

    public static final HTRegistryHolder<ITowerComponent> SUN_FLOWER = TOWERS.innerRegister(
            OpenTD.prefix("sun_flower"), new PVZPlantComponent(
                    new PVZPlantComponent.PlantSettings(
                            PVZPlantComponent.GrowSettings.DEFAULT,
                            false,
                            PVZPlantComponent.RenderSettings.make(0.8F, 1F, 1F, "sun_flower")
                    ),
                    Arrays.asList(),
                    Optional.empty(),
                    Optional.of(
                            new PVZPlantComponent.GenGoalSettings(
                                    20, 10, 100, 100,
                                    Arrays.asList(
                                            new PVZPlantComponent.GenSettings(
                                                    false, 100, 200, 1,
                                                    EntityType.EXPERIENCE_ORB, new CompoundTag(), Vec3.ZERO, 0.25D, 0.3D
                                            )
                                    )
                            )
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

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
import hungteen.opentd.impl.effect.NBTEffectComponent;
import hungteen.opentd.impl.effect.SplashEffectComponent;
import hungteen.opentd.impl.effect.SummonEffectComponent;
import hungteen.opentd.impl.filter.OrTargetFilter;
import hungteen.opentd.impl.filter.TagTargetFilter;
import hungteen.opentd.impl.filter.TypeTargetFilter;
import hungteen.opentd.impl.finder.RangeFinder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
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
            OpenTD.prefix("pea_shooter_test"), new PVZPlantComponent(
                    new PVZPlantComponent.PlantSettings(
                            new CompoundTag(),
                            PVZPlantComponent.GrowSettings.DEFAULT,
                            OpenTD.prefix("pea_shooter_test"), 0, true, false,
                            PVZPlantComponent.RenderSettings.DEFAULT
                    ),
                    Arrays.asList(new PVZPlantComponent.TargetSetting(
                            1, 0.2F, true, 10000,
                            new RangeFinder(true, 40, 40,
                                    new OrTargetFilter(
                                            Arrays.asList(
                                                    new TypeTargetFilter(Arrays.asList(
                                                            EntityType.CREEPER,
                                                            EntityType.LLAMA,
                                                            EntityType.PANDA,
                                                            EntityType.BEE,
                                                            EntityType.SKELETON,
                                                            EntityType.PIG,
                                                            EntityType.CHICKEN,
                                                            EntityType.TRADER_LLAMA,
                                                            EntityType.VILLAGER,
                                                            EntityType.AXOLOTL,
                                                            EntityType.BAT,
                                                            EntityType.TURTLE,
                                                            EntityType.VEX,
                                                            EntityType.TROPICAL_FISH,
                                                            EntityType.ARMOR_STAND,
                                                            EntityType.SPIDER,
                                                            EntityType.MAGMA_CUBE
                                                    )),
                                                    new TypeTargetFilter(Arrays.asList(
                                                            EntityType.WITHER,
                                                            EntityType.WITCH,
                                                            EntityType.ALLAY,
                                                            EntityType.ENDER_DRAGON,
                                                            EntityType.ENDERMAN,
                                                            EntityType.ENDERMITE,
                                                            EntityType.ZOMBIE_VILLAGER,
                                                            EntityType.VINDICATOR
                                                    )),
                                                    new TagTargetFilter(
                                                            EntityTypeTags.SKELETONS
                                                    )
                                            )
                                    )
                            )
                    )),
                    Optional.of(new PVZPlantComponent.ShootGoalSetting(
                            0, 20, 10, 4, false, Optional.of(SoundEvents.SNOW_GOLEM_SHOOT),
                            Arrays.asList(
                                    new PVZPlantComponent.ShootSettings(
                                            false, false, 0, Vec3.ZERO, 10, 0, 10,
                                            new PVZPlantComponent.BulletSettings(
                                                    new TypeTargetFilter(Arrays.asList(EntityType.CREEPER)),
                                                    Arrays.asList(
                                                            new DamageEffectComponent(false, 5F),
                                                            new SplashEffectComponent(5, 5, true, new OrTargetFilter(Arrays.asList()), Arrays.asList(new DamageEffectComponent(false, 2F))),
                                                            new NBTEffectComponent(get(), false),
                                                            new SummonEffectComponent(3, 5, true, true, EntityType.EXPERIENCE_ORB, new CompoundTag())
                                                    ),
                                                    1F, 1, 300, 0.0001F, 0.99999F, false, false,
                                                    PVZPlantComponent.RenderSettings.make(0.5F, 0.5F, 0.6F, "pea_shooter"),
                                                    Optional.empty(),
                                                    Optional.of(
                                                            new PVZPlantComponent.ParticleSetting(
                                                                    ParticleTypes.FLAME, 1, true, new Vec3(1, 1, 1), new Vec3(0.1, 0.1, 0.1)
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )),
                    Optional.empty(),
                    Optional.empty(),
                    Optional.empty(),
                    Arrays.asList(),
                    Arrays.asList(),
                    Arrays.asList()
            )
    );

    public static final HTRegistryHolder<ITowerComponent> SUN_FLOWER = TOWERS.innerRegister(
            OpenTD.prefix("sun_flower_test"), new PVZPlantComponent(
                    new PVZPlantComponent.PlantSettings(
                            new CompoundTag(),
                            PVZPlantComponent.GrowSettings.DEFAULT,
                            OpenTD.prefix("pea_shooter_test"), 200, false, false,
                            PVZPlantComponent.RenderSettings.make(0.8F, 1F, 1F, "sun_flower")
                    ),
                    Arrays.asList(),
                    Optional.empty(),
                    Optional.of(
                            new PVZPlantComponent.GenGoalSetting(
                                    20, 10, 100, 100, false, Optional.of(SoundEvents.PLAYER_LEVELUP),
                                    Arrays.asList(
                                            new PVZPlantComponent.GenSettings(
                                                    false, 100, 200, 1,
                                                    EntityType.EXPERIENCE_ORB, new CompoundTag(), Vec3.ZERO, 0.25D, 0.3D
                                            )
                                    )
                            )
                    ),
                    Optional.empty(),
                    Optional.empty(),
                    Arrays.asList(),
                    Arrays.asList(),
                    Arrays.asList()
            )
    );

    private static CompoundTag get(){
        CompoundTag tag = new CompoundTag();
        tag.putShort("Fire", (short) 100);
        return tag;
    }

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

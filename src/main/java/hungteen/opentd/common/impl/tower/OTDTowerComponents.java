package hungteen.opentd.common.impl.tower;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.util.helper.registry.EntityHelper;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.api.interfaces.ITowerComponentType;
import hungteen.opentd.common.codec.*;
import hungteen.opentd.common.impl.effect.*;
import hungteen.opentd.common.impl.filter.*;
import hungteen.opentd.common.impl.finder.RangeFinder;
import hungteen.opentd.util.Util;
import net.minecraft.commands.CommandFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:28
 **/
public interface OTDTowerComponents {

    HTCodecRegistry<ITowerComponent> TOWERS = HTRegistryManager.create(Util.prefix("tower_settings"), OTDTowerComponents::getDirectCodec);

    ResourceKey<ITowerComponent> PEA_SHOOTER = create("pea_shooter");
    ResourceKey<ITowerComponent> SUN_FLOWER = create("sun_flower");


    static void register(BootstapContext<ITowerComponent> context){
        context.register(PEA_SHOOTER, new PVZPlantComponent(
                new PVZPlantComponent.PlantSetting(
                        TowerSetting.DEFAULT,
                        get1(),
                        PVZPlantComponent.GrowSettings.DEFAULT,
                        Util.prefix("pea_shooter_test"), 0, true, false,
                        false,
                        true,
                        RenderSetting.DEFAULT
                ),
                List.of(new TargetSetting(
                        1, 0.2F, true, 10000,
                        new RangeFinder(true, 40, 40,
                                new OrTargetFilter(
                                        Arrays.asList(
                                                new TypeTargetFilter(List.of(
                                                        EntityType.CREEPER
                                                )),
                                                new TagTargetFilter(
                                                        Optional.of(EntityTypeTags.SKELETONS),
                                                        Optional.empty()
                                                ),
                                                new TeamFilter(Optional.empty(), false, false, true)
                                        )
                                )
                        )
                )),
                Optional.of(new ShootGoalSetting(
                        0, 20, 10, 4, false, Optional.of(SoundEvents.SNOW_GOLEM_SHOOT),
                        List.of(
                                new ShootGoalSetting.ShootSetting(
                                        false, false, 0, Vec3.ZERO, 10, 0, 10,
                                        new BulletSetting(
                                                new OrTargetFilter(
                                                        Arrays.asList(
                                                                new TypeTargetFilter(List.of(
                                                                        EntityType.CREEPER
                                                                )),
                                                                new TeamFilter(Optional.empty(), false, false, true)
                                                        )
                                                ),
                                                new ListEffectComponent(Arrays.asList(
                                                        new DamageEffectComponent(false, 5F, 0),
                                                        new SplashEffectComponent(5, 5, true, new OrTargetFilter(List.of()), new DamageEffectComponent(false, 2F, 0.1F)),
                                                        new NBTEffectComponent(get(), false),
                                                        new RandomEffectComponent(10, 1, true, List.of(
                                                                Pair.of(
                                                                        new FunctionEffectComponent(false, new CommandFunction.CacheableFunction(Util.prefix("test"))),
                                                                        1
                                                                )
                                                        ))
                                                )),
                                                0.2F, 1, 300, 0.0001F, 0.99999F, 0.8F, false, true, true,
                                                RenderSetting.make(0.5F, 0.5F, 0.6F, false, "pea_shooter"),
                                                Optional.empty(),
                                                Optional.of(
                                                        new ParticleSetting(
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
                Optional.empty(),
                List.of(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        ));
        context.register(SUN_FLOWER, new PVZPlantComponent(
                new PVZPlantComponent.PlantSetting(
                        new TowerSetting(false, true, true, 30),
                        new CompoundTag(),
                        PVZPlantComponent.GrowSettings.DEFAULT,
                        Util.prefix("sun_flower_test"), 2000, false, false,
                        false, true,
                        RenderSetting.make(0.8F, 1F, 1F, false, "sun_flower")
                ),
                List.of(new TargetSetting(1, 0.2F, true, 10000,
                        new RangeFinder(true, 40, 40,
                                new TypeTargetFilter(List.of(
                                        EntityType.CREEPER
                                ))
                        ))
                ),
                Optional.empty(),
                Optional.of(
                        new GenGoalSetting(
                                20, 10, 100, 100, false, Optional.of(SoundEvents.PLAYER_LEVELUP),
                                List.of(
                                        new GenGoalSetting.GenSetting(
                                                false, 100, 200, 1,
                                                EntityType.EXPERIENCE_ORB, new CompoundTag(), Vec3.ZERO, 0.25D, 0.3D
                                        )
                                )
                        )
                ),
                Optional.empty(),
                Optional.empty(),
//                    Optional.of(
//                            new LaserGoalSetting(
//                                    50, 100, 20,new TypeTargetFilter(List.of(
//                                    EntityType.CREEPER
//                            )), new DamageEffectComponent(false, 1F, 0),
//                                    new DamageEffectComponent(false, 10F, 0),
//                                    1F, 10F, 20D, false, Optional.empty()
//                                    )
//                    ),
                Optional.empty(),
                List.of(
                        new ConstantAffectSetting(
                                20,
                                new RangeFinder(true, 10, 10, new ClassFilter(ClassFilter.ENEMY)),
                                new AttractEffectComponent(
                                        AlwaysTrueFilter.INSTANCE,
                                        Optional.of(AlwaysTrueFilter.INSTANCE)
                                )
                        )
                ),
                Optional.of(
                        new SummonEffectComponent(1, 5, Optional.of(5), 16, true, true, EntityType.EXPERIENCE_ORB, new CompoundTag())
                ),
                Optional.empty(),
                Optional.empty()
        ));
    }

    private static CompoundTag get() {
        CompoundTag tag = new CompoundTag();
        tag.putShort("Fire", (short) 100);
        return tag;
    }

    private static CompoundTag get1() {
        CompoundTag tag = new CompoundTag();
        {
            ListTag listtag = new ListTag();
            {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putString("Name", Objects.requireNonNull(EntityHelper.attribute().getKey((Attributes.MOVEMENT_SPEED)).toString()));
                compoundtag.putDouble("Base", 0.4F);
                listtag.add(compoundtag);
            }
            tag.put("Attributes", listtag);
        }
        return tag;
    }

    static ResourceKey<ITowerComponent> create(String name) {
        return registry().createKey(Util.prefix(name));
    }

    static Codec<ITowerComponent> getDirectCodec(){
        return OTDTowerTypes.registry().byNameCodec().dispatch(ITowerComponent::getType, ITowerComponentType::codec);
    }

    static Codec<Holder<ITowerComponent>> getCodec(){
        return registry().getHolderCodec(getDirectCodec());
    }

    static IHTCodecRegistry<ITowerComponent> registry() {
        return TOWERS;
    }

}

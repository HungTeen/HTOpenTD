package hungteen.opentd.common.impl.tower;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.util.helper.ColorHelper;
import hungteen.htlib.util.helper.registry.EntityHelper;
import hungteen.opentd.api.interfaces.*;
import hungteen.opentd.common.codec.*;
import hungteen.opentd.common.impl.OTDBulletSettings;
import hungteen.opentd.common.impl.effect.OTDEffectComponents;
import hungteen.opentd.common.impl.filter.OTDTargetFilters;
import hungteen.opentd.common.impl.finder.OTDTargetFinders;
import hungteen.opentd.util.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:28
 **/
public interface OTDTowerComponents {

    HTCodecRegistry<ITowerComponent> TOWERS = HTRegistryManager.create(Util.prefix("tower_settings"), OTDTowerComponents::getDirectCodec, OTDTowerComponents::getNetworkCodec);

    ResourceKey<ITowerComponent> PEA_SHOOTER = create("pea_shooter");
    ResourceKey<ITowerComponent> PEA_PULT = create("pea_pult");
    ResourceKey<ITowerComponent> DIAMOND_SHOOTER = create("diamond_shooter");
    ResourceKey<ITowerComponent> SUN_FLOWER = create("sun_flower");
    ResourceKey<ITowerComponent> NUT_FLOWER = create("nut_flower");
    ResourceKey<ITowerComponent> LASER_FLOWER = create("laser_flower");

    static void register(BootstapContext<ITowerComponent> context) {
        final HolderGetter<ITargetFilter> filters = OTDTargetFilters.registry().helper().lookup(context);
        final HolderGetter<ITargetFinder> finders = OTDTargetFinders.registry().helper().lookup(context);
        final HolderGetter<IEffectComponent> effects = OTDEffectComponents.registry().helper().lookup(context);
        final HolderGetter<BulletSetting> bullets = OTDBulletSettings.registry().helper().lookup(context);
        context.register(PEA_SHOOTER, new PVZPlantComponent(
                new PVZPlantComponent.PlantSetting(
                        TowerSetting.DEFAULT,
                        get1(),
                        PVZPlantComponent.GrowSettings.DEFAULT,
                        Util.prefix("pea_shooter"), 0, true, false,
                        false,
                        true,
                        RenderSetting.DEFAULT
                ),
                List.of(
                        new TargetSetting(1, 0.2F, true, 10000, finders.getOrThrow(OTDTargetFinders.RANGE_SKELETONS))
                ),
                Optional.of(new ShootGoalSetting(
                        0, 20, 10, 2, false, Optional.of(Holder.direct(SoundEvents.SNOW_GOLEM_SHOOT)),
                        List.of(
                                new ShootGoalSetting.ShootSetting(
                                        false, false, 0, Vec3.ZERO, 10, 0, 10,
                                        bullets.getOrThrow(OTDBulletSettings.SKELETON_PEA)
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
        context.register(PEA_PULT, new PVZPlantComponent(
                new PVZPlantComponent.PlantSetting(
                        TowerSetting.DEFAULT,
                        new CompoundTag(),
                        PVZPlantComponent.GrowSettings.DEFAULT,
                        Util.prefix("pea_pult"), 0, true, false,
                        false,
                        true,
                        RenderSetting.DEFAULT
                ),
                List.of(
                        new TargetSetting(1, 0.2F, true, 10000, finders.getOrThrow(OTDTargetFinders.RANGE_CREEPER))
                ),
                Optional.of(new ShootGoalSetting(
                        0, 20, 10, 2, false, Optional.of(Holder.direct(SoundEvents.CROSSBOW_HIT)),
                        List.of(
                                new ShootGoalSetting.ShootSetting(
                                        false, true, 0, Vec3.ZERO, 60, 0, 10,
                                        bullets.getOrThrow(OTDBulletSettings.CREEPER_PEA)
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
        context.register(DIAMOND_SHOOTER, new PVZPlantComponent(
                new PVZPlantComponent.PlantSetting(
                        TowerSetting.DEFAULT,
                        new CompoundTag(),
                        PVZPlantComponent.GrowSettings.DEFAULT,
                        Util.prefix("pea_shooter_test"), 0, true, false,
                        false,
                        true,
                        RenderSetting.DEFAULT
                ),
                List.of(
                        new TargetSetting(1, 0.2F, true, 10000, finders.getOrThrow(OTDTargetFinders.AROUND_ENEMIES))
                ),
                Optional.of(new ShootGoalSetting(
                        0, 120, 10, 1, false, Optional.of(Holder.direct(SoundEvents.BLAZE_SHOOT)),
                        List.of(
                                new ShootGoalSetting.ShootSetting(
                                        false, false, 0, Vec3.ZERO, 10, 0, 10,
                                        bullets.getOrThrow(OTDBulletSettings.DIAMOND_PEA)
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
                List.of(),
                Optional.empty(),
                Optional.of(
                        new GenGoalSetting(
                                20, 10, 100, 100, false, Optional.of(Holder.direct(SoundEvents.PLAYER_LEVELUP)),
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
                Optional.empty(),
                List.of(),
                Optional.of(effects.getOrThrow(OTDEffectComponents.SUMMON_XP_AROUND)),
                Optional.empty(),
                Optional.empty()
        ));
        context.register(NUT_FLOWER, new PVZPlantComponent(
                new PVZPlantComponent.PlantSetting(
                        new TowerSetting(false, true, true, 100),
                        new CompoundTag(),
                        PVZPlantComponent.GrowSettings.DEFAULT,
                        Util.prefix("sun_flower_test"), 2000, false, false,
                        false, true,
                        RenderSetting.make(0.8F, 1F, 1F, false, "sun_flower")
                ),
                List.of(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                List.of(
                        new ConstantAffectSetting(
                                20,
                                finders.getOrThrow(OTDTargetFinders.AROUND_ENEMIES),
                                effects.getOrThrow(OTDEffectComponents.ATTRACT_ALL_EFFECT)
                        )
                ),
                Optional.of(effects.getOrThrow(OTDEffectComponents.KNOCKBACK_EFFECT)),
                Optional.of(effects.getOrThrow(OTDEffectComponents.EXPLOSION_EFFECT)),
                Optional.empty()
        ));
        context.register(LASER_FLOWER, new PVZPlantComponent(
                new PVZPlantComponent.PlantSetting(
                        new TowerSetting(false, true, true, 10),
                        new CompoundTag(),
                        PVZPlantComponent.GrowSettings.DEFAULT,
                        Util.prefix("sun_flower_test"), 2000, false, false,
                        false, true,
                        RenderSetting.make(0.8F, 1F, 1F, false, "sun_flower")
                ),
                List.of(
                        new TargetSetting(1, 0.2F, true, 10000, finders.getOrThrow(OTDTargetFinders.AROUND_ENEMIES))
                ),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(new LaserGoalSetting(
                        50, 100, 20,
                        Optional.of(filters.getOrThrow(OTDTargetFilters.ENEMY_CLASS)),
                        Optional.of(effects.getOrThrow(OTDEffectComponents.FOUR_POINT_DAMAGE)),
                        Optional.of(effects.getOrThrow(OTDEffectComponents.SPLASH_DAMAGE_TO_ALL_ENTITIES)),
                        1F, 10F, 20D, false, Optional.empty(), Optional.of(ColorHelper.PURPLE.rgb())
                )),
                Optional.empty(),
                List.of(),
                Optional.empty(),
                Optional.of(
                        effects.getOrThrow(OTDEffectComponents.ENDERMAN_SOUND_EFFECT)
                ),
                Optional.empty()
        ));
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

    static Codec<ITowerComponent> getDirectCodec() {
        return OTDTowerTypes.registry().byNameCodec().dispatch(ITowerComponent::getType, ITowerComponentType::codec);
    }

    static Codec<ITowerComponent> getNetworkCodec() {
        return OTDTowerTypes.registry().byNameCodec().dispatch(ITowerComponent::getType, ITowerComponentType::networkCodec);
    }

    static Codec<Holder<ITowerComponent>> getCodec() {
        return registry().getHolderCodec(getDirectCodec());
    }

    static IHTCodecRegistry<ITowerComponent> registry() {
        return TOWERS;
    }

}

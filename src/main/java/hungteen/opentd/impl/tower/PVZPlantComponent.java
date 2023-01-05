package hungteen.opentd.impl.tower;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.*;
import hungteen.opentd.common.entity.OpenTDEntities;
import hungteen.opentd.common.entity.PlantEntity;
import hungteen.opentd.common.item.SummonTowerItem;
import hungteen.opentd.impl.effect.HTEffectComponents;
import hungteen.opentd.impl.filter.HTTargetFilters;
import hungteen.opentd.impl.finder.HTTargetFinders;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:40
 **/
public record PVZPlantComponent(PlantSettings plantSettings, List<TargetSettings> targetSettings,
                                Optional<ShootGoalSettings> shootGoalSettings,
                                Optional<GenGoalSettings> genGoalSettings,
                                List<EffectTargetSettings> hurtSettings,
                                List<EffectTargetSettings> dieSettings) implements ITowerComponent {

    public static final Codec<PVZPlantComponent> CODEC = RecordCodecBuilder.<PVZPlantComponent>mapCodec(instance -> instance.group(
            PlantSettings.CODEC.fieldOf("plant_settings").forGetter(PVZPlantComponent::plantSettings),
            TargetSettings.CODEC.listOf().optionalFieldOf("target_settings", Arrays.asList()).forGetter(PVZPlantComponent::targetSettings),
            Codec.optionalField("shoot_goal", ShootGoalSettings.CODEC).forGetter(PVZPlantComponent::shootGoalSettings),
            Codec.optionalField("gen_goal", GenGoalSettings.CODEC).forGetter(PVZPlantComponent::genGoalSettings),
            EffectTargetSettings.CODEC.listOf().optionalFieldOf("hurt_settings", Arrays.asList()).forGetter(PVZPlantComponent::hurtSettings),
            EffectTargetSettings.CODEC.listOf().optionalFieldOf("die_settings", Arrays.asList()).forGetter(PVZPlantComponent::dieSettings)
    ).apply(instance, PVZPlantComponent::new)).codec();

    @Override
    public Entity createEntity(ServerLevel level, Player player, ItemStack stack, BlockPos pos) {
        ItemStack itemStack = stack.copy();
        HTTowerComponents.getCodec().encodeStart(NbtOps.INSTANCE, SummonTowerItem.getTowerSettings(stack))
                .resultOrPartial(msg -> OpenTD.log().error(msg + " [ Create Entity ]"))
                .ifPresent(tag -> {
                    itemStack.getOrCreateTag().put(PlantEntity.PLANT_SETTINGS, tag);
                });
        itemStack.getOrCreateTag().putFloat(PlantEntity.YROT, player.getYRot());
        return OpenTDEntities.PLANT_ENTITY.get().spawn(level, itemStack, player, pos, MobSpawnType.SPAWN_EGG, false, false);
    }

    @Override
    public ITowerComponentType<?> getType() {
        return HTTowerComponents.PVZ_PLANT_TOWER;
    }

    public record RenderSettings(float width, float height, float scale, ResourceLocation modelLocation,
                                 ResourceLocation textureLocation, ResourceLocation animationLocation) {

        public static final RenderSettings DEFAULT = make(0.8F, 0.8F, 1F, "pea_shooter");

        public static RenderSettings make(float width, float height, float scale, String name) {
            return new RenderSettings(width, height, scale,
                    OpenTD.prefix("geo/" + name + ".geo.json"),
                    OpenTD.prefix("textures/entity/" + name + ".png"),
                    OpenTD.prefix("animations/" + name + ".animation.json")
            );
        }

        public static final Codec<RenderSettings> CODEC = RecordCodecBuilder.<RenderSettings>mapCodec(instance -> instance.group(
                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("width").forGetter(RenderSettings::width),
                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("height").forGetter(RenderSettings::height),
                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("scale").forGetter(RenderSettings::scale),
                ResourceLocation.CODEC.fieldOf("model").forGetter(RenderSettings::modelLocation),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(RenderSettings::textureLocation),
                ResourceLocation.CODEC.fieldOf("animation").forGetter(RenderSettings::animationLocation)
        ).apply(instance, RenderSettings::new)).codec();
    }

    public record PlantSettings(GrowSettings growSettings, boolean changeDirection, RenderSettings renderSettings) {

        public static final Codec<PlantSettings> CODEC = RecordCodecBuilder.<PlantSettings>mapCodec(instance -> instance.group(
                GrowSettings.CODEC.fieldOf("grow_settings").forGetter(PlantSettings::growSettings),
                Codec.BOOL.optionalFieldOf("change_direction", true).forGetter(PlantSettings::changeDirection),
                RenderSettings.CODEC.fieldOf("render_settings").forGetter(PlantSettings::renderSettings)
        ).apply(instance, PlantSettings::new)).codec();
    }

    public record GrowSettings(List<Float> scales, List<Integer> growDurations) {
        public static final GrowSettings DEFAULT = new GrowSettings(Arrays.asList(1F), Arrays.asList());

        public static final Codec<GrowSettings> CODEC = RecordCodecBuilder.<GrowSettings>mapCodec(instance -> instance.group(
                Codec.floatRange(0, Float.MAX_VALUE).listOf().fieldOf("scales").forGetter(GrowSettings::scales),
                Codec.intRange(0, Integer.MAX_VALUE).listOf().fieldOf("grow_durations").forGetter(GrowSettings::growDurations)
        ).apply(instance, GrowSettings::new)).codec();

        public int getMaxAge() {
            return Math.min(this.scales.size(), this.growDurations.size() + 1);
        }
    }

    public record TargetSettings(int priority, int chance, ITargetFinder targetFinder) {

        public static final Codec<TargetSettings> CODEC = RecordCodecBuilder.<TargetSettings>mapCodec(instance -> instance.group(
                Codec.intRange(0, Integer.MAX_VALUE).fieldOf("priority").forGetter(TargetSettings::priority),
                Codec.intRange(0, Integer.MAX_VALUE).fieldOf("chance").forGetter(TargetSettings::chance),
                HTTargetFinders.getCodec().fieldOf("target_finder").forGetter(TargetSettings::targetFinder)
        ).apply(instance, TargetSettings::new)).codec();

    }

    public record ShootGoalSettings(int coolDown, int startTick, int shootCount, Optional<SoundEvent> shootSound,
                                    List<ShootSettings> shootSettings) {
        public static final Codec<ShootGoalSettings> CODEC = RecordCodecBuilder.<ShootGoalSettings>mapCodec(instance -> instance.group(
                Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("cool_down", 30).forGetter(ShootGoalSettings::coolDown),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", 0).forGetter(ShootGoalSettings::startTick),
                Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("shoot_count", 1).forGetter(ShootGoalSettings::shootCount),
                Codec.optionalField("shoot_sound", SoundEvent.CODEC).forGetter(ShootGoalSettings::shootSound),
                ShootSettings.CODEC.listOf().fieldOf("shoot_settings").forGetter(ShootGoalSettings::shootSettings)
        ).apply(instance, ShootGoalSettings::new)).codec();
    }

    public record ShootSettings(boolean plantFoodOnly, boolean isParabola, int shootTick, Vec3 offset,
                                double verticalAngleLimit, double horizontalAngleOffset,
                                double pultHeight, BulletSettings bulletSettings) {
        public static final Codec<ShootSettings> CODEC = RecordCodecBuilder.<ShootSettings>mapCodec(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("plant_food_only", false).forGetter(ShootSettings::plantFoodOnly),
                Codec.BOOL.optionalFieldOf("is_parabola", false).forGetter(ShootSettings::isParabola),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("shoot_tick", 0).forGetter(ShootSettings::shootTick),
                Vec3.CODEC.optionalFieldOf("originOffset", Vec3.ZERO).forGetter(ShootSettings::offset),
                Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("vertical_angle_limit", 0D).forGetter(ShootSettings::verticalAngleLimit),
                Codec.DOUBLE.optionalFieldOf("horizontal_angle_offset", 0D).forGetter(ShootSettings::horizontalAngleOffset),
                Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("pult_height", 10D).forGetter(ShootSettings::pultHeight),
                BulletSettings.CODEC.fieldOf("bullet_settings").forGetter(ShootSettings::bulletSettings)
        ).apply(instance, ShootSettings::new)).codec();
    }

    public record BulletSettings(ITargetFilter targetFilter, List<IEffectComponent> effects, float bulletSpeed,
                                 int maxHitCount, int maxExistTick, float gravity, float slowDown, boolean ignoreBlock,
                                 boolean lockToTarget, RenderSettings renderSettings) {

        public static final Codec<BulletSettings> CODEC = RecordCodecBuilder.<BulletSettings>mapCodec(instance -> instance.group(
                HTTargetFilters.getCodec().fieldOf("target_filter").forGetter(BulletSettings::targetFilter),
                HTEffectComponents.getCodec().listOf().optionalFieldOf("effects", Arrays.asList()).forGetter(BulletSettings::effects),
                Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("bullet_speed", 0.15F).forGetter(BulletSettings::bulletSpeed),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max_hit_count", 1).forGetter(BulletSettings::maxHitCount),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max_exist_tick", 50).forGetter(BulletSettings::maxExistTick),
                Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("gravity", 0.03F).forGetter(BulletSettings::gravity),
                Codec.floatRange(0, 1F).optionalFieldOf("slow_down", 0.99F).forGetter(BulletSettings::slowDown),
                Codec.BOOL.optionalFieldOf("ignore_block", false).forGetter(BulletSettings::ignoreBlock),
                Codec.BOOL.optionalFieldOf("lock_to_target", false).forGetter(BulletSettings::lockToTarget),
                RenderSettings.CODEC.fieldOf("render_settings").forGetter(BulletSettings::renderSettings)
        ).apply(instance, BulletSettings::new)).codec();
    }

    public record GenGoalSettings(int coolDown, int startTick, int totalWeight, int emptyCD,
                                  List<GenSettings> genSettings) {
        public static final Codec<GenGoalSettings> CODEC = RecordCodecBuilder.<GenGoalSettings>mapCodec(instance -> instance.group(
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("cool_down", 20).forGetter(GenGoalSettings::coolDown),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", 10).forGetter(GenGoalSettings::startTick),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("total_weight", 0).forGetter(GenGoalSettings::totalWeight),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("empty_cd", 100).forGetter(GenGoalSettings::emptyCD),
                GenSettings.CODEC.listOf().fieldOf("productions").forGetter(GenGoalSettings::genSettings)
        ).apply(instance, GenGoalSettings::new)).codec();
    }

    public record GenSettings(boolean plantFoodOnly, int weight, int cooldown, int count, EntityType<?> entityType,
                              CompoundTag nbt, Vec3 offset, double horizontalSpeed, double verticalSpeed) {

        public static final Codec<GenSettings> CODEC = RecordCodecBuilder.<GenSettings>mapCodec(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("plant_food_only", false).forGetter(GenSettings::plantFoodOnly),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("weight", 10).forGetter(GenSettings::weight),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("cool_down", 500).forGetter(GenSettings::cooldown),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("count", 1).forGetter(GenSettings::count),
                ForgeRegistries.ENTITY_TYPES.getCodec().fieldOf("entity_type").forGetter(GenSettings::entityType),
                CompoundTag.CODEC.optionalFieldOf("nbt", new CompoundTag()).forGetter(GenSettings::nbt),
                Vec3.CODEC.optionalFieldOf("offset", Vec3.ZERO).forGetter(GenSettings::offset),
                Codec.DOUBLE.optionalFieldOf("horizontal_speed", 0.25D).forGetter(GenSettings::horizontalSpeed),
                Codec.DOUBLE.optionalFieldOf("vertical_speed", 0.3D).forGetter(GenSettings::verticalSpeed)
        ).apply(instance, GenSettings::new)).codec();
    }

    public record EffectTargetSettings(ITargetFilter targetFilter, List<IEffectComponent> effects) {

        public static final Codec<EffectTargetSettings> CODEC = RecordCodecBuilder.<EffectTargetSettings>mapCodec(instance -> instance.group(
                HTTargetFilters.getCodec().fieldOf("filter").forGetter(EffectTargetSettings::targetFilter),
                HTEffectComponents.getCodec().listOf().optionalFieldOf("effects", Arrays.asList()).forGetter(EffectTargetSettings::effects)
        ).apply(instance, EffectTargetSettings::new)).codec();
    }

    public record EffectSettings(IEffectComponent effect) {

        public static final Codec<EffectSettings> CODEC = RecordCodecBuilder.<EffectSettings>mapCodec(instance -> instance.group(
                HTEffectComponents.getCodec().fieldOf("effect").forGetter(EffectSettings::effect)
        ).apply(instance, EffectSettings::new)).codec();
    }

}

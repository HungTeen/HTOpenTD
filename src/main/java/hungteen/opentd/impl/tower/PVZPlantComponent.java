package hungteen.opentd.impl.tower;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.*;
import hungteen.opentd.common.entity.OpenTDEntities;
import hungteen.opentd.common.entity.PlantEntity;
import hungteen.opentd.impl.effect.HTEffectComponents;
import hungteen.opentd.impl.filter.HTTargetFilters;
import hungteen.opentd.impl.finder.HTTargetFinders;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:40
 **/
public record PVZPlantComponent(PlantSettings plantSettings, List<TargetSettings> targetSettings, Optional<ShootGoalSettings> shootGoalSettings) implements ITowerComponent {

    public static final Codec<PVZPlantComponent> CODEC = RecordCodecBuilder.<PVZPlantComponent>mapCodec(instance -> instance.group(
            PlantSettings.CODEC.fieldOf("plant_settings").forGetter(PVZPlantComponent::plantSettings),
            TargetSettings.CODEC.listOf().optionalFieldOf("target_settings", Arrays.asList()).forGetter(PVZPlantComponent::targetSettings),
            Codec.optionalField("shoot_goal", ShootGoalSettings.CODEC).forGetter(PVZPlantComponent::shootGoalSettings)
    ).apply(instance, PVZPlantComponent::new)).codec();

    @Override
    public Entity createEntity(ServerLevel level, Player player, ItemStack stack, BlockPos pos) {
        ItemStack itemStack = stack.copy();
        itemStack.getOrCreateTag().putFloat(PlantEntity.YROT, player.getYRot());
        return OpenTDEntities.PLANT_ENTITY.get().spawn(level, itemStack, player, pos, MobSpawnType.SPAWN_EGG, false, false);
    }

    @Override
    public ITowerComponentType<?> getType() {
        return HTTowerComponents.PVZ_PLANT_TOWER;
    }

    public record RenderSettings(float width, float height, float scale, ResourceLocation modelLocation, ResourceLocation textureLocation, ResourceLocation animationLocation){

        public static final RenderSettings DEFAULT = new RenderSettings(
                0.8F, 0.8F, 1F,
                OpenTD.prefix("geo/pea_shooter.geo.json"),
                OpenTD.prefix("textures/entity/pea_shooter.png"),
                OpenTD.prefix("animations/pea_shooter.animation.json")
        );

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

    public record GrowSettings(List<Float> scales, List<Integer> growDurations){
        public static final GrowSettings DEFAULT = new GrowSettings(Arrays.asList(1F), Arrays.asList());

        public static final Codec<GrowSettings> CODEC = RecordCodecBuilder.<GrowSettings>mapCodec(instance -> instance.group(
                Codec.floatRange(0, Float.MAX_VALUE).listOf().fieldOf("scales").forGetter(GrowSettings::scales),
                Codec.intRange(0, Integer.MAX_VALUE).listOf().fieldOf("grow_durations").forGetter(GrowSettings::growDurations)
                ).apply(instance, GrowSettings::new)).codec();

        public int getMaxAge(){
            return Math.min(this.scales.size(), this.growDurations.size() + 1);
        }
    }

    public record TargetSettings(int priority, int chance, ITargetFinder targetFinder){

        public static final Codec<TargetSettings> CODEC = RecordCodecBuilder.<TargetSettings>mapCodec(instance -> instance.group(
                Codec.intRange(0, Integer.MAX_VALUE).fieldOf("priority").forGetter(TargetSettings::priority),
                Codec.intRange(0, Integer.MAX_VALUE).fieldOf("chance").forGetter(TargetSettings::chance),
                HTTargetFinders.getCodec().fieldOf("target_finder").forGetter(TargetSettings::targetFinder)
                ).apply(instance, TargetSettings::new)).codec();

    }

    public record ShootGoalSettings(int coolDown, int startTick, int shootCount, Optional<SoundEvent> shootSound, List<ShootSettings> shootSettings) {
        public static final Codec<ShootGoalSettings> CODEC = RecordCodecBuilder.<ShootGoalSettings>mapCodec(instance -> instance.group(
                Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("cool_down", 30).forGetter(ShootGoalSettings::coolDown),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", 0).forGetter(ShootGoalSettings::startTick),
                Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("shoot_count", 1).forGetter(ShootGoalSettings::shootCount),
                Codec.optionalField("shoot_sound", SoundEvent.CODEC).forGetter(ShootGoalSettings::shootSound),
                ShootSettings.CODEC.listOf().fieldOf("shoot_settings").forGetter(ShootGoalSettings::shootSettings)
        ).apply(instance, ShootGoalSettings::new)).codec();
    }

    public record ShootSettings(boolean plantFoodOnly, int shootTick, Vec3 offset, double angleOffset, BulletSettings bulletSettings) {
        public static final Codec<ShootSettings> CODEC = RecordCodecBuilder.<ShootSettings>mapCodec(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("plant_food_only", false).forGetter(ShootSettings::plantFoodOnly),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("shoot_tick", 0).forGetter(ShootSettings::shootTick),
                Vec3.CODEC.optionalFieldOf("originOffset", Vec3.ZERO).forGetter(ShootSettings::offset),
                Codec.DOUBLE.optionalFieldOf("angle_offset", 0D).forGetter(ShootSettings::angleOffset),
                BulletSettings.CODEC.fieldOf("bullet_settings").forGetter(ShootSettings::bulletSettings)
        ).apply(instance, ShootSettings::new)).codec();
    }

    public record BulletSettings(ITargetFilter targetFilter, List<IEffectComponent> effects, float bulletSpeed, int maxHitCount, int maxExistTick, float gravity, float slowDown, boolean ignoreBlock, RenderSettings renderSettings) {

        public static final Codec<BulletSettings> CODEC = RecordCodecBuilder.<BulletSettings>mapCodec(instance -> instance.group(
                HTTargetFilters.getCodec().fieldOf("target_filter").forGetter(BulletSettings::targetFilter),
                HTEffectComponents.getCodec().listOf().optionalFieldOf("effects", Arrays.asList()).forGetter(BulletSettings::effects),
                Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("bullet_speed", 0.15F).forGetter(BulletSettings::bulletSpeed),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max_hit_count", 1).forGetter(BulletSettings::maxHitCount),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max_exist_tick", 50).forGetter(BulletSettings::maxExistTick),
                Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("gravity", 0.03F).forGetter(BulletSettings::gravity),
                Codec.floatRange(0, 1F).optionalFieldOf("slow_down", 0.99F).forGetter(BulletSettings::slowDown),
                Codec.BOOL.optionalFieldOf("ignore_block", false).forGetter(BulletSettings::ignoreBlock),
                RenderSettings.CODEC.fieldOf("render_settings").forGetter(BulletSettings::renderSettings)
        ).apply(instance, BulletSettings::new)).codec();
    }
}

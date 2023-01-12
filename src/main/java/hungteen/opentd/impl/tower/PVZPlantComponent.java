package hungteen.opentd.impl.tower;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.helper.ParticleHelper;
import hungteen.htlib.util.helper.RandomHelper;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.*;
import hungteen.opentd.common.entity.OpenTDEntities;
import hungteen.opentd.common.entity.PlantEntity;
import hungteen.opentd.common.item.SummonTowerItem;
import hungteen.opentd.impl.effect.HTEffectComponents;
import hungteen.opentd.impl.filter.HTTargetFilters;
import hungteen.opentd.impl.finder.HTTargetFinders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:40
 **/
public record PVZPlantComponent(PlantSettings plantSetting, List<TargetSetting> targetSettings,
                                Optional<ShootGoalSetting> shootGoalSetting,
                                Optional<GenGoalSetting> genGoalSetting,
                                Optional<AttackGoalSetting> attackGoalSetting,
                                Optional<InstantEffectSetting> instantEffectSetting,
                                List<ConstantAffectSetting> constantAffectSettings,
                                List<EffectTargetSetting> hurtSettings,
                                List<EffectTargetSetting> dieSettings) implements ITowerComponent {

    public static final Codec<PVZPlantComponent> CODEC = RecordCodecBuilder.<PVZPlantComponent>mapCodec(instance -> instance.group(
            PlantSettings.CODEC.fieldOf("plant_setting").forGetter(PVZPlantComponent::plantSetting),
            TargetSetting.CODEC.listOf().optionalFieldOf("target_settings", Arrays.asList()).forGetter(PVZPlantComponent::targetSettings),
            Codec.optionalField("shoot_goal", ShootGoalSetting.CODEC).forGetter(PVZPlantComponent::shootGoalSetting),
            Codec.optionalField("gen_goal", GenGoalSetting.CODEC).forGetter(PVZPlantComponent::genGoalSetting),
            Codec.optionalField("attack_goal", AttackGoalSetting.CODEC).forGetter(PVZPlantComponent::attackGoalSetting),
            Codec.optionalField("instant_setting", InstantEffectSetting.CODEC).forGetter(PVZPlantComponent::instantEffectSetting),
            ConstantAffectSetting.CODEC.listOf().optionalFieldOf("constant_settings", Arrays.asList()).forGetter(PVZPlantComponent::constantAffectSettings),
            EffectTargetSetting.CODEC.listOf().optionalFieldOf("hurt_settings", Arrays.asList()).forGetter(PVZPlantComponent::hurtSettings),
            EffectTargetSetting.CODEC.listOf().optionalFieldOf("die_settings", Arrays.asList()).forGetter(PVZPlantComponent::dieSettings)
    ).apply(instance, PVZPlantComponent::new)).codec();

    @Override
    public Entity createEntity(ServerLevel level, Player player, ItemStack stack, BlockPos pos) {
        final ItemStack itemStack = stack.copy();
        final ITowerComponent towerComponent = SummonTowerItem.getTowerSettings(stack);
        HTTowerComponents.getCodec().encodeStart(NbtOps.INSTANCE, towerComponent)
                .resultOrPartial(msg -> OpenTD.log().error(msg + " [ Create Entity ]"))
                .ifPresent(tag -> {
                    itemStack.getOrCreateTag().put(PlantEntity.PLANT_SETTINGS, tag);
                });
        if(towerComponent instanceof PVZPlantComponent){
            itemStack.getOrCreateTag().put(SummonTowerItem.ENTITY_TAG, ((PVZPlantComponent) towerComponent).plantSetting().extraNBT());
        }
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
                Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("scale", 1F).forGetter(RenderSettings::scale),
                ResourceLocation.CODEC.fieldOf("model").forGetter(RenderSettings::modelLocation),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(RenderSettings::textureLocation),
                ResourceLocation.CODEC.fieldOf("animation").forGetter(RenderSettings::animationLocation)
        ).apply(instance, RenderSettings::new)).codec();
    }

    public record PlantSettings(CompoundTag extraNBT, GrowSettings growSetting, boolean changeDirection, boolean pushable, RenderSettings renderSetting) {

        public static final Codec<PlantSettings> CODEC = RecordCodecBuilder.<PlantSettings>mapCodec(instance -> instance.group(
                CompoundTag.CODEC.optionalFieldOf("extra_nbt", new CompoundTag()).forGetter(PlantSettings::extraNBT),
                GrowSettings.CODEC.optionalFieldOf("grow_setting", GrowSettings.DEFAULT).forGetter(PlantSettings::growSetting),
                Codec.BOOL.optionalFieldOf("change_direction", true).forGetter(PlantSettings::changeDirection),
                Codec.BOOL.optionalFieldOf("pushable", false).forGetter(PlantSettings::pushable),
                RenderSettings.CODEC.fieldOf("render_setting").forGetter(PlantSettings::renderSetting)
        ).apply(instance, PlantSettings::new)).codec();
    }

    public record GrowSettings(List<Float> scales, List<Integer> growDurations, Optional<SoundEvent> growSound) {
        public static final GrowSettings DEFAULT = new GrowSettings(Arrays.asList(1F), Arrays.asList(), Optional.empty());

        public static final Codec<GrowSettings> CODEC = RecordCodecBuilder.<GrowSettings>mapCodec(instance -> instance.group(
                Codec.floatRange(0, Float.MAX_VALUE).listOf().fieldOf("scales").forGetter(GrowSettings::scales),
                Codec.intRange(0, Integer.MAX_VALUE).listOf().fieldOf("grow_durations").forGetter(GrowSettings::growDurations),
                Codec.optionalField("grow_sound", SoundEvent.CODEC).forGetter(GrowSettings::growSound)
        ).apply(instance, GrowSettings::new)).codec();

        public int getMaxAge() {
            return Math.min(this.scales.size(), this.growDurations.size() + 1);
        }
    }

    public record TargetSetting(int priority, float chance, ITargetFinder targetFinder) {

        public static final Codec<TargetSetting> CODEC = RecordCodecBuilder.<TargetSetting>mapCodec(instance -> instance.group(
                Codec.intRange(0, Integer.MAX_VALUE).fieldOf("priority").forGetter(TargetSetting::priority),
                Codec.floatRange(0, 1).fieldOf("chance").forGetter(TargetSetting::chance),
                HTTargetFinders.getCodec().fieldOf("target_finder").forGetter(TargetSetting::targetFinder)
        ).apply(instance, TargetSetting::new)).codec();

    }

    public record ShootGoalSetting(int duration, int coolDown, int startTick, int shootCount, boolean needRest, Optional<SoundEvent> shootSound,
                                   List<ShootSettings> shootSettings) {
        public static final Codec<ShootGoalSetting> CODEC = RecordCodecBuilder.<ShootGoalSetting>mapCodec(instance -> instance.group(
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("duration", 0).forGetter(ShootGoalSetting::duration),
                Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("cool_down", 30).forGetter(ShootGoalSetting::coolDown),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", 20).forGetter(ShootGoalSetting::startTick),
                Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("shoot_count", 1).forGetter(ShootGoalSetting::shootCount),
                Codec.BOOL.optionalFieldOf("need_rest", false).forGetter(ShootGoalSetting::needRest),
                Codec.optionalField("shoot_sound", SoundEvent.CODEC).forGetter(ShootGoalSetting::shootSound),
                ShootSettings.CODEC.listOf().fieldOf("shoot_settings").forGetter(ShootGoalSetting::shootSettings)
        ).apply(instance, ShootGoalSetting::new)).codec();
    }

    public record ShootSettings(boolean plantFoodOnly, boolean isParabola, int shootTick, Vec3 offset,
                                double verticalAngleLimit, double horizontalAngleOffset,
                                double pultHeight, BulletSettings bulletSettings) {
        public static final Codec<ShootSettings> CODEC = RecordCodecBuilder.<ShootSettings>mapCodec(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("plant_food_only", false).forGetter(ShootSettings::plantFoodOnly),
                Codec.BOOL.optionalFieldOf("is_parabola", false).forGetter(ShootSettings::isParabola),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("shoot_tick", 0).forGetter(ShootSettings::shootTick),
                Vec3.CODEC.optionalFieldOf("origin_offset", Vec3.ZERO).forGetter(ShootSettings::offset),
                Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("vertical_angle_limit", 0D).forGetter(ShootSettings::verticalAngleLimit),
                Codec.DOUBLE.optionalFieldOf("horizontal_angle_offset", 0D).forGetter(ShootSettings::horizontalAngleOffset),
                Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("pult_height", 10D).forGetter(ShootSettings::pultHeight),
                BulletSettings.CODEC.fieldOf("bullet_settings").forGetter(ShootSettings::bulletSettings)
        ).apply(instance, ShootSettings::new)).codec();
    }

    public record BulletSettings(ITargetFilter targetFilter, List<IEffectComponent> effects, float bulletSpeed,
                                 int maxHitCount, int maxExistTick, float gravity, float slowDown, boolean ignoreBlock,
                                 boolean lockToTarget, RenderSettings renderSettings, Optional<ParticleSetting> hitParticle, Optional<ParticleSetting> trailParticle) {

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
                RenderSettings.CODEC.fieldOf("render_setting").forGetter(BulletSettings::renderSettings),
                Codec.optionalField("hit_particle", ParticleSetting.CODEC).forGetter(BulletSettings::hitParticle),
                Codec.optionalField("trail_particle", ParticleSetting.CODEC).forGetter(BulletSettings::trailParticle)
        ).apply(instance, BulletSettings::new)).codec();
    }

    public record GenGoalSetting(int coolDown, int startTick, int totalWeight, int emptyCD, boolean needRest,
                                 Optional<SoundEvent> genSound, List<GenSettings> genSettings) {
        public static final Codec<GenGoalSetting> CODEC = RecordCodecBuilder.<GenGoalSetting>mapCodec(instance -> instance.group(
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("cool_down", 20).forGetter(GenGoalSetting::coolDown),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", 10).forGetter(GenGoalSetting::startTick),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("total_weight", 0).forGetter(GenGoalSetting::totalWeight),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("empty_cd", 100).forGetter(GenGoalSetting::emptyCD),
                Codec.BOOL.optionalFieldOf("need_rest", false).forGetter(GenGoalSetting::needRest),
                Codec.optionalField("gen_sound", SoundEvent.CODEC).forGetter(GenGoalSetting::genSound),
                GenSettings.CODEC.listOf().fieldOf("productions").forGetter(GenGoalSetting::genSettings)
        ).apply(instance, GenGoalSetting::new)).codec();
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

    public record AttackGoalSetting(int duration, int coolDown, int startTick, boolean needRest, double distance, Optional<SoundEvent> attackSound,
                                    List<IEffectComponent> effects) {
        public static final Codec<AttackGoalSetting> CODEC = RecordCodecBuilder.<AttackGoalSetting>mapCodec(instance -> instance.group(
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("duration", 0).forGetter(AttackGoalSetting::duration),
                Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("cool_down", 30).forGetter(AttackGoalSetting::coolDown),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", 20).forGetter(AttackGoalSetting::startTick),
                Codec.BOOL.optionalFieldOf("need_rest", false).forGetter(AttackGoalSetting::needRest),
                Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("distance", 3D).forGetter(AttackGoalSetting::distance),
                Codec.optionalField("attack_sound", SoundEvent.CODEC).forGetter(AttackGoalSetting::attackSound),
                HTEffectComponents.getCodec().listOf().fieldOf("effects").forGetter(AttackGoalSetting::effects)
        ).apply(instance, AttackGoalSetting::new)).codec();
    }

    public record InstantEffectSetting(boolean needClose, double closeRange, int finalAge, ITargetFilter targetFilter, Optional<SoundEvent> instantSound, List<IEffectComponent> effects) {
        public static final Codec<InstantEffectSetting> CODEC = RecordCodecBuilder.<InstantEffectSetting>mapCodec(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("need_close", false).forGetter(InstantEffectSetting::needClose),
                Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("close_range", 3D).forGetter(InstantEffectSetting::closeRange),
                Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("final_age", 2).forGetter(InstantEffectSetting::finalAge),
                HTTargetFilters.getCodec().fieldOf("target_filter").forGetter(InstantEffectSetting::targetFilter),
                Codec.optionalField("instant_sound", SoundEvent.CODEC).forGetter(InstantEffectSetting::instantSound),
                HTEffectComponents.getCodec().listOf().fieldOf("effects").forGetter(InstantEffectSetting::effects)
        ).apply(instance, InstantEffectSetting::new)).codec();
    }

    public record ConstantAffectSetting(int cd, ITargetFinder targetFinder, List<EffectTargetSetting> effectSettings) {

        public static final Codec<ConstantAffectSetting> CODEC = RecordCodecBuilder.<ConstantAffectSetting>mapCodec(instance -> instance.group(
                Codec.intRange(1, Integer.MAX_VALUE).fieldOf("cd").forGetter(ConstantAffectSetting::cd),
                HTTargetFinders.getCodec().fieldOf("target_finder").forGetter(ConstantAffectSetting::targetFinder),
                EffectTargetSetting.CODEC.listOf().optionalFieldOf("effects", Arrays.asList()).forGetter(ConstantAffectSetting::effectSettings)
        ).apply(instance, ConstantAffectSetting::new)).codec();
    }

    public record EffectTargetSetting(ITargetFilter targetFilter, List<IEffectComponent> effects) {

        public static final Codec<EffectTargetSetting> CODEC = RecordCodecBuilder.<EffectTargetSetting>mapCodec(instance -> instance.group(
                HTTargetFilters.getCodec().fieldOf("filter").forGetter(EffectTargetSetting::targetFilter),
                HTEffectComponents.getCodec().listOf().optionalFieldOf("effects", Arrays.asList()).forGetter(EffectTargetSetting::effects)
        ).apply(instance, EffectTargetSetting::new)).codec();
    }

    public record ParticleSetting(ParticleType<?> particleType, int amount, boolean isRandom, Vec3 offset, Vec3 speed) {

        public static final Codec<ParticleSetting> CODEC = RecordCodecBuilder.<ParticleSetting>mapCodec(instance -> instance.group(
                ForgeRegistries.PARTICLE_TYPES.getCodec().fieldOf("particle_type").forGetter(ParticleSetting::particleType),
                Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("amount", 1).forGetter(ParticleSetting::amount),
                Codec.BOOL.optionalFieldOf("is_random", true).forGetter(ParticleSetting::isRandom),
                Vec3.CODEC.optionalFieldOf("offset", Vec3.ZERO).forGetter(ParticleSetting::offset),
                Vec3.CODEC.optionalFieldOf("speed", Vec3.ZERO).forGetter(ParticleSetting::speed)
        ).apply(instance, ParticleSetting::new)).codec();

        public Optional<ParticleOptions> getType(){
            if(particleType() instanceof SimpleParticleType){
                return Optional.of((SimpleParticleType) particleType());
            }
            return Optional.empty();
        }

        public void spawn(Level level, Vec3 center, RandomSource rand){
            if(this.getType().isPresent()){
                for(int i = 0; i < amount(); ++ i){
                    Vec3 pos = center;
                    Vec3 speed = speed();
                    if(isRandom()){
                        pos = center.add(RandomHelper.doubleRange(rand, offset().x()), RandomHelper.doubleRange(rand, offset().y()), RandomHelper.doubleRange(rand, offset().z()));
                        speed = new Vec3(RandomHelper.doubleRange(rand, speed().x()), RandomHelper.doubleRange(rand, speed().y()), RandomHelper.doubleRange(rand, speed().z()));
                    }
                    ParticleHelper.spawnParticles(level, getType().get(), pos, speed.x(), speed.y(), speed.z());
                }
            }
        }
    }

}

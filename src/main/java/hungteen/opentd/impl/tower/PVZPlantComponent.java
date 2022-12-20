package hungteen.opentd.impl.tower;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.api.interfaces.ITowerComponentType;
import hungteen.opentd.common.entity.OpenTDEntities;
import hungteen.opentd.common.entity.PlantEntity;
import hungteen.opentd.impl.target.HTTargetFilters;
import hungteen.opentd.impl.work.HTWorkComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
public record PVZPlantComponent(PlantSettings plantSettings, List<TargetSettings> targetSettings, Optional<ShootSettings> shootSettingsOpt) implements ITowerComponent {

    public static final Codec<PVZPlantComponent> CODEC = RecordCodecBuilder.<PVZPlantComponent>mapCodec(instance -> instance.group(
            PlantSettings.CODEC.fieldOf("plant_settings").forGetter(PVZPlantComponent::plantSettings),
            TargetSettings.CODEC.listOf().optionalFieldOf("target_settings", Arrays.asList()).forGetter(PVZPlantComponent::targetSettings),
            ShootSettings.CODEC.listOf().optionalFieldOf("shoot_settings", Arrays.as)
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

    public record PlantSettings(GrowSettings growSettings, ResourceLocation modelLocation, ResourceLocation textureLocation, ResourceLocation animationLocation) {

        public static final Codec<PlantSettings> CODEC = RecordCodecBuilder.<PlantSettings>mapCodec(instance -> instance.group(
                GrowSettings.CODEC.fieldOf("grow_settings").forGetter(PlantSettings::growSettings),
                ResourceLocation.CODEC.fieldOf("model").forGetter(PlantSettings::modelLocation),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(PlantSettings::textureLocation),
                ResourceLocation.CODEC.fieldOf("animation").forGetter(PlantSettings::animationLocation)
        ).apply(instance, PlantSettings::new)).codec();
    }

    public record GrowSettings(float width, float height, List<Float> scales, List<Integer> growDurations){
        public static final GrowSettings DEFAULT = new GrowSettings(0.8F, 1.5F, Arrays.asList(1F), Arrays.asList());
        public static final Codec<GrowSettings> CODEC = RecordCodecBuilder.<GrowSettings>mapCodec(instance -> instance.group(
                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("width").forGetter(GrowSettings::width),
                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("height").forGetter(GrowSettings::height),
                Codec.floatRange(0, Float.MAX_VALUE).listOf().fieldOf("scales").forGetter(GrowSettings::scales),
                Codec.intRange(0, Integer.MAX_VALUE).listOf().fieldOf("grow_durations").forGetter(GrowSettings::growDurations)
                ).apply(instance, GrowSettings::new)).codec();

        public int getMaxAge(){
            return Math.min(this.scales.size(), this.growDurations.size() + 1);
        }
    }

    public record TargetSettings(int priority, int chance, boolean checkSight, float width, float height, List<ITargetFilter> targetFilters){

        public static final Codec<TargetSettings> CODEC = RecordCodecBuilder.<TargetSettings>mapCodec(instance -> instance.group(
                Codec.intRange(0, Integer.MAX_VALUE).fieldOf("priority").forGetter(TargetSettings::priority),
                Codec.intRange(0, Integer.MAX_VALUE).fieldOf("chance").forGetter(TargetSettings::chance),
                Codec.BOOL.optionalFieldOf("check_sight", true).forGetter(TargetSettings::checkSight),
                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("width").forGetter(TargetSettings::width),
                Codec.floatRange(0, Float.MAX_VALUE).fieldOf("height").forGetter(TargetSettings::height),
                HTTargetFilters.getCodec().listOf().optionalFieldOf("target_filters", Arrays.asList()).forGetter(TargetSettings::targetFilters)
        ).apply(instance, TargetSettings::new)).codec();

        public boolean match(Mob owner, Entity target){
            return this.targetFilters().stream().allMatch(l -> l.match(owner, target));
        }

    }

    public record ShootSettings(int shootTick, Vec3 offset, double angleOffset, BulletSettings bulletSettings) {
        public static final Codec<ShootSettings> CODEC = RecordCodecBuilder.<ShootSettings>mapCodec(instance -> instance.group(
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("shoot_tick", 0).forGetter(ShootSettings::shootTick),
                Vec3.CODEC.optionalFieldOf("offset", Vec3.ZERO).forGetter(ShootSettings::offset),
                Codec.DOUBLE.optionalFieldOf("angle_offset", 0D).forGetter(ShootSettings::angleOffset),
                BulletSettings.CODEC.fieldOf("bullet_settings").forGetter(ShootSettings::bulletSettings)
        ).apply(instance, ShootSettings::new)).codec();
    }

    public record BulletSettings(ITargetFilter targetFilter, float bulletDamage, float bulletSpeed, int maxExistTick, float gravity, float slowDown, ResourceLocation modelLocation, ResourceLocation textureLocation, ResourceLocation animationLocation) {

        public static final Codec<BulletSettings> CODEC = RecordCodecBuilder.<BulletSettings>mapCodec(instance -> instance.group(
                HTTargetFilters.getCodec().fieldOf("target_filter").forGetter(BulletSettings::targetFilter),
                Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("bullet_damage", 2F).forGetter(BulletSettings::bulletDamage),
                Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("bullet_speed", 0.15F).forGetter(BulletSettings::bulletSpeed),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max_exist_tick", 50).forGetter(BulletSettings::maxExistTick),
                Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("gravity", 0.03F).forGetter(BulletSettings::gravity),
                Codec.floatRange(0, 1F).optionalFieldOf("slow_down", 0.99F).forGetter(BulletSettings::slowDown),
                net.minecraft.resources.ResourceLocation.CODEC.fieldOf("model").forGetter(BulletSettings::modelLocation),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(BulletSettings::textureLocation),
                ResourceLocation.CODEC.fieldOf("animation").forGetter(BulletSettings::animationLocation)
        ).apply(instance, BulletSettings::new)).codec();
    }
}

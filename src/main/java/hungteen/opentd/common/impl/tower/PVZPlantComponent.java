package hungteen.opentd.common.impl.tower;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.OpenTD;
import hungteen.opentd.api.interfaces.*;
import hungteen.opentd.common.codec.*;
import hungteen.opentd.common.entity.OpenTDEntities;
import hungteen.opentd.common.entity.PlantEntity;
import hungteen.opentd.common.item.SummonTowerItem;
import hungteen.opentd.common.impl.effect.HTEffectComponents;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:40
 **/
public class PVZPlantComponent extends TowerComponent {

    public static final Codec<PVZPlantComponent> CODEC = RecordCodecBuilder.<PVZPlantComponent>mapCodec(instance -> instance.group(
            PlantSetting.CODEC.fieldOf("plant_setting").forGetter(PVZPlantComponent::plantSetting),
            TargetSetting.CODEC.listOf().optionalFieldOf("target_settings", Arrays.asList()).forGetter(PVZPlantComponent::targetSettings),
            Codec.optionalField("shoot_goal", ShootGoalSetting.CODEC).forGetter(PVZPlantComponent::shootGoalSetting),
            Codec.optionalField("gen_goal", GenGoalSetting.CODEC).forGetter(PVZPlantComponent::genGoalSetting),
            Codec.optionalField("attack_goal", AttackGoalSetting.CODEC).forGetter(PVZPlantComponent::attackGoalSetting),
            Codec.optionalField("laser_goal", LaserGoalSetting.CODEC).forGetter(PVZPlantComponent::laserGoalSetting),
            Codec.optionalField("instant_setting", CloseInstantEffectSetting.CODEC).forGetter(PVZPlantComponent::instantEffectSetting),
            ConstantAffectSetting.CODEC.listOf().optionalFieldOf("constant_settings", Arrays.asList()).forGetter(PVZPlantComponent::constantAffectSettings),
            Codec.optionalField("hurt_effect", HTEffectComponents.getCodec()).forGetter(PVZPlantComponent::hurtEffect),
            Codec.optionalField("die_effect", HTEffectComponents.getCodec()).forGetter(PVZPlantComponent::dieEffect)
    ).apply(instance, PVZPlantComponent::new)).codec();
    private final PlantSetting plantSetting;

    public PVZPlantComponent(PlantSetting plantSetting, List<TargetSetting> targetSettings, Optional<ShootGoalSetting> shootGoalSetting, Optional<GenGoalSetting> genGoalSetting, Optional<AttackGoalSetting> attackGoalSetting, Optional<LaserGoalSetting> laserGoalSetting, Optional<CloseInstantEffectSetting> instantEffectSetting, List<ConstantAffectSetting> constantAffectSettings, Optional<IEffectComponent> hurtEffect, Optional<IEffectComponent> dieEffect) {
        super(targetSettings, Optional.empty(), shootGoalSetting, genGoalSetting, attackGoalSetting, laserGoalSetting, instantEffectSetting, constantAffectSettings, hurtEffect, dieEffect, Optional.empty());
        this.plantSetting = plantSetting;
    }

    @Override
    public EntityType<? extends Entity> getEntityType() {
        return OpenTDEntities.PLANT_ENTITY.get();
    }

    public PlantSetting plantSetting() {
        return plantSetting;
    }

    @Override
    public ITowerComponentType<?> getType() {
        return HTTowerComponents.PVZ_PLANT;
    }

    @Override
    public CompoundTag getExtraNBT() {
        return plantSetting().extraNBT();
    }

    public record PlantSetting(CompoundTag extraNBT, GrowSettings growSetting, ResourceLocation id, int maxExistTick, boolean changeDirection, boolean pushable, boolean canFloat, RenderSetting renderSetting) {

        public static final Codec<PlantSetting> CODEC = RecordCodecBuilder.<PlantSetting>mapCodec(instance -> instance.group(
                CompoundTag.CODEC.optionalFieldOf("extra_nbt", new CompoundTag()).forGetter(PlantSetting::extraNBT),
                GrowSettings.CODEC.optionalFieldOf("grow_setting", GrowSettings.DEFAULT).forGetter(PlantSetting::growSetting),
                ResourceLocation.CODEC.fieldOf("id").forGetter(PlantSetting::id),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max_exist_tick", 0).forGetter(PlantSetting::maxExistTick),
                Codec.BOOL.optionalFieldOf("change_direction", true).forGetter(PlantSetting::changeDirection),
                Codec.BOOL.optionalFieldOf("pushable", false).forGetter(PlantSetting::pushable),
                Codec.BOOL.optionalFieldOf("can_float", false).forGetter(PlantSetting::canFloat),
                RenderSetting.CODEC.fieldOf("render_setting").forGetter(PlantSetting::renderSetting)
        ).apply(instance, PlantSetting::new)).codec();
    }

    public record GrowSettings(List<Float> scales, List<Integer> growDurations, Optional<SoundEvent> growSound, List<Pair<IEffectComponent, Integer>> growEffects) {
        public static final GrowSettings DEFAULT = new GrowSettings(Arrays.asList(1F), Arrays.asList(), Optional.empty(), Arrays.asList());

        public static final Codec<GrowSettings> CODEC = RecordCodecBuilder.<GrowSettings>mapCodec(instance -> instance.group(
                Codec.floatRange(0, Float.MAX_VALUE).listOf().fieldOf("scales").forGetter(GrowSettings::scales),
                Codec.intRange(0, Integer.MAX_VALUE).listOf().fieldOf("grow_durations").forGetter(GrowSettings::growDurations),
                Codec.optionalField("grow_sound", SoundEvent.CODEC).forGetter(GrowSettings::growSound),
                Codec.mapPair(
                        HTEffectComponents.getCodec().fieldOf("effect"),
                        Codec.intRange(0, Integer.MAX_VALUE).fieldOf("age")
                ).codec().listOf().fieldOf("grow_effects").forGetter(GrowSettings::growEffects)
        ).apply(instance, GrowSettings::new)).codec();

        public int getMaxAge() {
            return Math.min(this.scales.size(), this.growDurations.size() + 1);
        }
    }

}

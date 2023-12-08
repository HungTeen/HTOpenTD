package hungteen.opentd.common.impl.tower;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.ITowerComponentType;
import hungteen.opentd.common.codec.*;
import hungteen.opentd.common.entity.OpenTDEntities;
import hungteen.opentd.common.impl.effect.OTDEffectComponentTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/4 16:11
 */
public class PlantHeroComponent extends TowerComponent {

    public static final Codec<PlantHeroComponent> CODEC = RecordCodecBuilder.<PlantHeroComponent>mapCodec(instance -> instance.group(
            HeroSetting.CODEC.fieldOf("hero_setting").forGetter(PlantHeroComponent::heroSetting),
            Codec.optionalField("movement_setting", MovementSetting.CODEC).forGetter(PlantHeroComponent::movementSetting),
            TargetSetting.CODEC.listOf().optionalFieldOf("target_settings", Arrays.asList()).forGetter(PlantHeroComponent::targetSettings),
            Codec.optionalField("shoot_goal", ShootGoalSetting.CODEC).forGetter(PlantHeroComponent::shootGoalSetting),
            Codec.optionalField("gen_goal", GenGoalSetting.CODEC).forGetter(PlantHeroComponent::genGoalSetting),
            Codec.optionalField("attack_goal", AttackGoalSetting.CODEC).forGetter(PlantHeroComponent::attackGoalSetting),
            Codec.optionalField("laser_goal", LaserGoalSetting.CODEC).forGetter(PlantHeroComponent::laserGoalSetting),
            Codec.optionalField("instant_setting", CloseInstantEffectSetting.CODEC).forGetter(PlantHeroComponent::instantEffectSetting),
            ConstantAffectSetting.CODEC.listOf().optionalFieldOf("constant_settings", Arrays.asList()).forGetter(PlantHeroComponent::constantAffectSettings),
            Codec.optionalField("hurt_effect", OTDEffectComponentTypes.getCodec()).forGetter(PlantHeroComponent::hurtEffect),
            Codec.optionalField("die_effect", OTDEffectComponentTypes.getCodec()).forGetter(PlantHeroComponent::dieEffect),
            Codec.optionalField("boss_bar_setting", BossBarSetting.CODEC).forGetter(PlantHeroComponent::bossBarSetting),
            Codec.optionalField("follow_goal", FollowGoalSetting.CODEC).forGetter(PlantHeroComponent::followGoalSetting)
    ).apply(instance, PlantHeroComponent::new)).codec();
    private final HeroSetting heroSetting;

    public PlantHeroComponent(HeroSetting heroSetting, Optional<MovementSetting> movementSetting, List<TargetSetting> targetSettings, Optional<ShootGoalSetting> shootGoalSetting, Optional<GenGoalSetting> genGoalSetting, Optional<AttackGoalSetting> attackGoalSetting, Optional<LaserGoalSetting> laserGoalSetting, Optional<CloseInstantEffectSetting> instantEffectSetting, List<ConstantAffectSetting> constantAffectSettings, Optional<IEffectComponent> hurtEffect, Optional<IEffectComponent> dieEffect, Optional<BossBarSetting> bossBarSetting, Optional<FollowGoalSetting> followGoalSetting) {
        super(targetSettings, movementSetting, shootGoalSetting, genGoalSetting, attackGoalSetting, laserGoalSetting, instantEffectSetting, constantAffectSettings, hurtEffect, dieEffect, bossBarSetting, followGoalSetting);
        this.heroSetting = heroSetting;
    }

    @Override
    public EntityType<? extends Entity> getEntityType() {
        return OpenTDEntities.PLANT_HERO_ENTITY.get();
    }

    @Override
    public TowerSetting towerSetting() {
        return heroSetting().towerSetting();
    }

    @Override
    public ITowerComponentType<?> getType() {
        return OTDTowerTypes.PLANT_HERO;
    }

    public HeroSetting heroSetting() {
        return heroSetting;
    }

    @Override
    public CompoundTag getExtraNBT() {
        return heroSetting().extraNBT();
    }

    public record HeroSetting(TowerSetting towerSetting, CompoundTag extraNBT, ResourceLocation id, boolean sameTeamWithOwner, RenderSetting renderSetting) {

        public static final Codec<HeroSetting> CODEC = RecordCodecBuilder.<HeroSetting>mapCodec(instance -> instance.group(
                TowerSetting.CODEC.optionalFieldOf("tower_setting", TowerSetting.DEFAULT).forGetter(HeroSetting::towerSetting),
                CompoundTag.CODEC.optionalFieldOf("extra_nbt", new CompoundTag()).forGetter(HeroSetting::extraNBT),
                ResourceLocation.CODEC.fieldOf("id").forGetter(HeroSetting::id),
                Codec.BOOL.optionalFieldOf("same_team_with_owner", true).forGetter(HeroSetting::sameTeamWithOwner),
                RenderSetting.CODEC.fieldOf("render_setting").forGetter(HeroSetting::renderSetting)
        ).apply(instance, HeroSetting::new)).codec();
    }


}

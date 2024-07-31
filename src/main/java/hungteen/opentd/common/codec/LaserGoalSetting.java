package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.common.impl.effect.OTDEffectComponents;
import hungteen.opentd.common.impl.filter.OTDTargetFilters;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/15 18:59
 */
public record LaserGoalSetting(int coolDown, int duration, int effectInterval, Optional<Holder<ITargetFilter>> laserFilter, Optional<Holder<IEffectComponent>> continueEffect, Optional<Holder<IEffectComponent>> finalEffect, float laserWidth, double trackDistance, double laserDistance, boolean needRest, Optional<ResourceLocation> laserTexture, Optional<Integer> laserColor) {

    public static final Codec<LaserGoalSetting> CODEC = RecordCodecBuilder.<LaserGoalSetting>mapCodec(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("cool_down", 100).forGetter(LaserGoalSetting::coolDown),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("duration", 80).forGetter(LaserGoalSetting::duration),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("effect_interval", 1).forGetter(LaserGoalSetting::effectInterval),
            Codec.optionalField("laser_filter", OTDTargetFilters.getCodec()).forGetter(LaserGoalSetting::laserFilter),
            Codec.optionalField("continue_effect", OTDEffectComponents.getCodec()).forGetter(LaserGoalSetting::continueEffect),
            Codec.optionalField("final_effect", OTDEffectComponents.getCodec()).forGetter(LaserGoalSetting::finalEffect),
            Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("laser_width", 0.5F).forGetter(LaserGoalSetting::laserWidth),
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("track_distance", 10D).forGetter(LaserGoalSetting::trackDistance),
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("laser_distance", 15D).forGetter(LaserGoalSetting::laserDistance),
            Codec.BOOL.optionalFieldOf("need_rest", false).forGetter(LaserGoalSetting::needRest),
            Codec.optionalField("laser_texture", ResourceLocation.CODEC).forGetter(LaserGoalSetting::laserTexture),
            Codec.optionalField("laser_color", Codec.INT).forGetter(LaserGoalSetting::laserColor)
    ).apply(instance, LaserGoalSetting::new)).codec();

    public static final Codec<LaserGoalSetting> NETWORK_CODEC = RecordCodecBuilder.<LaserGoalSetting>mapCodec(instance -> instance.group(
            Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("laser_width", 0.5F).forGetter(LaserGoalSetting::laserWidth),
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("track_distance", 10D).forGetter(LaserGoalSetting::trackDistance),
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("laser_distance", 15D).forGetter(LaserGoalSetting::laserDistance),
            Codec.optionalField("laser_texture", ResourceLocation.CODEC).forGetter(LaserGoalSetting::laserTexture),
            Codec.optionalField("laser_color", Codec.INT).forGetter(LaserGoalSetting::laserColor)
    ).apply(instance, (laserWidth, trackDistance, laserDistance, laserTexture, laserColor) -> {
        return new LaserGoalSetting(0, 0, 0, Optional.empty(), Optional.empty(), Optional.empty(), laserWidth, trackDistance, laserDistance, false, laserTexture, laserColor);
    })).codec();

}

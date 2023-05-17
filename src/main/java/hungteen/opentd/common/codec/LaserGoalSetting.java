package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.common.impl.effect.HTEffectComponents;
import hungteen.opentd.common.impl.filter.HTTargetFilters;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/15 18:59
 */
public record LaserGoalSetting(int coolDown, int duration, int effectInterval, ITargetFilter laserFilter, IEffectComponent continueEffect, IEffectComponent finalEffect, float laserWidth, double trackDistance, double laserDistance, boolean needRest, Optional<ResourceLocation> laserTexture) {

    public static final Codec<LaserGoalSetting> CODEC = RecordCodecBuilder.<LaserGoalSetting>mapCodec(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("cool_down", 100).forGetter(LaserGoalSetting::coolDown),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("duration", 80).forGetter(LaserGoalSetting::duration),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("effect_interval", 1).forGetter(LaserGoalSetting::effectInterval),
            HTTargetFilters.getCodec().fieldOf("laser_filter").forGetter(LaserGoalSetting::laserFilter),
            HTEffectComponents.getCodec().fieldOf("continue_effect").forGetter(LaserGoalSetting::continueEffect),
            HTEffectComponents.getCodec().fieldOf("final_effect").forGetter(LaserGoalSetting::finalEffect),
            Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("laser_width", 0.5F).forGetter(LaserGoalSetting::laserWidth),
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("track_distance", 10D).forGetter(LaserGoalSetting::trackDistance),
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("laser_distance", 15D).forGetter(LaserGoalSetting::laserDistance),
            Codec.BOOL.optionalFieldOf("need_rest", false).forGetter(LaserGoalSetting::needRest),
            Codec.optionalField("laser_texture", ResourceLocation.CODEC).forGetter(LaserGoalSetting::laserTexture)
    ).apply(instance, LaserGoalSetting::new)).codec();

}

package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.common.impl.effect.HTEffectComponents;
import net.minecraft.sounds.SoundEvent;

import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/15 18:59
 */
public record LaserGoalSetting(int coolDown, int duration, int effectInterval, IEffectComponent continueEffect, IEffectComponent finalEffect, double laserDistance, boolean needRest, boolean canBlockByEntity, Optional<SoundEvent> attackSound) {

    public static final Codec<LaserGoalSetting> CODEC = RecordCodecBuilder.<LaserGoalSetting>mapCodec(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("cool_down", 30).forGetter(LaserGoalSetting::coolDown),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("duration", 0).forGetter(LaserGoalSetting::duration),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("effect_interval", 1).forGetter(LaserGoalSetting::effectInterval),
            HTEffectComponents.getCodec().fieldOf("continue_effect").forGetter(LaserGoalSetting::continueEffect),
            HTEffectComponents.getCodec().fieldOf("final_effect").forGetter(LaserGoalSetting::finalEffect),
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("laser_distance", 15D).forGetter(LaserGoalSetting::laserDistance),
            Codec.BOOL.optionalFieldOf("need_rest", false).forGetter(LaserGoalSetting::needRest),
            Codec.BOOL.optionalFieldOf("can_block_by_entity", false).forGetter(LaserGoalSetting::canBlockByEntity),
            Codec.optionalField("attack_sound", SoundEvent.CODEC).forGetter(LaserGoalSetting::attackSound)
    ).apply(instance, LaserGoalSetting::new)).codec();

}

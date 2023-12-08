package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.common.impl.effect.OTDEffectComponentTypes;
import net.minecraft.sounds.SoundEvent;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-05-10 22:24
 **/
public record AttackGoalSetting(int duration, int coolDown, int startTick, boolean needRest, double distance, Optional<SoundEvent> attackSound,
                                IEffectComponent effect) {
    public static final Codec<AttackGoalSetting> CODEC = RecordCodecBuilder.<AttackGoalSetting>mapCodec(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("duration", 0).forGetter(AttackGoalSetting::duration),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("cool_down", 30).forGetter(AttackGoalSetting::coolDown),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", 20).forGetter(AttackGoalSetting::startTick),
            Codec.BOOL.optionalFieldOf("need_rest", false).forGetter(AttackGoalSetting::needRest),
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("distance", 3D).forGetter(AttackGoalSetting::distance),
            Codec.optionalField("attack_sound", SoundEvent.CODEC).forGetter(AttackGoalSetting::attackSound),
            OTDEffectComponentTypes.getCodec().fieldOf("effect").forGetter(AttackGoalSetting::effect)
    ).apply(instance, AttackGoalSetting::new)).codec();
}
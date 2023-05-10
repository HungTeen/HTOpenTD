package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.common.impl.effect.HTEffectComponents;
import hungteen.opentd.common.impl.filter.HTTargetFilters;
import hungteen.opentd.common.impl.tower.PVZPlantComponent;
import net.minecraft.sounds.SoundEvent;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-05-10 22:25
 **/
public record CloseInstantEffectSetting(double closeRange, int instantTick, ITargetFilter targetFilter, Optional<SoundEvent> instantSound, IEffectComponent effect) {
    public static final Codec<CloseInstantEffectSetting> CODEC = RecordCodecBuilder.<CloseInstantEffectSetting>mapCodec(instance -> instance.group(
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("close_range", 3D).forGetter(CloseInstantEffectSetting::closeRange),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("instant_tick", 30).forGetter(CloseInstantEffectSetting::instantTick),
            HTTargetFilters.getCodec().fieldOf("target_filter").forGetter(CloseInstantEffectSetting::targetFilter),
            Codec.optionalField("instant_sound", SoundEvent.CODEC).forGetter(CloseInstantEffectSetting::instantSound),
            HTEffectComponents.getCodec().fieldOf("effect").forGetter(CloseInstantEffectSetting::effect)
    ).apply(instance, CloseInstantEffectSetting::new)).codec();
}

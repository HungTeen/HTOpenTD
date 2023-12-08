package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.ITargetFinder;
import hungteen.opentd.common.impl.effect.OTDEffectComponentTypes;
import hungteen.opentd.common.impl.finder.OTDTargetFinders;
import net.minecraft.core.Holder;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-05-10 22:25
 **/
public record ConstantAffectSetting(int cd, Holder<ITargetFinder> targetFinder, IEffectComponent effect) {

    public static final Codec<ConstantAffectSetting> CODEC = RecordCodecBuilder.<ConstantAffectSetting>mapCodec(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("cd").forGetter(ConstantAffectSetting::cd),
            OTDTargetFinders.getCodec().fieldOf("target_finder").forGetter(ConstantAffectSetting::targetFinder),
            OTDEffectComponentTypes.getCodec().fieldOf("effect").forGetter(ConstantAffectSetting::effect)
    ).apply(instance, ConstantAffectSetting::new)).codec();

}
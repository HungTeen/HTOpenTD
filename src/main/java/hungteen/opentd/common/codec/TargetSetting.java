package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ITargetFinder;
import hungteen.opentd.common.impl.finder.HTTargetFinders;
import hungteen.opentd.common.impl.tower.PVZPlantComponent;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-05-10 22:18
 **/
public record TargetSetting(int priority, float chance, boolean closest, int refreshCD, ITargetFinder targetFinder) {

    public static final Codec<TargetSetting> CODEC = RecordCodecBuilder.<TargetSetting>mapCodec(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("priority").forGetter(TargetSetting::priority),
            Codec.floatRange(0, 1).fieldOf("chance").forGetter(TargetSetting::chance),
            Codec.BOOL.optionalFieldOf("closest", true).forGetter(TargetSetting::closest),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("refresh_cd", 1000).forGetter(TargetSetting::refreshCD),
            HTTargetFinders.getCodec().fieldOf("target_finder").forGetter(TargetSetting::targetFinder)
    ).apply(instance, TargetSetting::new)).codec();

}
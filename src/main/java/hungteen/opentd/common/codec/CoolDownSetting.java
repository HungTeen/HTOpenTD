package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;

/**
 * TODO 1.20 Content.
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/15 19:01
 */
public record CoolDownSetting(IntProvider cdProvider, boolean affectByEffects) {

    public static final Codec<CoolDownSetting> CODEC = RecordCodecBuilder.<CoolDownSetting>mapCodec(instance -> instance.group(
            IntProvider.POSITIVE_CODEC.fieldOf("cd").forGetter(CoolDownSetting::cdProvider),
            Codec.BOOL.optionalFieldOf("affect_by_effects", false).forGetter(CoolDownSetting::affectByEffects)
    ).apply(instance, CoolDownSetting::new)).codec();

    public int get(RandomSource rand){
        return cdProvider().sample(rand);
    }

}

package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.common.impl.tower.PVZPlantComponent;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/4 16:19
 */
public record MovementSetting(boolean canRandomMove, double speedModifier, double backwardPercent, double upwardPercent){
    public static final Codec<MovementSetting> CODEC = RecordCodecBuilder.<MovementSetting>mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("can_random_move", true).forGetter(MovementSetting::canRandomMove),
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("speed_modifier", 1D).forGetter(MovementSetting::speedModifier),
            Codec.doubleRange(0, 1).optionalFieldOf("backward_percent", 0.3D).forGetter(MovementSetting::backwardPercent),
            Codec.doubleRange(0, 1).optionalFieldOf("upward_percent", 0.7D).forGetter(MovementSetting::upwardPercent)
    ).apply(instance, MovementSetting::new)).codec();
}

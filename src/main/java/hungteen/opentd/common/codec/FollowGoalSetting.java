package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/6/8 17:22
 */
public record FollowGoalSetting(double teleportDistance, float startDistance, float stopDistance, float speedModifier, boolean canFly) {

    public static final Codec<FollowGoalSetting> CODEC = RecordCodecBuilder.<FollowGoalSetting>mapCodec(instance -> instance.group(
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("teleport_distance", Double.MAX_VALUE).forGetter(FollowGoalSetting::teleportDistance),
            Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("start_distance", 10F).forGetter(FollowGoalSetting::startDistance),
            Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("stop_distance", 2F).forGetter(FollowGoalSetting::stopDistance),
            Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("speed_modifier", 1F).forGetter(FollowGoalSetting::speedModifier),
            Codec.BOOL.optionalFieldOf("can_fly", false).forGetter(FollowGoalSetting::canFly)
    ).apply(instance, FollowGoalSetting::new)).codec();

}

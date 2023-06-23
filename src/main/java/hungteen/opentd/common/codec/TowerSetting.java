package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-06-23 10:55
 **/
public record TowerSetting(boolean canRideInWater, boolean customDeath, int deathDuration) {

    public static final TowerSetting DEFAULT = new TowerSetting(false, false, 0);

    public static final Codec<TowerSetting> CODEC = RecordCodecBuilder.<TowerSetting>mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("can_ride_in_water", false).forGetter(TowerSetting::canRideInWater),
            Codec.BOOL.optionalFieldOf("custom_death", true).forGetter(TowerSetting::customDeath),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("death_duration", 0).forGetter(TowerSetting::deathDuration)
    ).apply(instance, TowerSetting::new)).codec();
}

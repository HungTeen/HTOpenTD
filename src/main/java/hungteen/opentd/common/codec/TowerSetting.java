package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-06-23 10:55
 **/
public record TowerSetting(boolean canRideInWater, boolean canBreathInWater, boolean customDeath, boolean persistent, int deathDuration) {

    public static final TowerSetting DEFAULT = new TowerSetting(false, false, false, false, 0);

    public static final TowerSetting DEFAULT_WATER = new TowerSetting(true, true, false, false, 0);

    public static final Codec<TowerSetting> CODEC = RecordCodecBuilder.<TowerSetting>mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("can_ride_in_water", false).forGetter(TowerSetting::canRideInWater),
            Codec.BOOL.optionalFieldOf("can_breath_in_water", false).forGetter(TowerSetting::canBreathInWater),
            Codec.BOOL.optionalFieldOf("custom_death", true).forGetter(TowerSetting::customDeath),
            Codec.BOOL.optionalFieldOf("persistent", false).forGetter(TowerSetting::persistent),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("death_duration", 0).forGetter(TowerSetting::deathDuration)
    ).apply(instance, TowerSetting::new)).codec();
}

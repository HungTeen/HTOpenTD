package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;

import java.util.Optional;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/6/7 14:32
 */
public record BossBarSetting(Optional<String> title, String color, boolean darkenScreen, boolean playBossMusic, boolean createWorldFog) {

    public static final Codec<BossBarSetting> CODEC = RecordCodecBuilder.<BossBarSetting>mapCodec(instance -> instance.group(
            Codec.optionalField("title", Codec.STRING).forGetter(BossBarSetting::title),
            Codec.STRING.fieldOf("color").forGetter(BossBarSetting::color),
            Codec.BOOL.optionalFieldOf("darken_screen", false).forGetter(BossBarSetting::darkenScreen),
            Codec.BOOL.optionalFieldOf("play_boss_music", false).forGetter(BossBarSetting::playBossMusic),
            Codec.BOOL.optionalFieldOf("create_world_fog", false).forGetter(BossBarSetting::createWorldFog)
    ).apply(instance, BossBarSetting::new)).codec();

}

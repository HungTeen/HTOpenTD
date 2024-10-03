package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.common.impl.OTDBulletSettings;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-05-10 22:19
 **/
public record ShootGoalSetting(int duration, int coolDown, int startTick, int shootCount, boolean needRest, boolean mustSeeTarget, Optional<Holder<SoundEvent>> shootSound,
                               List<ShootSetting> shootSettings) {
    public static final Codec<ShootGoalSetting> CODEC = RecordCodecBuilder.<ShootGoalSetting>mapCodec(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("duration", 0).forGetter(ShootGoalSetting::duration),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("cool_down", 30).forGetter(ShootGoalSetting::coolDown),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("start_tick", 20).forGetter(ShootGoalSetting::startTick),
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("shoot_count", 1).forGetter(ShootGoalSetting::shootCount),
            Codec.BOOL.optionalFieldOf("need_rest", false).forGetter(ShootGoalSetting::needRest),
            Codec.BOOL.optionalFieldOf("must_see_target", true).forGetter(ShootGoalSetting::mustSeeTarget),
            Codec.optionalField("shoot_sound", SoundEvent.CODEC).forGetter(ShootGoalSetting::shootSound),
            ShootSetting.CODEC.listOf().fieldOf("shoot_settings").forGetter(ShootGoalSetting::shootSettings)
    ).apply(instance, ShootGoalSetting::new)).codec();

    public record ShootSetting(boolean plantFoodOnly, boolean isParabola, int shootTick, Vec3 offset,
                               double verticalAngleLimit, double horizontalAngleOffset,
                               double pultHeight, Holder<BulletSetting> bulletSetting) {
        public static final Codec<ShootSetting> CODEC = RecordCodecBuilder.<ShootSetting>mapCodec(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("plant_food_only", false).forGetter(ShootSetting::plantFoodOnly),
                Codec.BOOL.optionalFieldOf("is_parabola", false).forGetter(ShootSetting::isParabola),
                Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("shoot_tick", 0).forGetter(ShootSetting::shootTick),
                Vec3.CODEC.optionalFieldOf("origin_offset", Vec3.ZERO).forGetter(ShootSetting::offset),
                Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("vertical_angle_limit", 0D).forGetter(ShootSetting::verticalAngleLimit),
                Codec.DOUBLE.optionalFieldOf("horizontal_angle_offset", 0D).forGetter(ShootSetting::horizontalAngleOffset),
                Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("pult_height", 10D).forGetter(ShootSetting::pultHeight),
                OTDBulletSettings.getCodec().fieldOf("bullet_setting").forGetter(ShootSetting::bulletSetting)
        ).apply(instance, ShootSetting::new)).codec();
    }

}
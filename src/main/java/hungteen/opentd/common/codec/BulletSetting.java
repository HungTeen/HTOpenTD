package hungteen.opentd.common.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.common.impl.effect.HTEffectComponents;
import hungteen.opentd.common.impl.filter.HTTargetFilters;
import hungteen.opentd.common.impl.tower.PVZPlantComponent;
import hungteen.opentd.common.impl.tower.PlantHeroComponent;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-05-10 22:21
 **/
public record BulletSetting(ITargetFilter targetFilter, IEffectComponent effect, float bulletSpeed,
                             int maxHitCount, int maxExistTick, float gravity, float slowDown, boolean ignoreBlock,
                             boolean lockToTarget, boolean sameTeamWithOwner, RenderSetting renderSettings, Optional<ParticleSetting> hitParticle, Optional<ParticleSetting> trailParticle) {

    public static final Codec<BulletSetting> CODEC = RecordCodecBuilder.<BulletSetting>mapCodec(instance -> instance.group(
            HTTargetFilters.getCodec().fieldOf("target_filter").forGetter(BulletSetting::targetFilter),
            HTEffectComponents.getCodec().fieldOf("effect").forGetter(BulletSetting::effect),
            Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("bullet_speed", 0.15F).forGetter(BulletSetting::bulletSpeed),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max_hit_count", 1).forGetter(BulletSetting::maxHitCount),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max_exist_tick", 50).forGetter(BulletSetting::maxExistTick),
            Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("gravity", 0.03F).forGetter(BulletSetting::gravity),
            Codec.floatRange(0, 1F).optionalFieldOf("slow_down", 0.99F).forGetter(BulletSetting::slowDown),
            Codec.BOOL.optionalFieldOf("ignore_block", false).forGetter(BulletSetting::ignoreBlock),
            Codec.BOOL.optionalFieldOf("lock_to_target", false).forGetter(BulletSetting::lockToTarget),
            Codec.BOOL.optionalFieldOf("same_team_with_owner", true).forGetter(BulletSetting::sameTeamWithOwner),
            RenderSetting.CODEC.fieldOf("render_setting").forGetter(BulletSetting::renderSettings),
            Codec.optionalField("hit_particle", ParticleSetting.CODEC).forGetter(BulletSetting::hitParticle),
            Codec.optionalField("trail_particle", ParticleSetting.CODEC).forGetter(BulletSetting::trailParticle)
    ).apply(instance, BulletSetting::new)).codec();
}

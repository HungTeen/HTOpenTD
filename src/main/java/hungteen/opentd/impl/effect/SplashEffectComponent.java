package hungteen.opentd.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.impl.filter.HTTargetFilters;
import hungteen.opentd.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.util.Arrays;
import java.util.List;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-28 16:47
 **/
public record SplashEffectComponent(double radius, double height, boolean isCircle, ITargetFilter filter, List<IEffectComponent> effects) implements IEffectComponent {

    public static final Codec<SplashEffectComponent> CODEC = RecordCodecBuilder.<SplashEffectComponent>mapCodec(instance -> instance.group(
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("radius", 1D).forGetter(SplashEffectComponent::radius),
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("height", 1D).forGetter(SplashEffectComponent::height),
            Codec.BOOL.optionalFieldOf("is_circle", true).forGetter(SplashEffectComponent::isCircle),
            HTTargetFilters.getCodec().fieldOf("filter").forGetter(SplashEffectComponent::filter),
            HTEffectComponents.getCodec().listOf().optionalFieldOf("effects", Arrays.asList()).forGetter(SplashEffectComponent::effects)
    ).apply(instance, SplashEffectComponent::new)).codec();

    @Override
    public void effectTo(Entity owner, Entity entity) {
        effect(owner);
    }

    @Override
    public void effectTo(Entity owner, BlockPos pos) {
        effect(owner);
    }

    private void effect(Entity attacker){
        EntityUtil.getPredicateEntities(attacker, EntityUtil.getEntityAABB(attacker, radius(), height()), Entity.class, l -> {
            return filter().match(attacker, l);
        }).forEach(target -> {
            effects().forEach(effect -> effect.effectTo(attacker, target));
        });
    }

    @Override
    public IEffectComponentType<?> getType() {
        return HTEffectComponents.SPLASH_EFFECT;
    }
}

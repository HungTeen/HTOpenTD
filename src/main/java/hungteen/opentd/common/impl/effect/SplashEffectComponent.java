package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.helper.registry.EntityHelper;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.common.impl.filter.OTDTargetFilters;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-28 16:47
 **/
public record SplashEffectComponent(double radius, double height, boolean isCircle, Holder<ITargetFilter> filter,
                                    Holder<IEffectComponent> effect) implements IEffectComponent {

    public static final Codec<SplashEffectComponent> CODEC = RecordCodecBuilder.<SplashEffectComponent>mapCodec(instance -> instance.group(
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("radius", 1D).forGetter(SplashEffectComponent::radius),
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("height", 1D).forGetter(SplashEffectComponent::height),
            Codec.BOOL.optionalFieldOf("is_circle", true).forGetter(SplashEffectComponent::isCircle),
            OTDTargetFilters.getCodec().fieldOf("filter").forGetter(SplashEffectComponent::filter),
            OTDEffectComponents.getCodec().fieldOf("effect").forGetter(SplashEffectComponent::effect)
    ).apply(instance, SplashEffectComponent::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        effect(serverLevel, owner);
    }

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos) {
        effect(serverLevel, owner);
    }

    private void effect(ServerLevel serverLevel, Entity attacker) {
        EntityHelper.getPredicateEntities(attacker, EntityHelper.getEntityAABB(attacker, radius(), height()), Entity.class, l -> {
            return filter().get().match((ServerLevel) attacker.level(), attacker, l);
        }).forEach(target -> {
            effect().get().effectTo(serverLevel, attacker, target);
        });
    }

    @Override
    public IEffectComponentType<?> getType() {
        return OTDEffectComponentTypes.SPLASH_EFFECT;
    }
}

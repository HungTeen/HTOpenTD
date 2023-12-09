package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.common.impl.filter.AlwaysTrueFilter;
import hungteen.opentd.common.impl.filter.OTDTargetFilters;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-11 20:00
 **/
public record AttractEffectComponent(Holder<ITargetFilter> targetFilter, Optional<Holder<ITargetFilter>> attractFilter) implements IEffectComponent {

    public static final Codec<AttractEffectComponent> CODEC = RecordCodecBuilder.<AttractEffectComponent>mapCodec(instance -> instance.group(
            OTDTargetFilters.getCodec().optionalFieldOf("target_filter", Holder.direct(AlwaysTrueFilter.INSTANCE)).forGetter(AttractEffectComponent::targetFilter),
            Codec.optionalField("attract_filter", OTDTargetFilters.getCodec()).forGetter(AttractEffectComponent::attractFilter)
    ).apply(instance, AttractEffectComponent::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        if(entity instanceof Mob mob && owner instanceof LivingEntity){
            // Target not match.
            if(! targetFilter().get().match(serverLevel, owner, entity)){
                return;
            }
            // Match target's target.
            if(mob.getTarget() == null || attractFilter().isEmpty() || attractFilter().get().get().match(serverLevel, owner, mob.getTarget())){
                mob.setTarget((LivingEntity) owner);
            }
        }
    }

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos) {

    }

    @Override
    public IEffectComponentType<?> getType() {
        return OTDEffectComponentTypes.ATTRACT_EFFECT;
    }
}

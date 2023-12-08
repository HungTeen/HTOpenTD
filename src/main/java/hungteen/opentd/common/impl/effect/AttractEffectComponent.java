package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.common.impl.filter.AlwaysTrueFilter;
import hungteen.opentd.common.impl.filter.OTDTargetFilterTypes;
import net.minecraft.core.BlockPos;
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
public record AttractEffectComponent(ITargetFilter targetFilter, Optional<ITargetFilter> attractFilter) implements IEffectComponent {

    public static final Codec<AttractEffectComponent> CODEC = RecordCodecBuilder.<AttractEffectComponent>mapCodec(instance -> instance.group(
            OTDTargetFilterTypes.getCodec().optionalFieldOf("target_filter", AlwaysTrueFilter.INSTANCE).forGetter(AttractEffectComponent::targetFilter),
            Codec.optionalField("attract_filter", OTDTargetFilterTypes.getCodec()).forGetter(AttractEffectComponent::attractFilter)
    ).apply(instance, AttractEffectComponent::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        if(entity instanceof Mob && owner instanceof LivingEntity && owner.level instanceof ServerLevel){
            // Target not match.
            if(! targetFilter().match((ServerLevel) owner.level, owner, entity)){
                return;
            }
            // Match target's target.
            if(((Mob)entity).getTarget() == null || ! attractFilter().isPresent() || attractFilter().get().match((ServerLevel) owner.level, owner, ((Mob)entity).getTarget())){
                ((Mob)entity).setTarget((LivingEntity) owner);
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

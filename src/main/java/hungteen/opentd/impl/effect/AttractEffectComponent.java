package hungteen.opentd.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.common.entity.BulletEntity;
import hungteen.opentd.impl.filter.HTTargetFilters;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-11 20:00
 **/
public record AttractEffectComponent(Optional<ITargetFilter> attractFilter) implements IEffectComponent {

    public static final Codec<AttractEffectComponent> CODEC = RecordCodecBuilder.<AttractEffectComponent>mapCodec(instance -> instance.group(
            Codec.optionalField("attract_filter", HTTargetFilters.getCodec()).forGetter(AttractEffectComponent::attractFilter)
    ).apply(instance, AttractEffectComponent::new)).codec();

    @Override
    public void effectTo(Entity owner, Entity entity) {
        if(entity instanceof Mob && owner instanceof LivingEntity){
            if(((Mob)entity).getTarget() == null || ! attractFilter().isPresent() || attractFilter().get().match(owner, ((Mob)entity).getTarget())){
                ((Mob)entity).setTarget((LivingEntity) owner);
            }
        }
    }

    @Override
    public void effectTo(Entity owner, BlockPos pos) {

    }

    @Override
    public IEffectComponentType<?> getType() {
        return HTEffectComponents.ATTRACT_EFFECT;
    }
}

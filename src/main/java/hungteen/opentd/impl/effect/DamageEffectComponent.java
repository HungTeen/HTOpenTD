package hungteen.opentd.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.common.entity.BulletEntity;
import hungteen.opentd.impl.requirement.ExperienceRequirement;
import hungteen.opentd.impl.tower.PVZPlantComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-28 16:38
 **/
public record DamageEffectComponent(boolean ignoreImmuneTick, float amount) implements IEffectComponent {

    public static final Codec<DamageEffectComponent> CODEC = RecordCodecBuilder.<DamageEffectComponent>mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("ignore_immune_tick", false).forGetter(DamageEffectComponent::ignoreImmuneTick),
            Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("amount", 0F).forGetter(DamageEffectComponent::amount)
            ).apply(instance, DamageEffectComponent::new)).codec();

    @Override
    public void effectTo(Entity owner, Entity entity) {
        if(ignoreImmuneTick()){
            entity.invulnerableTime = 0;
        }
        if(owner instanceof BulletEntity){
            final DamageSource source = DamageSource.thrown(owner, ((BulletEntity) owner).getOwner());
            entity.hurt(source, this.amount());
        } else if(owner instanceof LivingEntity){
            entity.hurt(DamageSource.mobAttack((LivingEntity) owner), this.amount());
        }
    }

    @Override
    public void effectTo(Entity owner, BlockPos pos) {

    }

    @Override
    public IEffectComponentType<?> getType() {
        return HTEffectComponents.DAMAGE_EFFECT;
    }
}

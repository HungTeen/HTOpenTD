package hungteen.opentd.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.common.entity.BulletEntity;
import hungteen.opentd.impl.requirement.ExperienceRequirement;
import hungteen.opentd.impl.tower.PVZPlantComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-28 16:38
 **/
public record DamageEffectComponent(boolean ignoreImmuneTick, float amount, float kbStrength) implements IEffectComponent {

    public static final Codec<DamageEffectComponent> CODEC = RecordCodecBuilder.<DamageEffectComponent>mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("ignore_immune_tick", false).forGetter(DamageEffectComponent::ignoreImmuneTick),
            Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("amount", 0F).forGetter(DamageEffectComponent::amount),
            Codec.FLOAT.optionalFieldOf("kb_strength", 0F).forGetter(DamageEffectComponent::kbStrength)
    ).apply(instance, DamageEffectComponent::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        if(ignoreImmuneTick()){
            entity.invulnerableTime = 0;
        }
        // Store origin speed.
        final Vec3 originSpeed = entity.getDeltaMovement();

        if(owner instanceof BulletEntity){
            final DamageSource source = DamageSource.thrown(owner, ((BulletEntity) owner).getOwner());
            entity.hurt(source, this.amount());
        } else if(owner instanceof LivingEntity){
            entity.hurt(DamageSource.mobAttack((LivingEntity) owner), this.amount());
        }

        if(entity instanceof LivingEntity){
            // Return origin speed.
            entity.setDeltaMovement(originSpeed);
            if(this.kbStrength() != 0){
                ((LivingEntity) entity).knockback(this.kbStrength(), owner.getX() - entity.getX(), owner.getZ() - entity.getZ());
            }
        }
    }

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos) {

    }

    @Override
    public IEffectComponentType<?> getType() {
        return HTEffectComponents.DAMAGE_EFFECT;
    }
}

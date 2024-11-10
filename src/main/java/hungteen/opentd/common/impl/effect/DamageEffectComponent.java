package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.common.entity.BulletEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-28 16:38
 **/
public record DamageEffectComponent(Optional<Holder<DamageType>> specificSource, boolean ignoreImmuneTick, float amount, float kbStrength) implements IEffectComponent {

    public static final Codec<Holder<DamageType>> DAMAGE_TYPE_CODEC = RegistryFileCodec.create(Registries.DAMAGE_TYPE, DamageType.CODEC);

    public static final Codec<DamageEffectComponent> CODEC = RecordCodecBuilder.<DamageEffectComponent>mapCodec(instance -> instance.group(
            DAMAGE_TYPE_CODEC.optionalFieldOf("specific_source").forGetter(DamageEffectComponent::specificSource),
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

        specificSource().ifPresentOrElse(holder -> {
            if(owner instanceof BulletEntity bulletEntity){
                final DamageSource source = new DamageSource(holder, owner, bulletEntity.getOwner());
                entity.hurt(source, this.amount());
            } else {
                final DamageSource source = new DamageSource(holder, owner);
                entity.hurt(source, this.amount());
            }
        }, () -> {
            if(owner instanceof BulletEntity bulletEntity){
                final DamageSource source = bulletEntity.damageSources().thrown(owner, bulletEntity.getOwner());
                entity.hurt(source, this.amount());
            } else if(owner instanceof LivingEntity livingEntity){
                entity.hurt(livingEntity.damageSources().mobAttack((LivingEntity) owner), this.amount());
            }
        });


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
        return OTDEffectComponentTypes.DAMAGE_EFFECT;
    }
}

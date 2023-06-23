package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-02 11:47
 **/
public record KnockbackEffectComponent (boolean self, boolean horizontalOnly, float kbStrength, float affectPercent, Vec3 extraSpeed) implements IEffectComponent {

    public static final Codec<KnockbackEffectComponent> CODEC = RecordCodecBuilder.<KnockbackEffectComponent>mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("self", false).forGetter(KnockbackEffectComponent::self),
            Codec.BOOL.optionalFieldOf("horizontal_only", false).forGetter(KnockbackEffectComponent::horizontalOnly),
            Codec.FLOAT.optionalFieldOf("kb_strength", 0.4F).forGetter(KnockbackEffectComponent::kbStrength),
            Codec.floatRange(0, 1).optionalFieldOf("affect_percent", 0.5F).forGetter(KnockbackEffectComponent::affectPercent),
            Vec3.CODEC.optionalFieldOf("extra_speed", Vec3.ZERO).forGetter(KnockbackEffectComponent::extraSpeed)
    ).apply(instance, KnockbackEffectComponent::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        final Entity attacker = self() ? entity : owner;
        final Entity target = self() ? owner : entity;
        final Vec3 originSpeed = target.getDeltaMovement();
        double strength = kbStrength();
        if(target instanceof LivingEntity){
            strength *= (1.0D - ((LivingEntity) target).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        }
        if(Math.abs(strength) >= 0.00001){
            if(horizontalOnly()){
                final Vec3 dif = target.position().subtract(attacker.position());
                final Vec3 speed = (new Vec3(dif.x(), 0.0D, dif.z())).normalize().scale(strength);
                target.setDeltaMovement(originSpeed.x * affectPercent() - speed.x, target.isOnGround() ? Math.min(0.4D, originSpeed.y * affectPercent() + strength) : originSpeed.y, originSpeed.z  * affectPercent() - speed.z);
            } else{
                final Vec3 speed = target.getEyePosition().subtract(attacker.getEyePosition()).normalize().scale(strength);
                target.setDeltaMovement(originSpeed.x * affectPercent() - speed.x, Math.min(0.4D, originSpeed.y * affectPercent() - speed.y), originSpeed.z  * affectPercent() - speed.z);
            }
        }
        target.setDeltaMovement(target.getDeltaMovement().add(extraSpeed()));
    }

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos) {

    }

    @Override
    public IEffectComponentType<?> getType() {
        return HTEffectComponents.KB_EFFECT;
    }
}

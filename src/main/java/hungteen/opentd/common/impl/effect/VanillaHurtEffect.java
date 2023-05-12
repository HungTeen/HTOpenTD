package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/12 9:32
 */
public record VanillaHurtEffect(boolean ignoreImmuneTick) implements IEffectComponent {

    public static final Codec<VanillaHurtEffect> CODEC = RecordCodecBuilder.<VanillaHurtEffect>mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("ignore_immune_tick", false).forGetter(VanillaHurtEffect::ignoreImmuneTick),
    ).apply(instance, VanillaHurtEffect::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        if(ignoreImmuneTick()){
            entity.invulnerableTime = 0;
        }
        if(owner instanceof LivingEntity livingEntity){
            livingEntity.doHurtTarget(entity);
        }
    }

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos) {

    }

    @Override
    public IEffectComponentType<?> getType() {
        return HTEffectComponents.VANILLA_HURT_EFFECT;
    }
}

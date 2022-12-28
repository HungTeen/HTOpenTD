package hungteen.opentd.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.common.entity.BulletEntity;
import hungteen.opentd.impl.filter.HTTargetFilters;
import hungteen.opentd.util.EntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-28 16:47
 **/
public record SplashEffectComponent(double radius, double height, boolean isCircle, float amount, ITargetFilter filter) implements IEffectComponent {

    public static final Codec<SplashEffectComponent> CODEC = RecordCodecBuilder.<SplashEffectComponent>mapCodec(instance -> instance.group(
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("radius", 1D).forGetter(SplashEffectComponent::radius),
            Codec.doubleRange(0, Double.MAX_VALUE).optionalFieldOf("height", 1D).forGetter(SplashEffectComponent::height),
            Codec.BOOL.optionalFieldOf("is_circle", true).forGetter(SplashEffectComponent::isCircle),
            Codec.floatRange(0, Float.MAX_VALUE).optionalFieldOf("amount", 0F).forGetter(SplashEffectComponent::amount),
            HTTargetFilters.getCodec().fieldOf("filter").forGetter(SplashEffectComponent::filter)
    ).apply(instance, SplashEffectComponent::new)).codec();

    @Override
    public void effectTo(Entity owner, Entity entity) {
        dealDamage(owner);
    }

    @Override
    public void effectTo(Entity owner, BlockPos pos) {
        dealDamage(owner);
    }

    private void dealDamage(Entity attacker){
        EntityUtil.getPredicateEntities(attacker, EntityUtil.getEntityAABB(attacker, radius(), height()), Entity.class, l -> {
            return filter().match(attacker, l);
        }).forEach(target -> {
            if(attacker instanceof BulletEntity){
                final DamageSource source = DamageSource.thrown(attacker, ((BulletEntity) attacker).getOwner());
                target.hurt(source, this.amount());
            } else if(attacker instanceof LivingEntity){
                target.hurt(DamageSource.mobAttack((LivingEntity) attacker), this.amount());
            }
        });
    }

    @Override
    public IEffectComponentType<?> getType() {
        return HTEffectComponents.SPLASH_EFFECT;
    }
}

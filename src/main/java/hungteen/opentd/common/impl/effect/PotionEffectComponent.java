package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.helper.registry.EffectHelper;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-28 20:50
 **/
public record PotionEffectComponent(MobEffect effect, int duration, int level, boolean display, boolean self) implements IEffectComponent {

    public static final Codec<PotionEffectComponent> CODEC = RecordCodecBuilder.<PotionEffectComponent>mapCodec(instance -> instance.group(
            ForgeRegistries.MOB_EFFECTS.getCodec().fieldOf("mob_effect").forGetter(PotionEffectComponent::effect),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("duration").forGetter(PotionEffectComponent::duration),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("level", 0).forGetter(PotionEffectComponent::level),
            Codec.BOOL.optionalFieldOf("display", true).forGetter(PotionEffectComponent::display),
            Codec.BOOL.optionalFieldOf("self", false).forGetter(PotionEffectComponent::self)
    ).apply(instance, PotionEffectComponent::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        if(self()){
            if(owner instanceof LivingEntity){
                ((LivingEntity)owner).addEffect(EffectHelper.effect(this.effect(), duration(), level(), false, display()), entity);
            }
        } else{
            if(entity instanceof LivingEntity){
                ((LivingEntity)entity).addEffect(EffectHelper.effect(this.effect(), duration(), level(), false, display()), owner);
            }
        }
    }

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos) {
        if(self()){
            if(owner instanceof LivingEntity){
                ((LivingEntity)owner).addEffect(EffectHelper.effect(this.effect(), duration(), level(), false, display()));
            }
        }
    }

    @Override
    public IEffectComponentType<?> getType() {
        return OTDEffectComponentTypes.POTION_EFFECT;
    }
}

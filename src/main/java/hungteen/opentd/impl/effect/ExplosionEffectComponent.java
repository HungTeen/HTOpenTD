package hungteen.opentd.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.helper.WorldHelper;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-07 19:50
 **/
public record ExplosionEffectComponent(boolean canBreak, boolean destroyMode, float power) implements IEffectComponent {

    public static final Codec<ExplosionEffectComponent> CODEC = RecordCodecBuilder.<ExplosionEffectComponent>mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("can_break", true).forGetter(ExplosionEffectComponent::canBreak),
            Codec.BOOL.optionalFieldOf("destroy_mode", true).forGetter(ExplosionEffectComponent::destroyMode),
            Codec.floatRange(0, Float.MAX_VALUE).fieldOf("power").forGetter(ExplosionEffectComponent::power)
    ).apply(instance, ExplosionEffectComponent::new)).codec();

    @Override
    public void effectTo(Entity owner, Entity entity) {
        explode(owner.level, entity);
    }

    @Override
    public void effectTo(Entity owner, BlockPos pos) {

    }

    private void explode(Level level, Entity entity){
        Explosion.BlockInteraction interaction = Explosion.BlockInteraction.NONE;
        if(canBreak() && ForgeEventFactory.getMobGriefingEvent(level, entity)){
            interaction = destroyMode() ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.BREAK;
        }
        level.explode(entity, entity.getX(), entity.getY(), entity.getZ(), power(), interaction);
    }

    @Override
    public IEffectComponentType<?> getType() {
        return HTEffectComponents.EXPLOSION_EFFECT;
    }
}

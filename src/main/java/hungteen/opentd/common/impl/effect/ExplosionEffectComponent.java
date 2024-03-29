package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ForgeEventFactory;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-07 19:50
 **/
public record ExplosionEffectComponent(boolean canBreak, boolean destroyMode, float power, boolean self) implements IEffectComponent {

    public static final Codec<ExplosionEffectComponent> CODEC = RecordCodecBuilder.<ExplosionEffectComponent>mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("can_break", true).forGetter(ExplosionEffectComponent::canBreak),
            Codec.BOOL.optionalFieldOf("destroy_mode", true).forGetter(ExplosionEffectComponent::destroyMode),
            Codec.floatRange(0, Float.MAX_VALUE).fieldOf("power").forGetter(ExplosionEffectComponent::power),
            Codec.BOOL.optionalFieldOf("self", true).forGetter(ExplosionEffectComponent::self)
            ).apply(instance, ExplosionEffectComponent::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        explode(serverLevel, owner, self() ? owner.blockPosition() : entity.blockPosition());
    }

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos) {
        explode(serverLevel, owner, self() ? owner.blockPosition() : pos);
    }

    private void explode(Level level, Entity entity, BlockPos pos){
        Level.ExplosionInteraction interaction = Level.ExplosionInteraction.NONE;
        if(canBreak() && ForgeEventFactory.getMobGriefingEvent(level, entity)){
            interaction = destroyMode() ? Level.ExplosionInteraction.MOB : Level.ExplosionInteraction.BLOCK;
        }
        level.explode(entity, pos.getX(), pos.getY(), pos.getZ(), power(), interaction);
    }

    @Override
    public IEffectComponentType<?> getType() {
        return OTDEffectComponentTypes.EXPLOSION_EFFECT;
    }
}

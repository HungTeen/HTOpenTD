package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.common.event.events.EntityEffectEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-04 10:40
 **/
public record EventEffectComponent(ResourceLocation id) implements IEffectComponent {

    public static final Codec<EventEffectComponent> CODEC = RecordCodecBuilder.<EventEffectComponent>mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(EventEffectComponent::id)
            ).apply(instance, EventEffectComponent::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        MinecraftForge.EVENT_BUS.post(new EntityEffectEvent(serverLevel, id(), owner, entity));
    }

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos) {
        MinecraftForge.EVENT_BUS.post(new EntityEffectEvent(serverLevel, id(), owner, pos));
    }

    @Override
    public IEffectComponentType<?> getType() {
        return HTEffectComponents.EVENT_EFFECT;
    }
}

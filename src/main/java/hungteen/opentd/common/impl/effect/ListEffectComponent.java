package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.List;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-02 16:18
 **/
public record ListEffectComponent(List<Holder<IEffectComponent>> effects) implements IEffectComponent {

    public static final Codec<ListEffectComponent> CODEC = RecordCodecBuilder.<ListEffectComponent>mapCodec(instance -> instance.group(
            OTDEffectComponents.getCodec().listOf().fieldOf("effects").forGetter(ListEffectComponent::effects)
    ).apply(instance, ListEffectComponent::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        effects().forEach(l -> l.get().effectTo(serverLevel, owner, entity));
    }

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos) {
        effects().forEach(l -> l.get().effectTo(serverLevel, owner, pos));
    }

    @Override
    public IEffectComponentType<?> getType() {
        return OTDEffectComponentTypes.LIST_EFFECT;
    }
}

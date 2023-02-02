package hungteen.opentd.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.impl.filter.HTTargetFilters;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.Arrays;
import java.util.List;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-02 16:18
 **/
public record ListEffectComponent(List<IEffectComponent> effects) implements IEffectComponent {

    public static final Codec<ListEffectComponent> CODEC = RecordCodecBuilder.<ListEffectComponent>mapCodec(instance -> instance.group(
            HTEffectComponents.getCodec().listOf().fieldOf("effects").forGetter(ListEffectComponent::effects)
    ).apply(instance, ListEffectComponent::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        effects().forEach(l -> l.effectTo(serverLevel, owner, entity));
    }

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos) {
        effects().forEach(l -> l.effectTo(serverLevel, owner, pos));
    }

    @Override
    public IEffectComponentType<?> getType() {
        return HTEffectComponents.LIST_EFFECT;
    }
}

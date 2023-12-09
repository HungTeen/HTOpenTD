package hungteen.opentd.common.impl.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.common.impl.filter.OTDTargetFilters;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-02 16:01
 **/
public record FilterEffectComponent(Holder<ITargetFilter> targetFilter, Holder<IEffectComponent> effect) implements IEffectComponent{

    public static final Codec<FilterEffectComponent> CODEC = RecordCodecBuilder.<FilterEffectComponent>mapCodec(instance -> instance.group(
            OTDTargetFilters.getCodec().fieldOf("filter").forGetter(FilterEffectComponent::targetFilter),
            OTDEffectComponents.getCodec().fieldOf("effect").forGetter(FilterEffectComponent::effect)
    ).apply(instance, FilterEffectComponent::new)).codec();

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, Entity entity) {
        if(targetFilter().get().match(serverLevel, owner, entity)){
            effect().get().effectTo(serverLevel, owner, entity);
        }
    }

    @Override
    public void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos) {
        effect().get().effectTo(serverLevel, owner, pos);
    }

    @Override
    public IEffectComponentType<?> getType() {
        return OTDEffectComponentTypes.FILTER_EFFECT;
    }
}

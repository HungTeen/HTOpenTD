package hungteen.opentd.impl.effect;

import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.IEffectComponentType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-28 20:50
 **/
public record PotionEffectComponent() implements IEffectComponent {

    @Override
    public void effectTo(Entity owner, Entity entity) {

    }

    @Override
    public void effectTo(Entity owner, BlockPos pos) {

    }

    @Override
    public IEffectComponentType<?> getType() {
        return null;
    }
}

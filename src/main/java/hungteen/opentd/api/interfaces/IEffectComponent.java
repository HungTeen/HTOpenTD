package hungteen.opentd.api.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-20 19:14
 **/
public interface IEffectComponent {

    void effectTo(Entity owner, Entity entity);

    void effectTo(Entity owner, BlockState blockState, BlockPos pos);

    /**
     * Get the type of effect.
     * @return Effect type.
     */
    IEffectComponentType<?> getType();

}

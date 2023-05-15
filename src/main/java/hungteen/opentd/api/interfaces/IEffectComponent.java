package hungteen.opentd.api.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-20 19:14
 **/
public interface IEffectComponent {

    default void effectTo(ServerLevel serverLevel, Entity owner, List<Entity> targets, BlockPos pos){
        targets.forEach(target -> effectTo(serverLevel, owner, target));
        effectTo(serverLevel, owner, pos);
    }

    default void effectTo(ServerLevel serverLevel, Entity owner, Entity target, BlockPos pos){
        effectTo(serverLevel, owner, target);
        effectTo(serverLevel, owner, pos);
    }

    void effectTo(ServerLevel serverLevel, Entity owner, Entity entity);

    void effectTo(ServerLevel serverLevel, Entity owner, BlockPos pos);

    /**
     * Get the type of effect.
     * @return Effect type.
     */
    IEffectComponentType<?> getType();

}

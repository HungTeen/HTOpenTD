package hungteen.opentd.api.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-27 14:33
 **/
public interface ITargetFinder {

    List<Entity> getTargets(ServerLevel level, Entity entity);

    boolean stillValid(ServerLevel level, Entity entity, Entity target);

    default List<LivingEntity> getLivings(ServerLevel level, Entity entity){
        return getTargets(level, entity).stream().filter(LivingEntity.class::isInstance).map(LivingEntity.class::cast).collect(Collectors.toList());
    }

    /**
     * Get the type of target finder.
     * @return Target finder type.
     */
    ITargetFinderType<?> getType();
}
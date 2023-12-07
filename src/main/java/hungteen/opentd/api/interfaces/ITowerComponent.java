package hungteen.opentd.api.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-15 10:26
 **/
public interface ITowerComponent {

    @Nullable
    Entity createEntity(ServerLevel level, Player player, ItemStack stack, BlockPos pos);

    default CompoundTag getExtraNBT(){
        return new CompoundTag();
    }

    /**
     * Get the type of tower.
     * @return Tower type.
     */
    ITowerComponentType<?> getType();
}

package hungteen.opentd.common.event.events;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-01 22:53
 **/
@Cancelable
public class PostSummonTowerEvent extends SummonTowerEvent {

    private final Entity towerEntity;

    public PostSummonTowerEvent(Player player, ItemStack itemStack, InteractionHand hand, Entity towerEntity) {
        super(player, itemStack, hand);
        this.towerEntity = towerEntity;
    }

    public Entity getTowerEntity() {
        return towerEntity;
    }

}

package hungteen.opentd.common.event.events;

import net.minecraft.world.InteractionHand;
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
public class SummonTowerEvent extends PlayerEvent {

    private final ItemStack itemStack;
    private final InteractionHand hand;

    public SummonTowerEvent(Player player, ItemStack itemStack, InteractionHand hand) {
        super(player);
        this.itemStack = itemStack;
        this.hand = hand;
    }

    public InteractionHand getHand() {
        return hand;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

}

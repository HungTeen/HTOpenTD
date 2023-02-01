package hungteen.opentd.compat.kubejs.event;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import hungteen.opentd.common.event.events.SummonTowerEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-31 14:41
 **/
public class SummonTowerEventJS extends PlayerEventJS {
    private final SummonTowerEvent event;

    public SummonTowerEventJS(SummonTowerEvent event) {
        this.event = event;
    }

    public InteractionHand getHand() {
        return this.event.getHand();
    }

    public ItemStack getItemStack() {
        return this.event.getItemStack();
    }

    @Override
    public Player getEntity() {
        return this.event.getEntity();
    }

}

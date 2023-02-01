package hungteen.opentd.compat.kubejs.event;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import hungteen.opentd.common.event.events.PostSummonTowerEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-31 14:41
 **/
public class PostSummonTowerEventJS extends SummonTowerEventJS {
    private final PostSummonTowerEvent event;

    public PostSummonTowerEventJS(PostSummonTowerEvent event) {
        super(event);
        this.event = event;
    }

    public Entity getTowerEntity() {
        return this.event.getTowerEntity();
    }
}

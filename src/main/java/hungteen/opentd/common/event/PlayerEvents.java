package hungteen.opentd.common.event;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.world.entity.DummyEntityManager;
import hungteen.opentd.OpenTD;
import hungteen.opentd.common.item.SummonTowerItem;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-16 21:30
 **/
@Mod.EventBusSubscriber(modid = OpenTD.MOD_ID)
public class PlayerEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void interactAt(PlayerInteractEvent.EntityInteractSpecific event){
        SummonTowerItem.use(event);
    }
}

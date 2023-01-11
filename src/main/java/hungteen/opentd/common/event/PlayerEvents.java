package hungteen.opentd.common.event;

import hungteen.opentd.OpenTD;
import hungteen.opentd.common.capability.player.PlayerDataManager;
import hungteen.opentd.common.item.SummonTowerItem;
import hungteen.opentd.util.PlayerUtil;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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

    @SubscribeEvent
    public static void tickPlayer(TickEvent.PlayerTickEvent event) {
        if(! event.player.level.isClientSide){
            PlayerUtil.getOptManager(event.player).ifPresent(PlayerDataManager::tick);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (! event.getEntity().level.isClientSide) {
            PlayerUtil.getOptManager(event.getEntity()).ifPresent(PlayerDataManager::syncToClient);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (! event.getEntity().level.isClientSide) {
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerUtil.getOptManager(event.getOriginal()).ifPresent(oldOne -> {
            PlayerUtil.getOptManager(event.getEntity()).ifPresent(newOne -> {
                newOne.cloneFromExistingPlayerData(oldOne, event.isWasDeath());
            });
        });
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if(! event.getEntity().level.isClientSide) {
            PlayerUtil.getOptManager(event.getEntity()).ifPresent(PlayerDataManager::syncToClient);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if(! event.getEntity().level.isClientSide) {
            PlayerUtil.getOptManager(event.getEntity()).ifPresent(PlayerDataManager::syncToClient);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void interactAt(PlayerInteractEvent.EntityInteractSpecific event){
        SummonTowerItem.use(event);
    }
}

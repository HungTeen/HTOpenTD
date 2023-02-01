package hungteen.opentd.compat.kubejs;

import dev.architectury.event.Event;
import dev.architectury.event.EventActor;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.item.KubeJSItemEventHandler;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import hungteen.opentd.common.capability.OpenTDCapabilities;
import hungteen.opentd.common.event.events.PostSummonTowerEvent;
import hungteen.opentd.common.event.events.SummonTowerEvent;
import hungteen.opentd.compat.kubejs.event.PostSummonTowerEventJS;
import hungteen.opentd.compat.kubejs.event.SummonTowerEventJS;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-31 13:44
 **/
public class MyKubeJSPlugin extends KubeJSPlugin {

    @Override
    public void init() {
        final IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(EventPriority.NORMAL, MyKubeJSPlugin::preSummonTower);
        forgeBus.addListener(EventPriority.NORMAL, MyKubeJSPlugin::postSummonTower);
    }

    @Override
    public void registerEvents() {
        OTDKubeJSEvents.GROUP.register();
    }

    @Override
    public void registerClasses(ScriptType type, ClassFilter filter) {
//        filter.allow("hungteen.opentd");
    }

    private static void preSummonTower(SummonTowerEvent event) {
        if(OTDKubeJSEvents.PRE_SUMMON_TOWER.post(new SummonTowerEventJS(event))){
            event.setCanceled(true);
        }
    }

    private static void postSummonTower(PostSummonTowerEvent event) {
        OTDKubeJSEvents.POST_SUMMON_TOWER.post(new PostSummonTowerEventJS(event));
    }
}

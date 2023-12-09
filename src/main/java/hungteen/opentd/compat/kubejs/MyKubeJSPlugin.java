package hungteen.opentd.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import hungteen.opentd.common.event.events.*;
import hungteen.opentd.compat.kubejs.event.*;
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
        forgeBus.addListener(EventPriority.NORMAL, MyKubeJSPlugin::bulletHit);
        forgeBus.addListener(EventPriority.NORMAL, MyKubeJSPlugin::shootBullet);
        forgeBus.addListener(EventPriority.NORMAL, MyKubeJSPlugin::filterTarget);
        forgeBus.addListener(EventPriority.NORMAL, MyKubeJSPlugin::entityEffect);
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
        if(OTDKubeJSEvents.PRE_SUMMON_TOWER.post(new SummonTowerEventJS(event)).interruptFalse()){
            event.setCanceled(true);
        }
    }

    private static void postSummonTower(PostSummonTowerEvent event) {
        OTDKubeJSEvents.POST_SUMMON_TOWER.post(new PostSummonTowerEventJS(event));
    }

    private static void bulletHit(BulletHitEvent event) {
            OTDKubeJSEvents.BULLET_HIT.post(new BulletHitEventJS(event));
    }

    private static void shootBullet(ShootBulletEvent event) {
        if(OTDKubeJSEvents.SHOOT_BULLET.post(new ShootBulletEventJS(event)).interruptFalse()){
            event.setCanceled(true);
        }
    }

    private static void filterTarget(FilterTargetEvent event) {
        if(OTDKubeJSEvents.FILTER_TARGET.post(new FilterTargetEventJS(event)).interruptFalse()){
            event.setCanceled(true);
        }
    }

    private static void entityEffect(EntityEffectEvent event) {
        OTDKubeJSEvents.ENTITY_EFFECT.post(new EntityEffectEventJS(event));
    }
}

package hungteen.opentd.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import hungteen.opentd.compat.kubejs.event.PostSummonTowerEventJS;
import hungteen.opentd.compat.kubejs.event.SummonTowerEventJS;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-31 14:39
 **/
public interface OTDKubeJSEvents {

    EventGroup GROUP = EventGroup.of("OTDEvents");

    EventHandler PRE_SUMMON_TOWER = GROUP.server("preSummonTower", () -> SummonTowerEventJS.class).cancelable();
    EventHandler POST_SUMMON_TOWER = GROUP.server("postSummonTower", () -> PostSummonTowerEventJS.class);

}

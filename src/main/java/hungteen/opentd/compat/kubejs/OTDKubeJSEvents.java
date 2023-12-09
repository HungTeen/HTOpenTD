package hungteen.opentd.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import hungteen.opentd.compat.kubejs.event.*;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-31 14:39
 **/
public interface OTDKubeJSEvents {

    EventGroup GROUP = EventGroup.of("OTDEvents");

    EventHandler PRE_SUMMON_TOWER = GROUP.server("preSummonTower", () -> SummonTowerEventJS.class).hasResult();
    EventHandler POST_SUMMON_TOWER = GROUP.server("postSummonTower", () -> PostSummonTowerEventJS.class);
    EventHandler BULLET_HIT = GROUP.server("bulletHit", () -> BulletHitEventJS.class);
    EventHandler SHOOT_BULLET = GROUP.server("shootBullet", () -> ShootBulletEventJS.class).hasResult();
    EventHandler FILTER_TARGET = GROUP.server("filterTarget", () -> FilterTargetEventJS.class).hasResult();
    EventHandler ENTITY_EFFECT = GROUP.server("entityEffect", () -> EntityEffectEventJS.class);
}

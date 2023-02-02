package hungteen.opentd.compat.kubejs.event;

import dev.latvian.mods.kubejs.entity.LivingEntityEventJS;
import hungteen.opentd.common.entity.BulletEntity;
import hungteen.opentd.common.event.events.ShootBulletEvent;
import net.minecraft.world.entity.LivingEntity;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-02 13:51
 **/
public class ShootBulletEventJS extends LivingEntityEventJS {

    private final ShootBulletEvent event;

    public ShootBulletEventJS(ShootBulletEvent event) {
        this.event = event;
    }

    @Override
    public LivingEntity getEntity() {
        return this.event.getEntity();
    }

    public BulletEntity getBulletEntity() {
        return this.event.getBullet();
    }
}

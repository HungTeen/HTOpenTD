package hungteen.opentd.common.event.events;

import hungteen.opentd.common.entity.BulletEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-02 13:46
 **/
public class ShootBulletEvent extends LivingEvent {

    private final BulletEntity bullet;

    public ShootBulletEvent(LivingEntity entity, BulletEntity bullet) {
        super(entity);
        this.bullet = bullet;
    }

    public BulletEntity getBullet() {
        return bullet;
    }
}

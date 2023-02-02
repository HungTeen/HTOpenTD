package hungteen.opentd.common.event.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-02 11:07
 **/
public class ProduceEvent extends LivingEvent {

    public ProduceEvent(LivingEntity entity) {
        super(entity);
    }

}

package hungteen.opentd.api.interfaces;

import hungteen.htlib.api.interfaces.ISimpleEntry;
import net.minecraft.world.entity.Entity;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-18 17:33
 **/
public interface IEntityClassifier extends ISimpleEntry {

    Class<?> getEntityClass();

}

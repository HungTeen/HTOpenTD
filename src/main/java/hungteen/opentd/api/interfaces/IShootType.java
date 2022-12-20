package hungteen.opentd.api.interfaces;

import hungteen.htlib.api.interfaces.ISimpleEntry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;

import java.util.function.Consumer;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-20 15:32
 **/
public interface IShootType extends ISimpleEntry {

    void shoot(Entity target, Consumer<Entity> consumer);

}

package hungteen.opentd.api.interfaces;

import net.minecraft.world.entity.Entity;

import javax.annotation.Nonnull;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-20 12:46
 **/
public interface IRangeAttackEntity {

    /**
     * can not attack because of itself.
     */
    boolean canAttack();

    /**
     * set attack tick for animation.
     */
    void setAttackTick(int tick);

    /**
     * get attack tick for animation.
     */
    int getAttackTick();

    /**
     * real cool down each attack.(after affected by effect etc.)
     */
    double getCurrentAttackCD();

    /**
     * perform attack to entity.
     */
    void startAttack(@Nonnull Entity target);

    int getStartAttackTick();

}

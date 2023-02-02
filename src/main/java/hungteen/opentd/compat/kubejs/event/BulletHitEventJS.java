package hungteen.opentd.compat.kubejs.event;

import dev.latvian.mods.kubejs.entity.EntityEventJS;
import hungteen.opentd.common.entity.BulletEntity;
import hungteen.opentd.common.event.events.BulletHitEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-02 13:38
 **/
public class BulletHitEventJS extends EntityEventJS {

    private final BulletHitEvent event;

    public BulletHitEventJS(BulletHitEvent event) {
        this.event = event;
    }

    @Nullable
    public EntityHitResult getEntityHitResult() {
        return this.event.getEntityHitResult();
    }

    @Nullable
    public BlockHitResult getBlockHitResult(){
        return this.event.getBlockHitResult();
    }

    @Override
    public Entity getEntity() {
        return this.getBulletEntity();
    }

    public BulletEntity getBulletEntity() {
        return this.event.getBulletEntity();
    }
}

package hungteen.opentd.common.event.events;

import hungteen.opentd.common.entity.BulletEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-02 13:31
 **/
public class BulletHitEvent extends EntityEvent {

    private final BulletEntity bulletEntity;
    private EntityHitResult entityHitResult;
    private BlockHitResult blockHitResult;

    public BulletHitEvent(BulletEntity entity, EntityHitResult entityHitResult) {
        super(entity);
        this.bulletEntity = entity;
        this.entityHitResult = entityHitResult;
    }

    public BulletHitEvent(BulletEntity entity, BlockHitResult blockHitResult) {
        super(entity);
        this.bulletEntity = entity;
        this.blockHitResult = blockHitResult;
    }

    public BulletEntity getBulletEntity() {
        return bulletEntity;
    }

    public BlockHitResult getBlockHitResult() {
        return blockHitResult;
    }

    public EntityHitResult getEntityHitResult() {
        return entityHitResult;
    }
}

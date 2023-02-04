package hungteen.opentd.common.event.events;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nullable;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-04 10:42
 **/
@Cancelable
public class EntityEffectEvent extends EntityEvent {

    private final ServerLevel serverLevel;
    private final ResourceLocation id;
    private Entity target;
    private BlockPos blockPos;

    public EntityEffectEvent(ServerLevel serverLevel, ResourceLocation id, Entity entity, Entity target) {
        super(entity);
        this.id = id;
        this.serverLevel = serverLevel;
        this.target = target;
    }

    public EntityEffectEvent(ServerLevel serverLevel, ResourceLocation id, Entity entity, BlockPos blockPos) {
        super(entity);
        this.id = id;
        this.serverLevel = serverLevel;
        this.blockPos = blockPos;
    }

    public ResourceLocation getId() {
        return id;
    }

    public ServerLevel getServerLevel() {
        return serverLevel;
    }

    @Nullable
    public Entity getTarget() {
        return target;
    }

    @Nullable
    public BlockPos getBlockPos() {
        return blockPos;
    }
}

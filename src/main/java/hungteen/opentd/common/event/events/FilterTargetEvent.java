package hungteen.opentd.common.event.events;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-04 10:42
 **/
@Cancelable
public class FilterTargetEvent extends EntityEvent {

    private final ServerLevel serverLevel;
    private final ResourceLocation id;
    private final Entity target;
    private boolean match = false;

    public FilterTargetEvent(ServerLevel serverLevel, ResourceLocation id, Entity entity, Entity target) {
        super(entity);
        this.id = id;
        this.serverLevel = serverLevel;
        this.target = target;
    }

    public void setMatch(){
        this.match = true;
    }

    public ResourceLocation getId() {
        return id;
    }

    public boolean isMatch() {
        return this.match;
    }

    public ServerLevel getServerLevel() {
        return serverLevel;
    }

    public Entity getTarget() {
        return target;
    }
}

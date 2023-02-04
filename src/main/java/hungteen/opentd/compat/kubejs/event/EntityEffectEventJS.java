package hungteen.opentd.compat.kubejs.event;

import dev.latvian.mods.kubejs.entity.EntityEventJS;
import hungteen.opentd.common.event.events.EntityEffectEvent;
import hungteen.opentd.common.event.events.FilterTargetEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import javax.annotation.Nullable;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-04 10:54
 **/
public class EntityEffectEventJS extends EntityEventJS {

    private final EntityEffectEvent event;

    public EntityEffectEventJS(EntityEffectEvent event) {
        this.event = event;
    }

    public ResourceLocation getId(){
        return event.getId();
    }

    @Nullable
    public Entity getTarget(){
        return event.getTarget();
    }

    @Nullable
    public BlockPos getBlockPos(){
        return event.getBlockPos();
    }

    @Override
    public Entity getEntity() {
        return event.getEntity();
    }
}

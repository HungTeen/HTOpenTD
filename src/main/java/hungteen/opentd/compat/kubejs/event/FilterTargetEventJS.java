package hungteen.opentd.compat.kubejs.event;

import dev.latvian.mods.kubejs.entity.EntityEventJS;
import hungteen.opentd.common.event.events.FilterTargetEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-04 10:54
 **/
public class FilterTargetEventJS extends EntityEventJS {

    private final FilterTargetEvent event;

    public FilterTargetEventJS(FilterTargetEvent event) {
        this.event = event;
    }

    public boolean isMatch(){
        return event.isMatch();
    }

    public void setMatch(){
        event.setMatch();
    }

    public ResourceLocation getId(){
        return event.getId();
    }

    public Entity getTarget(){
        return event.getTarget();
    }

    @Override
    public Entity getEntity() {
        return event.getEntity();
    }
}

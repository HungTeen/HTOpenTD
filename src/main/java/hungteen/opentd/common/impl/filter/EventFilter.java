package hungteen.opentd.common.impl.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import hungteen.opentd.common.event.events.FilterTargetEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-04 10:40
 **/
public record EventFilter(ResourceLocation id) implements ITargetFilter {

    public static final Codec<EventFilter> CODEC = RecordCodecBuilder.<EventFilter>mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(EventFilter::id)
            ).apply(instance, EventFilter::new)).codec();

    @Override
    public boolean match(ServerLevel level, Entity owner, Entity target) {
        final FilterTargetEvent event = new FilterTargetEvent(level, id(), owner, target);
        final boolean canceled = MinecraftForge.EVENT_BUS.post(event);
        return event.isMatch() && ! canceled;
    }

    @Override
    public ITargetFilterType<?> getType() {
        return OTDTargetFilterTypes.EVENT_FILTER;
    }

}

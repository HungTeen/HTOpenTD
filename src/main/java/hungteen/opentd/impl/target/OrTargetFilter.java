package hungteen.opentd.impl.target;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import net.minecraft.core.Registry;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;

import java.util.List;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 18:14
 **/
public record OrTargetFilter(List<ITargetFilter> filters) implements ITargetFilter {

    public static final Codec<OrTargetFilter> CODEC = RecordCodecBuilder.<OrTargetFilter>mapCodec(instance -> instance.group(
            HTTargetFilters.getCodec().listOf().fieldOf("filters").forGetter(OrTargetFilter::filters)
    ).apply(instance, OrTargetFilter::new)).codec();

    @Override
    public boolean match(Mob owner, Entity target) {
        return this.filters().stream().anyMatch(l -> l.match(owner, target));
    }

    @Override
    public ITargetFilterType<?> getType() {
        return HTTargetFilters.OR_FILTER;
    }
}

package hungteen.opentd.impl.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 18:08
 **/
public record TagTargetFilter(TagKey<EntityType<?>> entityTag) implements ITargetFilter {

    public static final Codec<TagTargetFilter> CODEC = RecordCodecBuilder.<TagTargetFilter>mapCodec(instance -> instance.group(
            TagKey.hashedCodec(Registry.ENTITY_TYPE_REGISTRY).fieldOf("tag").forGetter(TagTargetFilter::entityTag)
    ).apply(instance, TagTargetFilter::new)).codec();

    @Override
    public boolean match(Entity owner, Entity target) {
        return target.getType().is(this.entityTag());
    }

    @Override
    public ITargetFilterType<?> getType() {
        return HTTargetFilters.TAG_FILTER;
    }
}

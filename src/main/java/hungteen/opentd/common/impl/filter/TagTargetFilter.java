package hungteen.opentd.common.impl.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 18:08
 **/
public record TagTargetFilter(Optional<TagKey<EntityType<?>>> typeTag, Optional<String> entityTag) implements ITargetFilter {

    public static final Codec<TagTargetFilter> CODEC = RecordCodecBuilder.<TagTargetFilter>mapCodec(instance -> instance.group(
            Codec.optionalField("tag", TagKey.hashedCodec(Registry.ENTITY_TYPE_REGISTRY)).forGetter(TagTargetFilter::typeTag),
            Codec.optionalField("entity_tag", Codec.STRING).forGetter(TagTargetFilter::entityTag)
    ).apply(instance, TagTargetFilter::new)).codec();

    @Override
    public boolean match(ServerLevel level, Entity owner, Entity target) {
        if(typeTag().isPresent() && ! target.getType().is(typeTag().get())){
            return false;
        }
        if(entityTag().isPresent() && ! target.getTags().contains(entityTag().get())){
            return false;
        }
        return true;
    }

    @Override
    public ITargetFilterType<?> getType() {
        return OTDTargetFilterTypes.TAG_FILTER;
    }
}

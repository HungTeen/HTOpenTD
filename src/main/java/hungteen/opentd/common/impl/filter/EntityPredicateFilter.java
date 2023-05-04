package hungteen.opentd.common.impl.filter;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-18 11:22
 **/
public record EntityPredicateFilter(EntityPredicate ownerPredicate, EntityPredicate targetPredicate) implements ITargetFilter {

    public static final Codec<EntityPredicate> ENTITY_PREDICATE_CODEC = Codec.PASSTHROUGH.comapFlatMap((dynamic) -> {
        JsonElement tag = dynamic.convert(JsonOps.INSTANCE).getValue();
        return DataResult.success(EntityPredicate.fromJson(tag));
    }, (predicate) -> {
        return new Dynamic<>(JsonOps.INSTANCE, predicate.serializeToJson());
    });

    public static final Codec<EntityPredicateFilter> CODEC = RecordCodecBuilder.<EntityPredicateFilter>mapCodec(instance -> instance.group(
            ENTITY_PREDICATE_CODEC.optionalFieldOf("owner_predicate", EntityPredicate.ANY).forGetter(EntityPredicateFilter::ownerPredicate),
            ENTITY_PREDICATE_CODEC.optionalFieldOf("target_predicate", EntityPredicate.ANY).forGetter(EntityPredicateFilter::targetPredicate)
    ).apply(instance, EntityPredicateFilter::new)).codec();

    @Override
    public boolean match(ServerLevel level, Entity owner, Entity target) {
        return ownerPredicate().matches(level, owner.position(), owner) && targetPredicate().matches(level, target.position(), target);
    }

    @Override
    public ITargetFilterType<?> getType() {
        return HTTargetFilters.ENTITY_PREDICATE_FILTER;
    }

}

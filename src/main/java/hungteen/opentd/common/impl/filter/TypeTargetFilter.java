package hungteen.opentd.common.impl.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 18:18
 **/
public record TypeTargetFilter(List<EntityType<?>> types) implements ITargetFilter {

    public static final Codec<TypeTargetFilter> CODEC = RecordCodecBuilder.<TypeTargetFilter>mapCodec(instance -> instance.group(
            ForgeRegistries.ENTITY_TYPES.getCodec().listOf().fieldOf("types").forGetter(TypeTargetFilter::types)
    ).apply(instance, TypeTargetFilter::new)).codec();

    @Override
    public boolean match(ServerLevel level, Entity owner, Entity target) {
        return this.types().stream().anyMatch(l -> l.equals(target.getType()));
    }

    @Override
    public ITargetFilterType<?> getType() {
        return HTTargetFilters.TYPE_FILTER;
    }

}

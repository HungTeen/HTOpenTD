package hungteen.opentd.common.impl.filter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFilterType;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-02-04 10:34
 **/
public record NBTTargetFilter(CompoundTag selfNbt, CompoundTag targetNbt) implements ITargetFilter {

    public static final Codec<NBTTargetFilter> CODEC = RecordCodecBuilder.<NBTTargetFilter>mapCodec(instance -> instance.group(
            CompoundTag.CODEC.optionalFieldOf("self_nbt", new CompoundTag()).forGetter(NBTTargetFilter::selfNbt),
            CompoundTag.CODEC.optionalFieldOf("target_nbt", new CompoundTag()).forGetter(NBTTargetFilter::targetNbt)
    ).apply(instance, NBTTargetFilter::new)).codec();

    @Override
    public boolean match(ServerLevel level, Entity owner, Entity target) {
        return NbtUtils.compareNbt(this.selfNbt(), NbtPredicate.getEntityTagToCompare(owner), true)
                && NbtUtils.compareNbt(this.targetNbt(), NbtPredicate.getEntityTagToCompare(target), true);
    }

    @Override
    public ITargetFilterType<?> getType() {
        return HTTargetFilters.NBT_FILTER;
    }
}

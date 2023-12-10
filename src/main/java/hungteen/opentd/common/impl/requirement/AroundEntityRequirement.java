package hungteen.opentd.common.impl.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.PlayerHelper;
import hungteen.htlib.util.helper.registry.EntityHelper;
import hungteen.opentd.api.interfaces.ISummonRequirement;
import hungteen.opentd.api.interfaces.ISummonRequirementType;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.common.impl.filter.OTDTargetFilters;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-17 11:22
 **/
public record AroundEntityRequirement(double width, double height, int minCount, int maxCount, Optional<String> tip, Holder<ITargetFilter> filter) implements ISummonRequirement {

    public static final Codec<AroundEntityRequirement> CODEC = RecordCodecBuilder.<AroundEntityRequirement>mapCodec(instance -> instance.group(
            Codec.DOUBLE.fieldOf("width").forGetter(AroundEntityRequirement::width),
            Codec.DOUBLE.fieldOf("height").forGetter(AroundEntityRequirement::height),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("min_count", 0).forGetter(AroundEntityRequirement::minCount),
            Codec.intRange(0, Integer.MAX_VALUE).optionalFieldOf("max_count", Integer.MAX_VALUE).forGetter(AroundEntityRequirement::maxCount),
            Codec.optionalField("tip", Codec.STRING).forGetter(AroundEntityRequirement::tip),
            OTDTargetFilters.getCodec().fieldOf("filter").forGetter(AroundEntityRequirement::filter)
    ).apply(instance, AroundEntityRequirement::new)).codec();

    @Override
    public boolean allowOn(ServerLevel level, Player player, Entity entity, boolean sendMessage) {
        return check(level, player, entity.position(), sendMessage);
    }

    @Override
    public boolean allowOn(ServerLevel level, Player player, BlockState state, BlockPos pos, boolean sendMessage) {
        return check(level, player, MathHelper.toVec3(pos), sendMessage);
    }

    public boolean check(ServerLevel level, Player player, Vec3 pos, boolean sendMessage){
        final AABB aabb = MathHelper.getAABB(pos, width(), height());
        final int size = EntityHelper.getPredicateEntities(player, aabb, Entity.class, l -> filter().get().match(level, player, l)).size();
        if(size < minCount() || size > maxCount()){
            if(sendMessage) PlayerHelper.sendTipTo(player, getTip());
            return false;
        }
        return true;
    }

    @Override
    public void consume(ServerLevel level, Player player) {
    }

    public Component getTip() {
        return Component.translatable(this.tip().orElse("tip.opentd.entity_count"), minCount(), maxCount());
    }

    @Override
    public ISummonRequirementType<?> getType() {
        return OTDRequirementTypes.AROUND_ENTITY_REQUIREMENT;
    }
}

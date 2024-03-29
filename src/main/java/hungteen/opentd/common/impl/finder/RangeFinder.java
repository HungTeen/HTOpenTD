package hungteen.opentd.common.impl.finder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFinder;
import hungteen.opentd.api.interfaces.ITargetFinderType;
import hungteen.opentd.common.impl.filter.OTDTargetFilters;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-27 14:38
 **/
public record RangeFinder(boolean checkSight, float width, float height, Holder<ITargetFilter> targetFilter) implements ITargetFinder {

    public static final Codec<RangeFinder> CODEC = RecordCodecBuilder.<RangeFinder>mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("check_sight", true).forGetter(RangeFinder::checkSight),
            Codec.floatRange(0, Float.MAX_VALUE).fieldOf("width").forGetter(RangeFinder::width),
            Codec.floatRange(0, Float.MAX_VALUE).fieldOf("height").forGetter(RangeFinder::height),
            OTDTargetFilters.getCodec().fieldOf("target_filter").forGetter(RangeFinder::targetFilter)
    ).apply(instance, RangeFinder::new)).codec();

    @Override
    public List<Entity> getTargets(ServerLevel level, Entity entity) {
        return level.getEntitiesOfClass(Entity.class, getAABB(entity)).stream().filter(l -> this.targetFilter().get().match(level, entity, l)).filter(l -> this.checkTarget(entity, l)).collect(Collectors.toList());
    }

    private boolean checkTarget(Entity entity, Entity target) {
        if(entity instanceof Mob){
            return (! this.checkSight() || ((Mob) entity).getSensing().hasLineOfSight(target)) || entity.equals(target);
        }
        return true;
    }

    @Override
    public boolean stillValid(ServerLevel level, Entity entity, Entity target) {
        return Math.abs(entity.getX() - target.getX()) <= this.width()
                && Math.abs(entity.getZ() - target.getZ()) <= this.width()
                && Math.abs(entity.getY() - target.getY()) <= this.height() && checkTarget(entity, target);
    }

    protected AABB getAABB(Entity entity) {
        return new AABB(entity.getX() + this.width(),
                entity.getY() + this.height(),
                entity.getZ() + this.width(),
                entity.getX() - this.width(),
                entity.getY() - this.height(),
                entity.getZ() - this.width());
    }

    @Override
    public ITargetFinderType<?> getType() {
        return OTDTargetFinderTypes.RANGE_FINDER;
    }
}

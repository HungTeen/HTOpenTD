package hungteen.opentd.impl.finder;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.util.helper.EntityHelper;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.opentd.api.interfaces.ITargetFilter;
import hungteen.opentd.api.interfaces.ITargetFinder;
import hungteen.opentd.api.interfaces.ITargetFinderType;
import hungteen.opentd.impl.filter.HTTargetFilters;
import hungteen.opentd.util.EntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-27 15:52
 **/
public record LineFinder(float horizontalDegree, float verticalDegree, float length, ITargetFilter targetFilter) implements ITargetFinder {

    public static final Codec<LineFinder> CODEC = RecordCodecBuilder.<LineFinder>mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("horizontal_degree", 0F).forGetter(LineFinder::horizontalDegree),
            Codec.FLOAT.optionalFieldOf("vertical_degree", 0F).forGetter(LineFinder::verticalDegree),
            Codec.floatRange(0, Float.MAX_VALUE).fieldOf("length").forGetter(LineFinder::length),
            HTTargetFilters.getCodec().fieldOf("target_filter").forGetter(LineFinder::targetFilter)
    ).apply(instance, LineFinder::new)).codec();

    @Override
    public List<Entity> getTargets(Level level, Entity entity) {
        final Vec3 direction = MathHelper.rotate(entity.getViewVector(1F), horizontalDegree(), verticalDegree());
        final EntityHitResult entityRay = EntityHelper.rayTraceEntities(level, entity, direction, length, (target) -> {
            return this.targetFilter().match(entity, target);
        });
        if(entityRay != null && entityRay.getType() == HitResult.Type.ENTITY) {
            return Arrays.asList(entityRay.getEntity());
        }
        return Arrays.asList();
    }

    @Override
    public boolean stillValid(Level level, Entity entity, Entity target) {
        return getTargets(level, entity).contains(target);
    }

    @Override
    public ITargetFinderType<?> getType() {
        return HTTargetFinders.LINE_FINDER;
    }
}

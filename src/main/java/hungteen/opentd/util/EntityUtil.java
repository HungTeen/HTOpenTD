package hungteen.opentd.util;

import hungteen.opentd.OpenTD;
import hungteen.opentd.common.effect.OpenTDEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-23 15:01
 **/
public class EntityUtil {

    public static boolean inEnergetic(LivingEntity entity){
        return entity.hasEffect(OpenTDEffects.ENERGETIC_EFFECT.get());
    }

    /**
     * used for bullets hit check.
     * get predicate entity between start to end.
     */
    public static EntityHitResult rayTraceEntities(Level world, Entity entity, Vec3 startVec, Vec3 endVec, Predicate<Entity> predicate) {
        return ProjectileUtil.getEntityHitResult(world, entity, startVec, endVec,
                entity.getBoundingBox().expandTowards(entity.getDeltaMovement()).inflate(0.5D), predicate);
    }

    /**
     * used for item ray trace.
     * get predicate entity with dis and vec.
     */
    public static EntityHitResult rayTraceEntities(Level world, Entity entity, Vec3 lookVec, double distance, Predicate<Entity> predicate) {
        final Vec3 startVec = entity.getEyePosition();
        Vec3 endVec = startVec.add(lookVec.normalize().scale(distance));
        ClipContext ray = new ClipContext(startVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
        BlockHitResult result = world.clip(ray);
        if(result.getType() != HitResult.Type.MISS) {// hit something
            endVec = result.getLocation();
        }
        return ProjectileUtil.getEntityHitResult(world, entity, startVec, endVec,
                entity.getBoundingBox().inflate(distance), predicate);
    }

}

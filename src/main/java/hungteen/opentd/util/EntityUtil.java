package hungteen.opentd.util;

import hungteen.opentd.common.effect.OpenTDEffects;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-23 15:01
 **/
public class EntityUtil {

    public static boolean inEnergetic(LivingEntity entity) {
        return entity.hasEffect(OpenTDEffects.ENERGETIC_EFFECT.get());
    }

    /**
     * {@link net.minecraft.world.entity.projectile.ProjectileUtil#rotateTowardsMovement(Entity, float)}.
     */
    public static void rotateTowardsMovement(Entity entity, float partial) {
        Vec3 vec3 = entity.getDeltaMovement();
        if (vec3.lengthSqr() != 0.0D) {
            double d0 = vec3.horizontalDistance();
            entity.setYRot((float)(Mth.atan2(vec3.z, vec3.x) * (double)(180F / (float)Math.PI)) + 90.0F);
            entity.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));

            while(entity.getXRot() - entity.xRotO < -180.0F) {
                entity.xRotO -= 360.0F;
            }

            while(entity.getXRot() - entity.xRotO >= 180.0F) {
                entity.xRotO += 360.0F;
            }

            while(entity.getYRot() - entity.yRotO < -180.0F) {
                entity.yRotO -= 360.0F;
            }

            while(entity.getYRot() - entity.yRotO >= 180.0F) {
                entity.yRotO += 360.0F;
            }

            entity.setXRot(Mth.lerp(partial, entity.xRotO, entity.getXRot()));
            entity.setYRot(Mth.lerp(partial, entity.yRotO, entity.getYRot()));
        }

    }

}

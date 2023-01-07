package hungteen.opentd.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-27 17:05
 **/
public class MathUtil {

    public static Vec3 rotate(Vec3 vec, double horizontalDegree, double verticalDegree) {
        final double horizontalRadians = Math.toRadians(horizontalDegree);
        final double verticalRadians = Math.toRadians(verticalDegree);
        final double horizontalLength = Math.sqrt(vec.x() * vec.x() + vec.z() * vec.z());
        // Rotate horizontally.
        final double x = vec.x() * Math.cos(horizontalRadians) - vec.z() * Math.sin(horizontalRadians);
        final double z = vec.z() * Math.cos(horizontalRadians) + vec.x() * Math.sin(horizontalRadians);
        // Rotate vertically.
        final double xz = horizontalLength * Math.cos(verticalRadians) - vec.y() * Math.sin(verticalRadians);
        final double y = vec.y() * Math.cos(verticalRadians) + horizontalLength * Math.sin(verticalRadians);
        return new Vec3(x / horizontalLength * xz, y, z / horizontalLength * xz);
    }

    public static double smooth(double from, double to, int tick, int cd){
        return smooth(from, to, cd == 0 ? 1F : tick * 1F / cd);
    }

    public static double smooth(double from, double to, double percent){
        return from + (to - from) * percent;
    }

    /**
     * get expand collide box.
     */
    public static AABB getAABB(Vec3 pos, double radius, double height) {
        return new AABB(pos.x() - radius, pos.y() - height, pos.z() - radius, pos.x() + radius, pos.y() + height, pos.z() + radius);
    }

}

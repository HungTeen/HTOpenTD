package hungteen.opentd.util;

import hungteen.opentd.OpenTD;
import hungteen.opentd.common.effect.OpenTDEffects;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-23 15:01
 **/
public class EntityUtil {

    public static boolean inEnergetic(LivingEntity entity) {
        return entity.hasEffect(OpenTDEffects.ENERGETIC_EFFECT.get());
    }

}

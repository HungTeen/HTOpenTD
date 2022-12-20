package hungteen.opentd.common.entity.ai;

import hungteen.htlib.util.helper.EntityHelper;
import hungteen.opentd.common.entity.TowerEntity;
import hungteen.opentd.impl.tower.PVZPlantComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 18:44
 **/
public class TowerTargetGoal extends Goal {

    protected final TowerEntity towerEntity;
    private final PVZPlantComponent.TargetSettings targetSettings;
    protected LivingEntity targetMob;

    public TowerTargetGoal(TowerEntity towerEntity, PVZPlantComponent.TargetSettings targetSettings) {
        this.towerEntity = towerEntity;
        this.targetSettings = targetSettings;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.targetSettings.chance() > 0 && this.towerEntity.getRandom().nextInt(this.targetSettings.chance()) != 0) {
            return false;
        }
        List<LivingEntity> list1 = this.towerEntity.level.getEntitiesOfClass(LivingEntity.class, getAABB()).stream().filter(this::checkTarget).collect(Collectors.toList());

        final LivingEntity target = this.chooseTarget(list1);
        if (target != null) {
            this.targetMob = target;
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        this.towerEntity.setTarget(this.targetMob);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity entity = this.towerEntity.getTarget();
        //alternative target not exist.
        if (!EntityHelper.isEntityValid(this.targetMob)) {
            return false;
        }
        if (!EntityHelper.isEntityValid(entity)) {
            entity = this.targetMob;
        } else if (!entity.is(this.targetMob)) {
            this.targetMob = entity;
        }
        if (!EntityHelper.isEntityValid(entity)) {
            return false;
        }
        //already out range.
        if (Math.abs(this.towerEntity.getX() - entity.getX()) > this.targetSettings.width()
                || Math.abs(this.towerEntity.getZ() - entity.getZ()) > this.targetSettings.width()
                || Math.abs(this.towerEntity.getY() - entity.getY()) > this.targetSettings.height()) {
            return false;
        }
        //can attack.
        if (this.checkTarget(entity)) {
            this.towerEntity.setTarget(entity);
            return true;
        }
        return false;
    }

    @Override
    public void stop() {
        this.towerEntity.setTarget(null);
    }

    protected LivingEntity chooseTarget(List<LivingEntity> list) {
        return list.isEmpty() ? null : list.get(0);
    }

//    protected boolean checkSenses(Entity entity) {
//        return this.mob.getSensing().hasLineOfSight(entity);
//    }

    protected boolean checkTarget(LivingEntity entity) {
        return (! this.targetSettings.checkSight() || this.towerEntity.getSensing().hasLineOfSight(entity)) && this.targetSettings.match(this.towerEntity, entity);
    }

    protected AABB getAABB() {
        final double width = this.targetSettings.width();
        final double height = this.targetSettings.height();
        return new AABB(this.towerEntity.getX() + width, this.towerEntity.getY() + width,
                this.towerEntity.getZ() + height, this.towerEntity.getX() - height,
                this.towerEntity.getY() - width, this.towerEntity.getZ() - width);
    }

}

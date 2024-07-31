package hungteen.opentd.common.entity.ai;

import hungteen.htlib.util.helper.registry.EntityHelper;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.common.codec.LaserGoalSetting;
import hungteen.opentd.common.entity.TowerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Objects;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/15 19:23
 */
public class TowerLaserAttackGoal extends HTGoal {

    private final TowerEntity towerEntity;
    protected LivingEntity target;
    private int attackTime;

    public TowerLaserAttackGoal(TowerEntity towerEntity) {
        this.towerEntity = towerEntity;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        this.attackTime = -1;
    }

    @Override
    public boolean canUse() {
        if (this.setting() == null) {
            return false;
        }
        this.target = this.towerEntity.getTarget();
        if (!this.checkTarget()) {//can not attack because of its target(such as height limit).
            return false;
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void stop() {
        this.towerEntity.setActiveAttackTarget(0);
        this.attackTime = -1;
    }

    @Override
    public void tick() {
        if (this.towerEntity.canChangeDirection()) {
            this.towerEntity.getLookControl().setLookAt(this.target, 90.0F, 90.0F);
        }
        // 冷却结束 或者 动画已经开始。
        if (this.towerEntity.preLaserTick <= 0 || this.attackTime >= 0) {
            ++this.attackTime;
            if (this.attackTime == 0) {
                this.towerEntity.setActiveAttackTarget(this.target.getId());
                if (!this.towerEntity.isSilent()) {
                    this.towerEntity.level().broadcastEntityEvent(this.towerEntity, (byte) 21);
                }
            }
            if (this.attackTime > Objects.requireNonNull(this.setting()).duration()) {
                this.attackTime = -1;
                this.towerEntity.setActiveAttackTarget(0);
                this.towerEntity.preAttackTick = Objects.requireNonNull(this.setting()).coolDown();
            } else {
                if (this.attackTime > 0 && this.attackTime % this.setting().effectInterval() == 0) {
                    this.setting().continueEffect().ifPresent(l -> this.effectTo(l.get()));
                }
                if (this.attackTime >= this.setting().duration()) {
                    this.setting().finalEffect().ifPresent(l -> this.effectTo(l.get()));
                }
            }
        } else {
            --this.towerEntity.preLaserTick;
        }
        if (Objects.requireNonNull(this.setting()).needRest()) {
            this.towerEntity.setResting(this.towerEntity.preLaserTick > 0);
        }
    }

    private void effectTo(IEffectComponent effect) {
        if(this.towerEntity.level() instanceof ServerLevel serverLevel){
            final Vec3 st = this.towerEntity.getEyePosition();
            final Vec3 ep = this.target.getEyePosition();
            final Vec3 laser = ep.subtract(st);
            final AABB aabb = EntityHelper.getEntityAABB(this.towerEntity, this.setting().laserDistance(), this.setting().laserDistance());
            EntityHelper.getPredicateEntities(this.towerEntity, aabb, Entity.class, entity -> {
                if(this.setting().laserFilter().isPresent() && this.setting().laserFilter().get().get().match(serverLevel, this.towerEntity, entity)){
                    final Vec3 tp = entity.getEyePosition();
                    final Vec3 vec = tp.subtract(st);
                    if(vec.length() < this.setting().laserDistance() && vec.length() > 0 && laser.length() > 0) {
                        final double cos = laser.dot(vec) / vec.length() / laser.length();
                        final double sin = Math.sqrt(1 - cos * cos);
                        return sin * vec.length() <= this.setting().laserWidth();
                    }
                }
                return false;
            }).forEach(entity -> {
                effect.effectTo(serverLevel, this.towerEntity, entity);
            });
            effect.effectTo(serverLevel, this.towerEntity, this.target.blockPosition());
        }
    }

    private LaserGoalSetting setting() {
        return this.towerEntity.getLaserSetting();
    }

    protected boolean checkTarget() {
        return EntityHelper.isEntityValid(this.target)
                && this.towerEntity.canAttack(this.target)
                && this.towerEntity.getSensing().hasLineOfSight(this.target)
                && this.towerEntity.distanceTo(this.target) <= Math.min(
                Objects.requireNonNull(this.setting()).laserDistance(),
                Objects.requireNonNull(this.setting()).trackDistance()
        );
    }


}

package hungteen.opentd.common.entity.ai;

import hungteen.opentd.common.codec.AttackGoalSetting;
import hungteen.opentd.common.codec.LaserGoalSetting;
import hungteen.opentd.common.entity.TowerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.ElderGuardian;

import java.util.EnumSet;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/15 19:23
 */
public class TowerLaserAttackGoal extends HTGoal{

    private final TowerEntity towerEntity;
    private int attackTime;

    public TowerLaserAttackGoal(TowerEntity towerEntity) {
        this.towerEntity = towerEntity;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.towerEntity.getTarget();
        return setting() != null && livingentity != null && livingentity.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return setting() != null &&  this.towerEntity.getTarget() != null;
    }

    @Override
    public void start() {
        this.attackTime = -1;
        LivingEntity livingentity = this.towerEntity.getTarget();
        if (livingentity != null) {
            this.towerEntity.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
        }

        this.towerEntity.hasImpulse = true;
    }

    @Override
    public void stop() {
        this.towerEntity.setActiveAttackTarget(0);
        this.towerEntity.setTarget(null);
    }

    @Override
    public void tick() {
        LivingEntity livingentity = this.towerEntity.getTarget();
        if (livingentity.level instanceof ServerLevel serverLevel && livingentity != null) {
            this.towerEntity.getLookControl().setLookAt(livingentity, 90.0F, 90.0F);
            if (!this.towerEntity.hasLineOfSight(livingentity)) {
                this.towerEntity.setTarget(null);
            } else {
                ++ this.attackTime;
                if (this.attackTime == 0) {
                    this.towerEntity.setActiveAttackTarget(livingentity.getId());
                    if (!this.towerEntity.isSilent()) {
                        this.towerEntity.level.broadcastEntityEvent(this.towerEntity, (byte)21);
                    }
                } else {
                    if(this.attackTime % this.setting().effectInterval() == 0){
                        this.setting().continueEffect().effectTo(serverLevel, towerEntity, livingentity, livingentity.blockPosition());
                    }
                    if (this.attackTime >= this.setting().duration()) {
                        this.setting().finalEffect().effectTo(serverLevel, towerEntity, livingentity, livingentity.blockPosition());
                        this.towerEntity.setTarget(null);
                    }
                }

                super.tick();
            }
        }
    }

    private LaserGoalSetting setting(){
        return this.towerEntity.getLaserSetting();
    }


}

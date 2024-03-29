package hungteen.opentd.common.codec;

import hungteen.htlib.util.helper.CodecHelper;
import hungteen.opentd.api.interfaces.IEffectComponent;
import hungteen.opentd.api.interfaces.ITowerComponent;
import hungteen.opentd.common.entity.PlantEntity;
import hungteen.opentd.common.entity.TowerEntity;
import hungteen.opentd.common.impl.tower.OTDTowerComponents;
import hungteen.opentd.common.item.SummonTowerItem;
import hungteen.opentd.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-05-10 22:09
 **/
public abstract class TowerComponent implements ITowerComponent {

    private final List<TargetSetting> targetSettings;
    private final Optional<MovementSetting> movementSetting;
    private final Optional<ShootGoalSetting> shootGoalSetting;
    private final Optional<GenGoalSetting> genGoalSetting;
    private final Optional<AttackGoalSetting> attackGoalSetting;
    private final Optional<LaserGoalSetting> laserGoalSetting;
    private final Optional<CloseInstantEffectSetting> instantEffectSetting;
    private final List<ConstantAffectSetting> constantAffectSettings;
    private final Optional<Holder<IEffectComponent>> hurtEffect;
    private final Optional<Holder<IEffectComponent>> dieEffect;
    private final Optional<BossBarSetting> bossBarSetting;
    private final Optional<FollowGoalSetting> followGoalSetting;

    public TowerComponent(List<TargetSetting> targetSettings, Optional<MovementSetting> movementSetting, Optional<ShootGoalSetting> shootGoalSetting, Optional<GenGoalSetting> genGoalSetting, Optional<AttackGoalSetting> attackGoalSetting, Optional<LaserGoalSetting> laserGoalSetting, Optional<CloseInstantEffectSetting> instantEffectSetting, List<ConstantAffectSetting> constantAffectSettings, Optional<Holder<IEffectComponent>> hurtEffect, Optional<Holder<IEffectComponent>> dieEffect, Optional<BossBarSetting> bossBarSetting, Optional<FollowGoalSetting> followGoalSetting) {
        this.targetSettings = targetSettings;
        this.movementSetting = movementSetting;
        this.shootGoalSetting = shootGoalSetting;
        this.genGoalSetting = genGoalSetting;
        this.attackGoalSetting = attackGoalSetting;
        this.laserGoalSetting = laserGoalSetting;
        this.instantEffectSetting = instantEffectSetting;
        this.constantAffectSettings = constantAffectSettings;
        this.hurtEffect = hurtEffect;
        this.dieEffect = dieEffect;
        this.bossBarSetting = bossBarSetting;
        this.followGoalSetting = followGoalSetting;
    }

    @Nullable
    @Override
    public Entity createEntity(ServerLevel level, Player player, ItemStack stack, BlockPos pos) {
        final ItemStack itemStack = stack.copy();
        return SummonTowerItem.getTowerSetting(level, stack).map(towerComponent -> {
            CodecHelper.encodeNbt(OTDTowerComponents.getDirectCodec(), towerComponent)
                    .resultOrPartial(msg -> Util.error("TowerComponent encode error : " + msg))
                    .ifPresent(tag -> {
                        itemStack.getOrCreateTag().put(TowerEntity.TOWER_SETTING, tag);
                    });
            itemStack.getOrCreateTag().put(SummonTowerItem.ENTITY_TAG, towerComponent.getExtraNBT());
            itemStack.getOrCreateTag().putFloat(PlantEntity.YROT, player.getYRot());
            return getEntityType().spawn(level, itemStack, player, pos, MobSpawnType.SPAWN_EGG, false, false);
        }).orElse(null);
    }

    public abstract EntityType<? extends Entity> getEntityType();

    public abstract TowerSetting towerSetting();

    public List<TargetSetting> targetSettings() {
        return targetSettings;
    }

    public Optional<MovementSetting> movementSetting() {
        return movementSetting;
    }

    public Optional<ShootGoalSetting> shootGoalSetting() {
        return shootGoalSetting;
    }

    public Optional<GenGoalSetting> genGoalSetting() {
        return genGoalSetting;
    }

    public Optional<AttackGoalSetting> attackGoalSetting() {
        return attackGoalSetting;
    }

    public Optional<LaserGoalSetting> laserGoalSetting() {
        return laserGoalSetting;
    }

    public Optional<CloseInstantEffectSetting> instantEffectSetting() {
        return instantEffectSetting;
    }

    public List<ConstantAffectSetting> constantAffectSettings() {
        return constantAffectSettings;
    }

    public Optional<Holder<IEffectComponent>> hurtEffect() {
        return hurtEffect;
    }

    public Optional<Holder<IEffectComponent>> dieEffect() {
        return dieEffect;
    }

    public Optional<BossBarSetting> bossBarSetting(){
        return bossBarSetting;
    }

    public Optional<FollowGoalSetting> followGoalSetting(){
        return followGoalSetting;
    }
}

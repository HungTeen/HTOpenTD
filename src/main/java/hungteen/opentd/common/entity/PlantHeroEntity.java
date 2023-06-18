package hungteen.opentd.common.entity;

import hungteen.opentd.common.codec.RenderSetting;
import hungteen.opentd.common.codec.TowerComponent;
import hungteen.opentd.common.impl.tower.PVZPlantComponent;
import hungteen.opentd.common.impl.tower.PlantHeroComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-05-10 21:46
 **/
public class PlantHeroEntity extends TowerEntity {

    private PlantHeroComponent component;

    public PlantHeroEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TowerEntity.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50D)
                .add(Attributes.ATTACK_DAMAGE, 4D)
                .add(Attributes.ATTACK_KNOCKBACK, 0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0D)
                .add(Attributes.FOLLOW_RANGE, 40D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D);
    }

    @Override
    public PlantHeroComponent getComponent() {
        if (component == null || this.componentDirty) {
            this.parseComponent(PlantHeroComponent.CODEC, t -> this.component = t);
            this.componentDirty = false;
        }
        return component;
    }

    @Override
    public boolean sameTeamWithOwner() {
        return getComponent() != null && getComponent().heroSetting().sameTeamWithOwner();
    }

    @Override
    public RenderSetting getRenderSetting() {
        return getComponent() != null ? getComponent().heroSetting().renderSetting() : RenderSetting.DEFAULT;
    }

}

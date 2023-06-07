package hungteen.opentd.client.model.entity;

import hungteen.opentd.common.entity.IOTDEntity;
import hungteen.opentd.common.entity.TowerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/6/7 14:10
 */
public abstract class OTDEntityModel<T extends Entity & IOTDEntity> extends AnimatedGeoModel<T> {

    @Override
    public ResourceLocation getModelResource(T towerEntity) {
        return towerEntity.getTowerModel().orElse(towerEntity.getRenderSetting().modelLocation());
    }

    @Override
    public ResourceLocation getTextureResource(T towerEntity) {
        return towerEntity.getTowerTexture().orElse(towerEntity.getRenderSetting().textureLocation());
    }

    @Override
    public ResourceLocation getAnimationResource(T towerEntity) {
        return towerEntity.getTowerAnimation().orElse(towerEntity.getRenderSetting().animationLocation());
    }
}

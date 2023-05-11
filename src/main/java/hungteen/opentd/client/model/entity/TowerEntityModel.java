package hungteen.opentd.client.model.entity;

import hungteen.opentd.common.entity.PlantEntity;
import hungteen.opentd.common.entity.TowerEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/11 15:59
 */
public class TowerEntityModel<T extends TowerEntity> extends AnimatedGeoModel<T> {

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
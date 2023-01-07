package hungteen.opentd.client.model.entity;

import hungteen.opentd.common.entity.PlantEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 10:53
 **/
public class PlantEntityModel extends AnimatedGeoModel<PlantEntity> {

    @Override
    public ResourceLocation getModelResource(PlantEntity plantEntity) {
        return plantEntity.getComponent().plantSetting().renderSetting().modelLocation();
    }

    @Override
    public ResourceLocation getTextureResource(PlantEntity plantEntity) {
        return plantEntity.getComponent().plantSetting().renderSetting().textureLocation();
    }

    @Override
    public ResourceLocation getAnimationResource(PlantEntity plantEntity) {
        return plantEntity.getComponent().plantSetting().renderSetting().animationLocation();
    }
}

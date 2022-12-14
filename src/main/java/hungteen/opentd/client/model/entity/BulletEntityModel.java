package hungteen.opentd.client.model.entity;

import hungteen.opentd.common.entity.BulletEntity;
import hungteen.opentd.common.entity.PlantEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-27 13:13
 **/
public class BulletEntityModel extends AnimatedGeoModel<BulletEntity> {

    @Override
    public ResourceLocation getModelResource(BulletEntity bulletEntity) {
        return bulletEntity.getSettings().renderSettings().modelLocation();
    }

    @Override
    public ResourceLocation getTextureResource(BulletEntity bulletEntity) {
        return bulletEntity.getSettings().renderSettings().textureLocation();
    }

    @Override
    public ResourceLocation getAnimationResource(BulletEntity bulletEntity) {
        return bulletEntity.getSettings().renderSettings().animationLocation();
    }
}

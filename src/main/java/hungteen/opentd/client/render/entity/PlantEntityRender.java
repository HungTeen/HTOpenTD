package hungteen.opentd.client.render.entity;

import hungteen.htlib.util.helper.MathHelper;
import hungteen.opentd.client.model.entity.PlantEntityModel;
import hungteen.opentd.common.entity.PlantEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 11:25
 **/
public class PlantEntityRender extends TowerEntityRender<PlantEntity> {

    public PlantEntityRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PlantEntityModel());
    }

    @Override
    protected float getScale(PlantEntity animatable) {
        if (animatable.getComponent() != null) {
            double tmp;
            if(animatable.oldAge != animatable.getAge()){
                final float oldScale = animatable.getGrowSetting().scales().get(animatable.oldAge);
                final float newScale = animatable.getGrowSetting().scales().get(animatable.getAge());
                tmp = MathHelper.smooth(oldScale, newScale, PlantEntity.GROW_ANIM_CD - animatable.growAnimTick, PlantEntity.GROW_ANIM_CD);
            } else{
                tmp = animatable.getGrowSetting().scales().get(animatable.getAge());
            }
            return  (float) (tmp * animatable.getComponent().plantSetting().renderSetting().scale());
        }
        return 1F;
    }

}

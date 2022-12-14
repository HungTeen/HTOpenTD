package hungteen.opentd.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hungteen.opentd.client.model.entity.PlantEntityModel;
import hungteen.opentd.common.entity.PlantEntity;
import hungteen.opentd.util.MathUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-17 11:25
 **/
public class PlantEntityRender extends GeoEntityRenderer<PlantEntity> {

    public PlantEntityRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PlantEntityModel());
    }

    @Override
    protected void applyRotations(PlantEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        if (animatable.getComponent() != null) {
            double tmp;
            if(animatable.oldAge != animatable.getAge()){
                final float oldScale = animatable.getGrowSettings().scales().get(animatable.oldAge);
                final float newScale = animatable.getGrowSettings().scales().get(animatable.getAge());
                tmp = MathUtil.smooth(oldScale, newScale, PlantEntity.GROW_ANIM_CD - animatable.growAnimTick, PlantEntity.GROW_ANIM_CD);
            } else{
                tmp = animatable.getGrowSettings().scales().get(animatable.getAge());
            }
            final float scale = (float) (tmp * animatable.getComponent().plantSetting().renderSetting().scale());
            poseStack.scale(scale, scale, scale);
        }
    }

    @Override
    public void render(GeoModel model, PlantEntity animatable, float partialTick, RenderType type, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (animatable.getComponent() != null) {
            super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    @Override
    public void render(PlantEntity animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (animatable.getComponent() != null) {
            super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        }
    }
}

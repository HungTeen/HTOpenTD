package hungteen.opentd.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hungteen.opentd.client.model.entity.BulletEntityModel;
import hungteen.opentd.common.entity.BulletEntity;
import hungteen.opentd.common.entity.PlantEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoProjectilesRenderer;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-27 13:11
 **/
public class BulletEntityRender extends GeoProjectilesRenderer<BulletEntity> {

    public BulletEntityRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BulletEntityModel());
    }

    @Override
    public void render(GeoModel model, BulletEntity animatable, float partialTick, RenderType type, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (animatable.getSettings() != null) {
            poseStack.pushPose();
            final float scale = animatable.getSettings().renderSettings().scale();
            poseStack.scale(scale, scale, scale);
            super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }
    }

    @Override
    public void render(BulletEntity animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (animatable.getSettings() != null) {
            super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        }
    }
}

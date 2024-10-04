package hungteen.opentd.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import hungteen.opentd.client.model.entity.BulletEntityModel;
import hungteen.opentd.common.entity.BulletEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
        if (animatable.bulletSetting() != null) {
            poseStack.pushPose();
            final float scale = animatable.bulletSetting().renderSettings().scale();
            poseStack.scale(scale, scale, scale);
            super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            poseStack.popPose();
        }
    }

    @Override
    public void render(BulletEntity animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (animatable.bulletSetting() != null) {
            poseStack.pushPose();
            poseStack.mulPose(Vector3f.YP.rotationDegrees(- Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) - 90.0F));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(- Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(- Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) + 180.0F));
            super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
    }

    @Override
    public RenderType getRenderType(BulletEntity animatable, float partialTick, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, ResourceLocation texture) {
        if (animatable.getRenderSetting() != null && animatable.getRenderSetting().translucent()) {
            return RenderType.entityTranslucent(getTextureLocation(animatable));
        }
        return super.getRenderType(animatable, partialTick, poseStack, bufferSource, buffer, packedLight, texture);
    }
}

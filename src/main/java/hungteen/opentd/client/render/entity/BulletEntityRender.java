package hungteen.opentd.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import hungteen.opentd.client.model.entity.BulletEntityModel;
import hungteen.opentd.common.entity.BulletEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-27 13:11
 **/
public class BulletEntityRender extends GeoEntityRenderer<BulletEntity> {

    public BulletEntityRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BulletEntityModel());
    }

    @Override
    public void render(BulletEntity animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (animatable.bulletSetting() != null) {
            poseStack.pushPose();
            final float scale = animatable.bulletSetting().renderSettings().scale();
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Axis.YP.rotationDegrees(- Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) - 90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(- Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
            poseStack.mulPose(Axis.YP.rotationDegrees(- Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot()) + 180.0F));
            super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            poseStack.popPose();
        }
    }
}

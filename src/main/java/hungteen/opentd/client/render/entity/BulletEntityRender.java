package hungteen.opentd.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import hungteen.opentd.client.model.entity.BulletEntityModel;
import hungteen.opentd.common.entity.BulletEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
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
    protected void applyRotations(BulletEntity animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
//        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        if (animatable.getComponent() != null) {
            final float scale = getScale(animatable);
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Axis.YP.rotationDegrees(- Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot())));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
        }
    }

    protected float getScale(BulletEntity animatable){
        return animatable.getRenderSetting() == null ? 1F : animatable.getRenderSetting().scale();
    }

    @Override
    public void render(BulletEntity animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (animatable.bulletSetting() != null) {
            super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        }
    }

    @Override
    public RenderType getRenderType(BulletEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        if (animatable.getRenderSetting() != null && animatable.getRenderSetting().translucent()) {
            return RenderType.entityTranslucent(getTextureLocation(animatable));
        }
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}

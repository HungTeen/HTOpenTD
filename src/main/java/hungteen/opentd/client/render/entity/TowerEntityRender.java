package hungteen.opentd.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import hungteen.opentd.common.codec.LaserGoalSetting;
import hungteen.opentd.common.entity.TowerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/11 15:54
 */
public class TowerEntityRender<T extends TowerEntity> extends GeoEntityRenderer<T> {

    private static final ResourceLocation GUARDIAN_BEAM_LOCATION = new ResourceLocation("textures/entity/guardian_beam.png");
    private static final RenderType BEAM_RENDER_TYPE = RenderType.entityCutoutNoCull(GUARDIAN_BEAM_LOCATION);

    public TowerEntityRender(EntityRendererProvider.Context renderManager, GeoModel<T> modelProvider) {
        super(renderManager, modelProvider);
    }

    @Override
    protected void applyRotations(T animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        if (animatable.getComponent() != null) {
            final float scale = getScale(animatable);
            poseStack.scale(scale, scale, scale);
        }
    }

    protected float getScale(T animatable){
        return animatable.getRenderSetting() == null ? 1F : animatable.getRenderSetting().scale();
    }

    public boolean shouldRender(T p_114836_, Frustum p_114837_, double p_114838_, double p_114839_, double p_114840_) {
        if (super.shouldRender(p_114836_, p_114837_, p_114838_, p_114839_, p_114840_)) {
            return true;
        } else {
            if (p_114836_.hasActiveAttackTarget()) {
                LivingEntity livingentity = p_114836_.getActiveAttackTarget();
                if (livingentity != null) {
                    Vec3 vec3 = this.getPosition(livingentity, (double)livingentity.getBbHeight() * 0.5D, 1.0F);
                    Vec3 vec31 = this.getPosition(p_114836_, p_114836_.getEyeHeight(), 1.0F);
                    return p_114837_.isVisible(new AABB(vec31.x, vec31.y, vec31.z, vec3.x, vec3.y, vec3.z));
                }
            }

            return false;
        }
    }

    @Override
    public void render(T animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (animatable.getComponent() != null) {
            super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
            this.renderLaser(animatable, partialTick, poseStack, bufferSource);
        }
    }

    @Override
    public RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        if (animatable.getRenderSetting() != null && animatable.getRenderSetting().translucent()) {
            return RenderType.entityTranslucent(getTextureLocation(animatable));
        }
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    private void renderLaser(T towerEntity, float partialTick, PoseStack stack, MultiBufferSource bufferSource){
        LivingEntity livingentity = towerEntity.getActiveAttackTarget();
        if (livingentity != null && towerEntity.getLaserSetting() != null) {
            float f = towerEntity.getAttackAnimationScale(towerEntity.getLaserSetting(), partialTick);
            float f1 = (float)towerEntity.level().getGameTime() + partialTick;
            float f2 = f1 * 0.5F % 1.0F;
            float f3 = towerEntity.getEyeHeight();
            stack.pushPose();
            stack.translate(0.0D, f3, 0.0D);
            Vec3 vec3 = this.getPosition(livingentity, livingentity.getEyeHeight(), partialTick);
            Vec3 vec31 = this.getPosition(towerEntity, f3, partialTick);
            Vec3 vec32 = vec3.subtract(vec31);
            float f4 = (float) towerEntity.getLaserSetting().laserDistance() + 1;
            vec32 = vec32.normalize();
            float f5 = (float)Math.acos(vec32.y);
            float f6 = (float)Math.atan2(vec32.z, vec32.x);
            stack.mulPose(Axis.YP.rotationDegrees((((float)Math.PI / 2F) - f6) * (180F / (float)Math.PI)));
            stack.mulPose(Axis.XP.rotationDegrees(f5 * (180F / (float)Math.PI)));
            int i = 1;
            float f7 = f1 * 0.05F * -1.5F;
            float f8 = f * f;
            int j = 64 + (int)(f8 * 191.0F);
            int k = 32 + (int)(f8 * 191.0F);
            int l = 128 - (int)(f8 * 64.0F);
            // 固定激光颜色。
            if(towerEntity.getLaserSetting().laserColor().isPresent()){
                int color = towerEntity.getLaserSetting().laserColor().get();
                j = (color >> 16) & 0xFF;
                k = (color >> 8) & 0xFF;
                l = color & 0xFF;
            }
            float f9 = 0.2F;
            float f10 = 0.282F;
            float f11 = Mth.cos(f7 + 2.3561945F) * 0.282F;
            float f12 = Mth.sin(f7 + 2.3561945F) * 0.282F;
            float f13 = Mth.cos(f7 + ((float)Math.PI / 4F)) * 0.282F;
            float f14 = Mth.sin(f7 + ((float)Math.PI / 4F)) * 0.282F;
            float f15 = Mth.cos(f7 + 3.926991F) * 0.282F;
            float f16 = Mth.sin(f7 + 3.926991F) * 0.282F;
            float f17 = Mth.cos(f7 + 5.4977875F) * 0.282F;
            float f18 = Mth.sin(f7 + 5.4977875F) * 0.282F;
            float f19 = Mth.cos(f7 + (float)Math.PI) * 0.2F;
            float f20 = Mth.sin(f7 + (float)Math.PI) * 0.2F;
            float f21 = Mth.cos(f7 + 0.0F) * 0.2F;
            float f22 = Mth.sin(f7 + 0.0F) * 0.2F;
            float f23 = Mth.cos(f7 + ((float)Math.PI / 2F)) * 0.2F;
            float f24 = Mth.sin(f7 + ((float)Math.PI / 2F)) * 0.2F;
            float f25 = Mth.cos(f7 + ((float)Math.PI * 1.5F)) * 0.2F;
            float f26 = Mth.sin(f7 + ((float)Math.PI * 1.5F)) * 0.2F;
            float f27 = 0.0F;
            float f28 = 0.4999F;
            float f29 = -1.0F + f2;
            float f30 = f4 * 2.5F + f29;
            VertexConsumer vertexconsumer = bufferSource.getBuffer(getBeamRenderType(towerEntity.getLaserSetting()));
            PoseStack.Pose posestack$pose = stack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            vertex(vertexconsumer, matrix4f, matrix3f, f19, f4, f20, j, k, l, 0.4999F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f19, 0.0F, f20, j, k, l, 0.4999F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f21, 0.0F, f22, j, k, l, 0.0F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f21, f4, f22, j, k, l, 0.0F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f23, f4, f24, j, k, l, 0.4999F, f30);
            vertex(vertexconsumer, matrix4f, matrix3f, f23, 0.0F, f24, j, k, l, 0.4999F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f25, 0.0F, f26, j, k, l, 0.0F, f29);
            vertex(vertexconsumer, matrix4f, matrix3f, f25, f4, f26, j, k, l, 0.0F, f30);
            float f31 = 0.0F;
            if (towerEntity.tickCount % 2 == 0) {
                f31 = 0.5F;
            }

            vertex(vertexconsumer, matrix4f, matrix3f, f11, f4, f12, j, k, l, 0.5F, f31 + 0.5F);
            vertex(vertexconsumer, matrix4f, matrix3f, f13, f4, f14, j, k, l, 1.0F, f31 + 0.5F);
            vertex(vertexconsumer, matrix4f, matrix3f, f17, f4, f18, j, k, l, 1.0F, f31);
            vertex(vertexconsumer, matrix4f, matrix3f, f15, f4, f16, j, k, l, 0.5F, f31);
            stack.popPose();
        }
    }

    private static void vertex(VertexConsumer p_114842_, Matrix4f p_114843_, Matrix3f p_114844_, float p_114845_, float p_114846_, float p_114847_, int p_114848_, int p_114849_, int p_114850_, float p_114851_, float p_114852_) {
        p_114842_.vertex(p_114843_, p_114845_, p_114846_, p_114847_).color(p_114848_, p_114849_, p_114850_, 255).uv(p_114851_, p_114852_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(p_114844_, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private Vec3 getPosition(LivingEntity entity, double p_114804_, float p_114805_) {
        double d0 = Mth.lerp(p_114805_, entity.xOld, entity.getX());
        double d1 = Mth.lerp(p_114805_, entity.yOld, entity.getY()) + p_114804_;
        double d2 = Mth.lerp(p_114805_, entity.zOld, entity.getZ());
        return new Vec3(d0, d1, d2);
    }

    private RenderType getBeamRenderType(LaserGoalSetting setting){
        return setting.laserTexture().map(RenderType::entityTranslucent).orElse(BEAM_RENDER_TYPE);
    }
}

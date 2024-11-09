package hungteen.opentd.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import software.bernie.geckolib3.util.AnimationUtils;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.IRenderCycle;
import software.bernie.geckolib3.util.RenderUtils;

import javax.annotation.Nonnull;
import java.util.Collections;

/**
 * @program: HTOpenTD
 * @author: PangTeen
 * @create: 2024/11/9 15:48
 **/
public class HTGeoProjectilesRenderer<T extends Entity & IAnimatable> extends EntityRenderer<T> implements IGeoRenderer<T> {

    protected final AnimatedGeoModel<T> modelProvider;
    protected float widthScale = 1.0F;
    protected float heightScale = 1.0F;
    protected Matrix4f dispatchedMat = new Matrix4f();
    protected Matrix4f renderEarlyMat = new Matrix4f();
    protected T animatable;
    private IRenderCycle currentModelRenderCycle;
    protected MultiBufferSource rtb;

    public HTGeoProjectilesRenderer(EntityRendererProvider.Context renderManager, AnimatedGeoModel<T> modelProvider) {
        super(renderManager);
        this.currentModelRenderCycle = EModelRenderCycle.INITIAL;
        this.rtb = null;
        this.modelProvider = modelProvider;
    }

    @Override
    public void render(T animatable, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelResource(animatable));
        this.dispatchedMat = poseStack.last().pose().copy();
        this.setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
        poseStack.pushPose();
        poseStack.mulPose(Vector3f.YP.rotationDegrees(- Mth.lerp(partialTick, animatable.yRotO, animatable.getYRot())));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(Mth.lerp(partialTick, animatable.xRotO, animatable.getXRot())));
        AnimationEvent<T> predicate = new AnimationEvent<>(animatable, 0.0F, 0.0F, partialTick, false, Collections.singletonList(new EntityModelData()));
        this.modelProvider.setCustomAnimations(animatable, this.getInstanceId(animatable), predicate);
        RenderSystem.setShaderTexture(0, this.getTextureLocation(animatable));
        Color renderColor = this.getRenderColor(animatable, partialTick, poseStack, bufferSource, null, packedLight);
        RenderType renderType = this.getRenderType(animatable, partialTick, poseStack, bufferSource, null, packedLight, this.getTextureLocation(animatable));
        if (!animatable.isInvisibleTo(Minecraft.getInstance().player)) {
            this.render(model, animatable, partialTick, renderType, poseStack, bufferSource, null, packedLight, getPackedOverlay(animatable, 0.0F), (float)renderColor.getRed() / 255.0F, (float)renderColor.getGreen() / 255.0F, (float)renderColor.getBlue() / 255.0F, (float)renderColor.getAlpha() / 255.0F);
        }

        poseStack.popPose();
        super.render(animatable, yaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public void renderEarly(T animatable, PoseStack poseStack, float partialTick, MultiBufferSource bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.renderEarlyMat = poseStack.last().pose().copy();
        this.animatable = animatable;
        IGeoRenderer.super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (bone.isTrackingXform()) {
            Matrix4f poseState = poseStack.last().pose().copy();
            Matrix4f localMatrix = RenderUtils.invertAndMultiplyMatrices(poseState, this.dispatchedMat);
            bone.setModelSpaceXform(RenderUtils.invertAndMultiplyMatrices(poseState, this.renderEarlyMat));
            localMatrix.translate(new Vector3f(this.getRenderOffset(this.animatable, 1.0F)));
            bone.setLocalSpaceXform(localMatrix);
            Matrix4f worldState = localMatrix.copy();
            worldState.translate(new Vector3f(this.animatable.position()));
            bone.setWorldSpaceXform(worldState);
        }

        IGeoRenderer.super.renderRecursively(bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public static int getPackedOverlay(Entity entity, float uIn) {
        return OverlayTexture.pack(OverlayTexture.u(uIn), OverlayTexture.v(false));
    }

    @Override
    public GeoModelProvider<T> getGeoModelProvider() {
        return this.modelProvider;
    }

    @Nonnull
    @Override
    public IRenderCycle getCurrentModelRenderCycle() {
        return this.currentModelRenderCycle;
    }

    @Override
    public void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
        this.currentModelRenderCycle = currentModelRenderCycle;
    }

    @Override
    public float getWidthScale(T animatable) {
        return this.widthScale;
    }

    @Override
    public float getHeightScale(T entity) {
        return this.heightScale;
    }

    @Override
    public ResourceLocation getTextureLocation(T animatable) {
        return this.modelProvider.getTextureResource(animatable);
    }

    /** @deprecated */
    @Deprecated(
            forRemoval = true
    )
    public Integer getUniqueID(T animatable) {
        return this.getInstanceId(animatable);
    }

    @Override
    public int getInstanceId(T animatable) {
        return animatable.getUUID().hashCode();
    }

    @Override
    public void setCurrentRTB(MultiBufferSource bufferSource) {
        this.rtb = bufferSource;
    }

    @Override
    public MultiBufferSource getCurrentRTB() {
        return this.rtb;
    }

    static {
        AnimationController.addModelFetcher((animatable) -> {
            IAnimatableModel var10000;
            if (animatable instanceof Entity entity) {
                var10000 = (IAnimatableModel) AnimationUtils.getGeoModelForEntity(entity);
            } else {
                var10000 = null;
            }

            return var10000;
        });
    }
}

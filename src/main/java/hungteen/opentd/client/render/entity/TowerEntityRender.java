package hungteen.opentd.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hungteen.opentd.common.entity.PlantEntity;
import hungteen.opentd.common.entity.TowerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

/**
 * @author PangTeen
 * @program HTOpenTD
 * @data 2023/5/11 15:54
 */
public class TowerEntityRender<T extends TowerEntity> extends GeoEntityRenderer<T> {

    public TowerEntityRender(EntityRendererProvider.Context renderManager, AnimatedGeoModel<T> modelProvider) {
        super(renderManager, modelProvider);
    }

    @Override
    public void render(GeoModel model, T animatable, float partialTick, RenderType type, PoseStack poseStack, @Nullable MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (animatable.getComponent() != null) {
            super.render(model, animatable, partialTick, type, poseStack, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    @Override
    public void render(T animatable, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (animatable.getComponent() != null) {
            super.render(animatable, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        }
    }
}

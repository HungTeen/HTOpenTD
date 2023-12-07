package hungteen.opentd.client.model.item;

import hungteen.opentd.common.codec.ItemSetting;
import hungteen.opentd.common.item.SummonTowerItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2022-12-30 16:45
 **/
public class SummonTowerModel implements BakedModel {
    private final BakedModel defaultModel;
    private final ItemOverrides itemHandler;

    public SummonTowerModel(BakedModel model, ModelBakery loader) {
        this.defaultModel = model;
        BlockModel missing = (BlockModel) loader.getModel(ModelBakery.MISSING_MODEL_LOCATION);

        this.itemHandler = new ItemOverrides(new ModelBaker() {

            @Override
            public @org.jetbrains.annotations.Nullable BakedModel bake(ResourceLocation location, ModelState state, Function<Material, TextureAtlasSprite> sprites) {
                return null;
            }

            @Override
            public Function<Material, TextureAtlasSprite> getModelTextureGetter() {
                return null;
            }

            @Override
            public UnbakedModel getModel(ResourceLocation p_252194_) {
                return null;
            }

            @org.jetbrains.annotations.Nullable
            @Override
            public BakedModel bake(ResourceLocation p_250776_, ModelState p_251280_) {
                return null;
            }

        }, missing, Collections.emptyList()) {
            @Override
            public BakedModel resolve(@NotNull BakedModel original, @NotNull ItemStack stack,
                                      @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
                ItemSetting itemSettings = SummonTowerItem.getItemSetting(stack);
                ModelResourceLocation modelPath = new ModelResourceLocation(itemSettings.model(), "inventory");
                return Minecraft.getInstance().getModelManager().getModel(modelPath);
            }
        };
    }

    @NotNull
    @Override
    public ItemOverrides getOverrides() {
        return itemHandler;
    }

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
        return defaultModel.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return defaultModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return defaultModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return defaultModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return defaultModel.isCustomRenderer();
    }

    @NotNull
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return defaultModel.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return defaultModel.getTransforms();
    }
}

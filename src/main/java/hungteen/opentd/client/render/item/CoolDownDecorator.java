package hungteen.opentd.client.render.item;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import hungteen.opentd.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;

/**
 * @program: HTOpenTD
 * @author: HungTeen
 * @create: 2023-01-08 13:13
 **/
public class CoolDownDecorator implements IItemDecorator {
    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        LocalPlayer localplayer = Minecraft.getInstance().player;
        double percent = localplayer == null ? 0.0F : PlayerUtil.getCDPercent(localplayer, stack);
        //TODO 冷却可能出问题
        if (percent > 0) {
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder builder = tesselator.getBuilder();
            this.fillRect(builder, xOffset, yOffset + Mth.floor(16.0F * (1.0F - percent)), 16, Mth.ceil(16.0F * percent), 255, 255, 255, 127);
            RenderSystem.enableDepthTest();
        }
        return true;
    }

    private void fillRect(BufferBuilder builder, int x, int y, int p_115156_, int p_115157_, int p_115158_, int p_115159_, int p_115160_, int p_115161_) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        builder.vertex(x, y, 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
        builder.vertex(x, (y + p_115157_), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
        builder.vertex(x + p_115156_, (y + p_115157_), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
        builder.vertex(x + p_115156_, (y), 0.0D).color(p_115158_, p_115159_, p_115160_, p_115161_).endVertex();
        BufferUploader.drawWithShader(builder.end());
    }

}

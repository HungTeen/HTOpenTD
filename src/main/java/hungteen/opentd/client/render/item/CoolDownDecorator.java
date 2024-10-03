package hungteen.opentd.client.render.item;

import com.mojang.blaze3d.vertex.VertexConsumer;
import hungteen.opentd.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;
import org.joml.Matrix4f;

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
        if (percent > 0) {
            guiGraphics.fill(RenderType.guiOverlay(), xOffset, yOffset + Mth.floor(16.0F * (1.0F - percent)), xOffset + 16, yOffset + 16, Integer.MAX_VALUE);
//            fillRect(guiGraphics, RenderType.guiOverlay(), xOffset, yOffset + Mth.floor(16.0F * (1.0F - percent)), xOffset + 16, yOffset + 16, 0, 255, 255, 255, 255);
        }
        return true;
    }

    public static void fillRect(GuiGraphics guiGraphics, RenderType renderType, int x, int y, int tx, int ty, int z, int red, int green, int blue, int alpha) {
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        if (x < tx) {
            int i = x;
            x = tx;
            tx = i;
        }

        if (y < ty) {
            int j = y;
            y = ty;
            ty = j;
        }

        float f3 = (float) alpha / 255.0F;
        float f = (float) red / 255.0F;
        float f1 = (float) green / 255.0F;
        float f2 = (float) blue / 255.0F;
        VertexConsumer vertexconsumer = guiGraphics.bufferSource().getBuffer(renderType);
        vertexconsumer.vertex(matrix4f, (float)x, (float)y, (float)z).color(f, f1, f2, f3).endVertex();
        vertexconsumer.vertex(matrix4f, (float)x, (float)ty, (float)z).color(f, f1, f2, f3).endVertex();
        vertexconsumer.vertex(matrix4f, (float)tx, (float)ty, (float)z).color(f, f1, f2, f3).endVertex();
        vertexconsumer.vertex(matrix4f, (float)tx, (float)y, (float)z).color(f, f1, f2, f3).endVertex();
    }

}

package fr.snyker.pixor.client.gui.button;

import fr.snyker.pixor.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiEditButton extends GuiButton {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MODID, "textures/gui/edit.png");

    public GuiEditButton(int buttonId, int x, int y) {
        super(buttonId, x, y, "");
        width = 48 / 3;
        height = 48 / 3;
    }

    @Override
    protected int getHoverState(boolean mouseOver) {

        int i = 0;

        if(mouseOver)
            i = 1;

        return i;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            mc.getTextureManager().bindTexture(TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            drawScaledCustomSizeModalRect(x, y, 0, i * 48, 48, 48, width, height, 48, 96);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

}

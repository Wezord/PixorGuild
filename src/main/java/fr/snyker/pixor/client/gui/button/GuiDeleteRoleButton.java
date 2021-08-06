package fr.snyker.pixor.client.gui.button;

import fr.snyker.pixor.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiDeleteRoleButton extends GuiButton {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MODID, "textures/gui/trash.png");

    public GuiDeleteRoleButton(int buttonId, int x, int y) {
        super(buttonId, x, y, 20, 20, "");
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            //On dessine le fond du bouton
            super.drawButton(mc,mouseX, mouseY,partialTicks);

            mc.getTextureManager().bindTexture(TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            drawScaledCustomSizeModalRect(x+2, y+2, 0, 0, 32, 32, 16, 16, 32, 32);
        }
    }

}

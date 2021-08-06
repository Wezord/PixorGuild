package fr.snyker.pixor.client.gui;

import fr.snyker.pixor.Main;
import fr.snyker.pixor.guild.GuildRole;
import fr.snyker.pixor.network.server.SPacketChangeRoleMember;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class GuiSelectRoleMember extends GuiScreen {

    private GuiGuild backScreen;
    private int maxLengthFont;
    private int maxLength;

    private int startX, startY;

    public GuiSelectRoleMember(GuiGuild backScreen) {
        this.backScreen = backScreen;
    }

    @Override
    public void initGui() {
        super.initGui();

        for (GuildRole role : backScreen.guild.getRoles()) {
            maxLengthFont = Math.max(fontRenderer.getStringWidth(role.getName()), maxLengthFont);
            maxLength = Math.max(role.getName().length(), maxLength);
        }

        int size = backScreen.guild.getRoles().size();
        int columns = size / 10 + 1;

        int bWidth = (maxLengthFont + 8);
        startX = (width - bWidth) / 2 - 10 * (columns-1) * bWidth;
        startY = height / 2 - Math.min(size, 10) * 30;

        /*
        Sx = width / 2 - bwidth / 2 - (columns-1 + 10) * bwidth
         */

        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < Math.min(size, 10); j++) {
                GuildRole role = backScreen.guild.getRoles().get(i * 10 + j);
                GuiButton button = new GuiButton(role.getIdRole(),startX + 10 * i, startY + 30 * j, maxLengthFont + 8, 20, role.getName());
                if(role.isOwner())
                    button.enabled = backScreen.guild.getRole(backScreen.viewer.getIdRole()).isOwner();
                addButton(button);
            }
        }

        this.buttonList.add(new GuiButton(-1, this.width / 2 - 100, startY + Math.min(size, 10) * 30 + 20, "Annuler"));

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        String s = "Séléctionner un rôle pour le membre : " + backScreen.edit.getName();
        fontRenderer.drawString(s, width / 2 - fontRenderer.getStringWidth(s) / 2, startY - 16, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        if(keyCode == 1) {
            backScreen.confirmClicked(false, 3);
        }

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        if(button.id < 0) {
            backScreen.confirmClicked(false, 3);
        } else {
            Main.NETWORK.sendToServer(new SPacketChangeRoleMember(backScreen.edit, button.id));
        }
    }
}

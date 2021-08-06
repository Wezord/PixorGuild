package fr.snyker.pixor.client.gui;

import fr.snyker.pixor.Main;
import fr.snyker.pixor.guild.GuildPermission;
import fr.snyker.pixor.guild.GuildRole;
import fr.snyker.pixor.network.server.SPacketUpdateRole;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiRole extends GuiScreen {

    private GuiTextField nameField;
    private GuiCheckBox[] permsField;

    private int startX, startY;

    private final GuiGuild backScreen;
    private final GuildRole role;
    private final boolean create;

    public GuiRole(GuiGuild guiGuild, boolean create, int idRole) {
        this.backScreen = guiGuild;
        if(!create) this.role = guiGuild.guild.getRole(idRole);
        else this.role = new GuildRole(-999, "Aucun nom");
        this.create = create;
    }

    @Override
    public void initGui() {
        super.initGui();

        if(role == null) {
            mc.displayGuiScreen(null);
            return;
        }

        Keyboard.enableRepeatEvents(true);
        permsField = new GuiCheckBox[GuildPermission.values().length];

        startX = width / 2 - 100;
        startY = height / 2 - 100;

        nameField = new GuiTextField(0, fontRenderer, startX, startY, 200, 20);
        nameField.setFocused(true);
        nameField.setText(role.getName());
        nameField.setMaxStringLength(16);

        int buttonY = startY;

        for (int i = 0; i < permsField.length; i++) {
            GuildPermission permission = GuildPermission.values()[i];
            permsField[i] = new GuiCheckBox(i + 2, startX + 4, buttonY = startY + 30 + i * 22, permission.getInfo(), role.hasPermission(permission));
            addButton(permsField[i]);
        }

        addButton(new GuiButton(0, startX, buttonY + 22, 200, 20, "Sauvegarder"));
        addButton(new GuiButton(1, startX, buttonY + 44, 200, 20, "Annuler"));

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        this.drawDefaultBackground();

        String s = "Créer un rôle : ";
        if(!create) s = "Editer le rôle : ";

        fontRenderer.drawString(s, width / 2 - fontRenderer.getStringWidth(s) / 2, startY - 12, -1);

        nameField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        this.nameField.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == 28 || keyCode == 156)
        {
            this.actionPerformed(this.buttonList.get(0));
        }

        if(keyCode == 1) {
            backScreen.confirmClicked(false, 1);
        }

    }

    @Override
    public void updateScreen() {
        nameField.updateCursorCounter();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        nameField.mouseClicked(mouseX, mouseY, mouseButton);

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.enabled) {

            if(button.id == 0) {
                role.setName(nameField.getText());
                for (int i = 0; i < permsField.length; i++) {
                    role.setPermission(GuildPermission.values()[i], permsField[i].isChecked());
                }
                Main.NETWORK.sendToServer(new SPacketUpdateRole(Main.GSON.toJson(role)));
            } else if(button.id == 1) {
                backScreen.confirmClicked(false, 1);
            }
        }
    }
}

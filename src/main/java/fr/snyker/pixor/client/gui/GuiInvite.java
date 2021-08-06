package fr.snyker.pixor.client.gui;

import fr.snyker.pixor.Main;
import fr.snyker.pixor.network.server.SPacketInviteMember;
import net.minecraft.client.gui.*;
import net.minecraft.util.TabCompleter;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiInvite extends GuiScreen {

    private GuiScreen backScreen;
    private GuiTextField fieldName;

    public GuiInvite(GuiScreen backScreen) {
        this.backScreen = backScreen;
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + 22, "Envoyer l'invitation"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + 42, "Annuler"));
        this.fieldName = new GuiTextField(0, this.fontRenderer, this.width / 2 - 100, this.height / 2, 200, 20);
        this.fieldName.setFocused(true);
        this.fieldName.setText("");
        this.fieldName.setCanLoseFocus(false);
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        fieldName.updateCursorCounter();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.enabled) {
            switch (button.id) {
                case 0:
                    if(!fieldName.getText().isEmpty()) {
                        //Envoit de l'invitation via le serveur (c'est lui qui gère on oublie pas)
                        Main.NETWORK.sendToServer(new SPacketInviteMember(fieldName.getText()));
                        //On ferme le menu quoi qu'il arrive
                        mc.displayGuiScreen(null);
                        mc.setIngameFocus();
                    } else
                        fieldName.setFocused(true);
                    break;
                case 1:
                    backScreen.confirmClicked(false, 0);
                    break;
            }
        }
    }


    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {

        this.fieldName.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == 28 || keyCode == 156)
        {
            this.actionPerformed(this.buttonList.get(0));
        }

        if(keyCode == 1) {
            backScreen.confirmClicked(false, 0);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        fieldName.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        drawDefaultBackground();

        fontRenderer.drawString("Entrez le nom du joueur : ", width / 2 - 100, height / 2 - 18, -1);

        fieldName.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

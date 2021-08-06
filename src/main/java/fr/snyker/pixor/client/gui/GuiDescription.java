package fr.snyker.pixor.client.gui;

import fr.snyker.pixor.Main;
import fr.snyker.pixor.config.Configuration;
import fr.snyker.pixor.network.server.SPacketModifyDescription;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiDescription extends GuiScreen {

    private GuiScreen backScreen;
    private String description;

    private GuiTextField textField;

    public GuiDescription(GuiScreen backScreen, String description) {
        this.backScreen = backScreen;
        this.description = description;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        textField.updateCursorCounter();
    }

    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + 22, "Valider"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + 42, "Annuler"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 2 + 62, "Reset"));
        this.textField = new GuiTextField(0, this.fontRenderer, this.width / 2 - 100, this.height / 2, 200, 20);
        this.textField.setFocused(true);
        this.textField.setMaxStringLength(256); //Nombre de caractère maximum
        this.textField.setText(this.description);
        this.textField.setCanLoseFocus(false);
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if(button.enabled) {
            switch (button.id) {
                case 0:
                    Main.NETWORK.sendToServer(new SPacketModifyDescription(textField.getText()));
                    break;
                case 1:
                    backScreen.confirmClicked(false, 2);
                    break;
                case 2:
                    textField.setText(Configuration.defaultDescription);
                    break;
            }
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {

        this.textField.textboxKeyTyped(typedChar, keyCode);

        if (keyCode == 28 || keyCode == 156)
        {
            this.actionPerformed(this.buttonList.get(0));
        }

        if(keyCode == 1) {
            backScreen.confirmClicked(false, 2);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        textField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        drawDefaultBackground();

        fontRenderer.drawString("Description de votre guilde : ", width / 2 - 100, height / 2 - 28, -1);
        fontRenderer.drawString("Infos: ';' pour revenir à la ligne", width / 2 - 100, height / 2 - 18, -1);

        textField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        if(id == 0) {
            backScreen.confirmClicked(result, 2);
            if(result && backScreen instanceof GuiGuild)
                ((GuiGuild) backScreen).guild.setDescription(textField.getText());
        }
    }
}

package fr.snyker.pixor.client;

import fr.snyker.CommonProxy;
import fr.snyker.pixor.client.gui.GuiDescription;
import fr.snyker.pixor.client.gui.GuiGuild;
import fr.snyker.pixor.guild.Guild;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {

    public static KeyBinding[] keyBindings;
    public static Guild playerGuild;

    @Override
    public void init() {

        keyBindings = new KeyBinding[1];

        keyBindings[0] = new KeyBinding("key.pixor.guild", Keyboard.KEY_G, "key.pixor.category");

        for (int i = 0; i < keyBindings.length; i++) {
            ClientRegistry.registerKeyBinding(keyBindings[i]);
        }

    }

    public static void changeRole() {
        if(Minecraft.getMinecraft().currentScreen instanceof GuiGuild) {
            ((GuiGuild) Minecraft.getMinecraft().currentScreen).allowChangeRole();
        }
    }

    public static void openGuild() {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            Minecraft.getMinecraft().displayGuiScreen(new GuiGuild(ClientProxy.playerGuild));
        });
    }

    public static void modifyDesc(boolean value) {

        if(Minecraft.getMinecraft().currentScreen instanceof GuiDescription){
            Minecraft.getMinecraft().currentScreen.confirmClicked(value, 0);
        }
    }

    public static void openMember(String name) {

        if(Minecraft.getMinecraft().currentScreen instanceof GuiGuild) {
            ((GuiGuild) Minecraft.getMinecraft().currentScreen).showMember(name);
        }
    }
}

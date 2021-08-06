package fr.snyker.pixor.client;

import fr.snyker.pixor.Constants;
import fr.snyker.pixor.Main;
import fr.snyker.pixor.client.gui.GuiGuild;
import fr.snyker.pixor.network.server.SPacketOpenGuild;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = Constants.MODID)
public class ClientHandler {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void onEvent(InputEvent.KeyInputEvent event) {

        KeyBinding[] copies = ClientProxy.keyBindings;

        if(copies[0].isPressed() && !(Minecraft.getMinecraft().currentScreen instanceof GuiGuild)) {
            Main.NETWORK.sendToServer(new SPacketOpenGuild(Minecraft.getMinecraft().player.getName()));
        }

    }

}

package fr.snyker.pixor.network.client;


import fr.snyker.pixor.Main;
import fr.snyker.pixor.client.ClientProxy;
import fr.snyker.pixor.guild.Guild;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class CPacketOpenGuild implements IMessage {

    private String jsonGuild;
    private boolean hasGuild;

    public CPacketOpenGuild() {}

    public CPacketOpenGuild(boolean hasGuild, @Nullable String jsonGuild) {
        this.hasGuild = hasGuild;
        this.jsonGuild = jsonGuild;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        hasGuild = buf.readBoolean();
        if(hasGuild) {
            jsonGuild = ByteBufUtils.readUTF8String(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(hasGuild);
        if(hasGuild) {
            ByteBufUtils.writeUTF8String(buf, jsonGuild);
        }
    }

    public static class Handler implements IMessageHandler<CPacketOpenGuild, IMessage> {

        @Override
        public IMessage onMessage(CPacketOpenGuild message, MessageContext ctx) {

            boolean hasGuild = message.hasGuild;

            if(!hasGuild) {
                ClientProxy.playerGuild = null;
            } else {
                ClientProxy.playerGuild = Main.GSON.fromJson(message.jsonGuild, Guild.class);
            }

            ClientProxy.openGuild();
            //Minecraft.getMinecraft().displayGuiScreen(new GuiGuild(ClientProxy.playerGuild));
            //Minecraft.getMinecraft().currentScreen.setFocused(true);
            //FMLNetworkHandler.openGui(Minecraft.getMinecraft().player, Constants.MODID, 0, Minecraft.getMinecraft().world, 0, 0, 0);


            return null;
        }
    }
}

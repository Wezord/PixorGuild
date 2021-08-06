package fr.snyker.pixor.network.server;

import fr.snyker.pixor.Constants;
import fr.snyker.pixor.Main;
import fr.snyker.pixor.guild.Guild;
import fr.snyker.pixor.network.client.CPacketOpenGuild;
import fr.snyker.pixor.server.GuildManager;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketOpenGuild implements IMessage {

    private boolean askToOpenGuild;
    private String namePlayer;

    public SPacketOpenGuild() {}

    public SPacketOpenGuild(String name) {
        this.askToOpenGuild = true;
        this.namePlayer = name;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        askToOpenGuild = buf.readBoolean();
        namePlayer = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(askToOpenGuild);
        ByteBufUtils.writeUTF8String(buf, namePlayer);
    }

    public static class Handler implements IMessageHandler<SPacketOpenGuild, CPacketOpenGuild> {

        @Override
        public CPacketOpenGuild onMessage(SPacketOpenGuild message, MessageContext ctx) {

            //Side server
            boolean hasGuild = GuildManager.containsPlayer(message.namePlayer);
            String json = null;

            if(hasGuild) {
                final Guild guild = GuildManager.getByPlayer(message.namePlayer);
                if(guild == null) {
                    hasGuild = false;
                } else {
                    json = Main.GSON.toJson(guild);
                }
            }

            return new CPacketOpenGuild(hasGuild, json);
        }
    }
}

package fr.snyker.pixor.network.server;

import fr.snyker.pixor.guild.Guild;
import fr.snyker.pixor.guild.GuildPermission;
import fr.snyker.pixor.network.client.CPacketModifyDescription;
import fr.snyker.pixor.server.GuildManager;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketModifyDescription implements IMessage {

    private String description;

    public SPacketModifyDescription() {
    }

    public SPacketModifyDescription(String description) {
        this.description =description;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        description = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf,description);
    }

    public static class Handler implements IMessageHandler<SPacketModifyDescription, CPacketModifyDescription> {

        @Override
        public CPacketModifyDescription onMessage(SPacketModifyDescription message, MessageContext ctx) {

            final String name = ctx.getServerHandler().player.getName();
            final Guild guild = GuildManager.getByPlayer(name);
            if(guild != null) {
                if(guild.hasPermission(guild.getMember(name), GuildPermission.MODIFY_DESCRIPTION)) {
                    if(message.description.length() <= 300) {
                        guild.setDescription(message.description);
                        guild.save();
                        return new CPacketModifyDescription(true);
                    }
                }
            }

            return new CPacketModifyDescription(false);
        }
    }
}

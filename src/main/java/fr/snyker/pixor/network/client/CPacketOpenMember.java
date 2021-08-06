package fr.snyker.pixor.network.client;

import fr.snyker.pixor.client.ClientProxy;
import fr.snyker.pixor.client.gui.GuiGuild;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketOpenMember implements IMessage {

    private String nameMember;

    public CPacketOpenMember() {
    }

    public CPacketOpenMember(String nameMember) {
        this.nameMember = nameMember;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        nameMember = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, nameMember);
    }

    public static class Handler implements IMessageHandler<CPacketOpenMember, IMessage> {

        @Override
        public IMessage onMessage(CPacketOpenMember message, MessageContext ctx) {

            ClientProxy.openMember(message.nameMember);

            return null;
        }
    }
}

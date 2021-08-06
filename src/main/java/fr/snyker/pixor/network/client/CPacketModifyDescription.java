package fr.snyker.pixor.network.client;

import fr.snyker.pixor.client.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketModifyDescription implements IMessage {

    private boolean valide;

    public CPacketModifyDescription() {
    }

    public CPacketModifyDescription(boolean valide) {
        this.valide = valide;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        valide = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(valide);
    }

    public static class Handler implements IMessageHandler<CPacketModifyDescription, IMessage> {

        @Override
        public IMessage onMessage(CPacketModifyDescription message, MessageContext ctx) {

            ClientProxy.modifyDesc(message.valide);

            return null;
        }
    }
}

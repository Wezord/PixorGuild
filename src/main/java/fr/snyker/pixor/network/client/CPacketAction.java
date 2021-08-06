package fr.snyker.pixor.network.client;

import fr.snyker.pixor.client.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CPacketAction implements IMessage {

    private int idAction;
    private boolean value;

    public CPacketAction() {
    }

    public CPacketAction(int idAction, boolean value) {
        this.idAction = idAction;
        this.value = value;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        idAction = buf.readInt();
        value = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(idAction);
        buf.writeBoolean(value);
    }

    public static class Handler implements IMessageHandler<CPacketAction, IMessage> {

        @Override
        public IMessage onMessage(CPacketAction message, MessageContext ctx) {

            switch (message.idAction) {
                case 0:

                    ClientProxy.changeRole();

                    break;
            }

            return null;
        }
    }
}

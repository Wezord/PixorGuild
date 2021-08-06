package fr.snyker.pixor.network.server;

import fr.snyker.pixor.guild.Guild;
import fr.snyker.pixor.guild.GuildMember;
import fr.snyker.pixor.guild.GuildPermission;
import fr.snyker.pixor.network.client.CPacketAction;
import fr.snyker.pixor.network.client.CPacketOpenGuild;
import fr.snyker.pixor.server.GuildManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketSelectRoleMember implements IMessage {

    private String memberName;

    public SPacketSelectRoleMember() {}

    public SPacketSelectRoleMember(GuildMember member) {
        this.memberName = member.getName();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.memberName = ByteBufUtils.readUTF8String(buf);

    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, memberName);
    }

    public static class Handler implements IMessageHandler<SPacketSelectRoleMember, CPacketAction> {

        @Override
        public CPacketAction onMessage(SPacketSelectRoleMember message, MessageContext ctx) {

            //On récupère le sender
            EntityPlayer sender = ctx.getServerHandler().player;

            //On récupère la guilde le sender est
            final Guild guild = GuildManager.getByPlayer(sender.getName());
            final Guild gCheck = GuildManager.getByPlayer(message.memberName);

            //Si les deux guildes correspondent
            if(guild != null && gCheck != null && guild.getName().equalsIgnoreCase(gCheck.getName())) {

                //on vérifie la permission d'accès
                final GuildMember whoWant = guild.getMember(sender.getName());
                if(guild.hasPermission(whoWant, GuildPermission.GIVE_ROLE)) {

                    final GuildMember target = guild.getMember(message.memberName);

                    if(!guild.getRole(target.getIdRole()).isOwner()) {
                        return new CPacketAction(0, true);
                    }

                }

            }

            //On envoit rien
            return null;
        }
    }
}

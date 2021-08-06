package fr.snyker.pixor.network.server;

import fr.snyker.pixor.Main;
import fr.snyker.pixor.guild.Guild;
import fr.snyker.pixor.guild.GuildMember;
import fr.snyker.pixor.guild.GuildPermission;
import fr.snyker.pixor.network.client.CPacketOpenGuild;
import fr.snyker.pixor.network.client.CPacketOpenMember;
import fr.snyker.pixor.server.GuildManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketKickMember implements IMessage {

    private String memberName;

    public SPacketKickMember() {}

    public SPacketKickMember(GuildMember member) {
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

    public static class Handler implements IMessageHandler<SPacketKickMember, CPacketOpenGuild> {

        @Override
        public CPacketOpenGuild onMessage(SPacketKickMember message, MessageContext ctx) {

            //On récupère le sender
            EntityPlayer sender = ctx.getServerHandler().player;

            //On récupère la guilde le sender est
            final Guild guild = GuildManager.getByPlayer(sender.getName());
            final Guild gCheck = GuildManager.getByPlayer(message.memberName);

            //Si les deux guildes correspondent
            if(guild != null && gCheck != null && guild.getName().equalsIgnoreCase(gCheck.getName())) {

                //on vérifie la permission d'accès
                final GuildMember whoWant = guild.getMember(sender.getName());
                if(guild.hasPermission(whoWant, GuildPermission.KICK)) {

                    final GuildMember target = guild.getMember(message.memberName);

                    if(!sender.getName().equals(message.memberName)) {

                        if (guild.kickMember(target)) {
                            sender.sendMessage(new TextComponentString("Vous avez exclu " + message.memberName + " de la guilde."));
                            guild.save();
                            return new CPacketOpenGuild(true, Main.GSON.toJson(guild));
                        } else {
                            sender.sendMessage(new TextComponentString("Vous ne pouvez pas exclure votre supérieur."));
                        }

                    } else {
                        sender.sendMessage(new TextComponentString("Vous ne pouvez pas vous exclure."));
                    }

                }

            }

            //On envoit rien
            return null;
        }
    }
}

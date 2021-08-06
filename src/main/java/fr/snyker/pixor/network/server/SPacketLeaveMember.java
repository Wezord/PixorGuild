package fr.snyker.pixor.network.server;

import fr.snyker.pixor.Main;
import fr.snyker.pixor.guild.Guild;
import fr.snyker.pixor.guild.GuildMember;
import fr.snyker.pixor.guild.GuildPermission;
import fr.snyker.pixor.guild.GuildRole;
import fr.snyker.pixor.network.client.CPacketOpenGuild;
import fr.snyker.pixor.server.GuildManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketLeaveMember implements IMessage {

    public SPacketLeaveMember() {}

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<SPacketLeaveMember, CPacketOpenGuild> {

        @Override
        public CPacketOpenGuild onMessage(SPacketLeaveMember message, MessageContext ctx) {

            //On récupère le sender
            EntityPlayer sender = ctx.getServerHandler().player;

            //On récupère la guilde le sender est
            final Guild guild = GuildManager.getByPlayer(sender.getName());

            //Si les deux guildes correspondent
            if(guild != null) {

                //on vérifie la permission d'accès
                final GuildMember member = guild.getMember(sender.getName());
                final GuildRole role = guild.getRole(member.getIdRole());

                if(role.isOwner()) {
                    if(guild.getMembers().size() >= 2) {
                        sender.sendMessage(new TextComponentString("Vous ne pouvez pas abandonner votre guilde tant qu'il y'a du monde."));
                        return null;
                    } else {
                        guild.disband();
                        return new CPacketOpenGuild(false, "");
                    }
                } else {
                    if (guild.removeMember(sender)) {
                        guild.save();
                        return new CPacketOpenGuild(false, "");
                    } else {
                        sender.sendMessage(new TextComponentString("Vous ne pouvez pas quitter votre guilde. Erreur?"));
                    }
                }
            }
            //On envoit rien
            return null;
        }
    }
}

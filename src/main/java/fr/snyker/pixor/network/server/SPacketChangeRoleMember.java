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

public class SPacketChangeRoleMember implements IMessage {

    private String memberName;
    private int idRole;

    public SPacketChangeRoleMember() {}

    public SPacketChangeRoleMember(GuildMember member, int idRole) {
        this.memberName = member.getName();
        this.idRole = idRole;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.memberName = ByteBufUtils.readUTF8String(buf);
        this.idRole = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, memberName);
        buf.writeInt(idRole);
    }

    public static class Handler implements IMessageHandler<SPacketChangeRoleMember, CPacketOpenGuild> {

        @Override
        public CPacketOpenGuild onMessage(SPacketChangeRoleMember message, MessageContext ctx) {

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
                    //Si null sera définit comme rôle de défaut
                    final GuildRole selected = guild.getRole(message.idRole);

                    if(sender.getName().equals(message.memberName) && guild.getRole(target.getIdRole()).isOwner()) {
                        //Le chef ne peut pas changer
                        sender.sendMessage(new TextComponentString("Vous ne pouvez pas changer votre rôle."));
                    } else {

                        final GuildRole executor = guild.getRole(whoWant.getIdRole());
                        final GuildRole executed = guild.getRole(target.getIdRole());

                        //Si le chef transmet son droit.
                        if(executor.isOwner() && !executed.isOwner() && selected.isOwner()) {

                            whoWant.setRole(guild.getDefaultRole());
                            target.setRole(selected);
                            guild.save();

                            sender.sendMessage(new TextComponentString("Vous avez transmit vos droits de guilde."));
                            return new CPacketOpenGuild(true, Main.GSON.toJson(guild));
                        }

                        //Si un mec essai de récupérer le rôle de chef
                        if(!executor.isOwner() && executed.isOwner() && selected.isOwner()) {
                            //Hack probablement
                            sender.sendMessage(new TextComponentString("Vous ne pouvez pas récupérer les droits de cette façon."));
                            return null;
                        }

                        if(!selected.isOwner() && !executed.isOwner()) {

                            target.setRole(selected);
                            guild.save();

                            sender.sendMessage(new TextComponentString("Vous avez attribué un nouveau rôle a " + target.getName()));
                            return new CPacketOpenGuild(true, Main.GSON.toJson(guild));
                        }

                    }

                }

            }

            //On envoit rien
            return null;
        }
    }
}

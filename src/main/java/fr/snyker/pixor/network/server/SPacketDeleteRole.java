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
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketDeleteRole implements IMessage {

    private int idRole;

    public SPacketDeleteRole() {
    }

    public SPacketDeleteRole(int idRole) {
        this.idRole = idRole;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.idRole = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(idRole);
    }

    public static class Handler implements IMessageHandler<SPacketDeleteRole, CPacketOpenGuild> {

        @Override
        public CPacketOpenGuild onMessage(SPacketDeleteRole message, MessageContext ctx) {

            EntityPlayer player = ctx.getServerHandler().player;
            String playerName = ctx.getServerHandler().player.getName();

            Guild guild = GuildManager.getByPlayer(playerName);

            if(guild != null) {
                GuildMember member = guild.getMember(playerName);

                if(guild.hasPermission(member, GuildPermission.DELETE_ROLE)) {
                    if (guild.hasRole(message.idRole)) {

                        if (guild.deleteRole(message.idRole)) {
                            guild.save();
                            return new CPacketOpenGuild(true, Main.GSON.toJson(guild));
                        } else {
                            player.sendMessage(new TextComponentString("Impossible de supprimer ce rôle."));
                        }
                    } else {
                        player.sendMessage(new TextComponentString("Le rôle que vous essayez de supprimer n'existe pas."));
                    }
                } else {
                    player.sendMessage(new TextComponentString("Vous n'avez pas la permission de supprimer un rôle."));
                }
            }


            return null;
        }
    }
}

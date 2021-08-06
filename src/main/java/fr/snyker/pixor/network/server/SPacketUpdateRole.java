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

public class SPacketUpdateRole implements IMessage {

    private String jsonRole;

    public SPacketUpdateRole() {
    }

    public SPacketUpdateRole(String jsonRole) {
        this.jsonRole = jsonRole;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        jsonRole = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf,jsonRole);
    }

    public static class Handler implements IMessageHandler<SPacketUpdateRole, CPacketOpenGuild> {

        @Override
        public CPacketOpenGuild onMessage(SPacketUpdateRole message, MessageContext ctx) {

            EntityPlayer player = ctx.getServerHandler().player;
            String playerName = ctx.getServerHandler().player.getName();

            if(message.jsonRole != null) {
                GuildRole modifiedOrCreated = Main.GSON.fromJson(message.jsonRole, GuildRole.class);

                if(modifiedOrCreated != null) {
                    Guild guild = GuildManager.getByPlayer(playerName);

                    if(guild != null) {
                        GuildMember member = guild.getMember(playerName);

                        if(guild.hasPermission(member, GuildPermission.CREATE_ROLE)) {
                            if (guild.hasRole(modifiedOrCreated)) {

                                if (guild.updateRole(modifiedOrCreated)) {
                                    guild.save();
                                    return new CPacketOpenGuild(true, Main.GSON.toJson(guild));
                                } else {
                                    player.sendMessage(new TextComponentString("Le rôle que vous essayez de modifier n'existe pas."));
                                }

                            } else {
                                if (guild.addRole(modifiedOrCreated)) {
                                    guild.save();
                                    return new CPacketOpenGuild(true, Main.GSON.toJson(guild));
                                } else {
                                    player.sendMessage(new TextComponentString("Impossible d'ajouter un nouveau rôle de guilde."));
                                }
                            }
                        } else {
                            player.sendMessage(new TextComponentString("Vous n'avez pas la permission de créer ou modifier un rôle."));
                        }
                    }
                }
            }

            return null;
        }
    }
}

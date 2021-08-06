package fr.snyker.pixor.network.server;

import fr.snyker.pixor.guild.Guild;
import fr.snyker.pixor.guild.GuildMember;
import fr.snyker.pixor.guild.GuildPermission;
import fr.snyker.pixor.server.GuildInvite;
import fr.snyker.pixor.server.GuildManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SPacketInviteMember implements IMessage {

    private String playerName;

    public SPacketInviteMember() {
    }

    public SPacketInviteMember(String playerName) {
        this.playerName = playerName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerName);
    }

    public static class Handler implements IMessageHandler<SPacketInviteMember, IMessage> {

        @Override
        public IMessage onMessage(SPacketInviteMember message, MessageContext ctx) {

            final EntityPlayer playerSender = ctx.getServerHandler().player;
            final String sender = ctx.getServerHandler().player.getName();
            final String target = message.playerName;

            if(!target.isEmpty()) {

                //On regarde que le joueur est bien connecté
                EntityPlayer playerTarget = playerSender.getServer().getPlayerList().getPlayerByUsername(target);

                if(playerTarget == null) {
                    playerSender.sendMessage(new TextComponentString("Le joueur doit être connecté pour l'inviter dans la guilde."));
                    return null;
                }

                final Guild gSender = GuildManager.getByPlayer(sender);

                if(gSender != null) {
                    final GuildMember mSender = gSender.getMember(sender);

                    if(gSender.hasPermission(mSender, GuildPermission.INVITE)) {
                        if (sender.equals(target)) {
                            playerSender.sendMessage(new TextComponentString("Vous ne pouvez pas vous inviter vous-mêmes... Sauf si vous trouvez le parchemin de l'intelligence."));
                        } else {

                            if(GuildManager.containsPlayer(target)) {
                                playerSender.sendMessage(new TextComponentString(target + " fait déjà partie d'une guilde, il doit d'abord la quitter."));
                            } else {

                                if(gSender.getBannedList().contains(target)) {
                                    playerSender.sendMessage(new TextComponentString("Cette personne est banni de votre guilde."));
                                    return null;
                                }

                                //On crée l'invitation
                                final GuildInvite guildInvite = new GuildInvite(sender, target, gSender);
                                if(!GuildManager.invite(guildInvite)) {
                                    playerSender.sendMessage(new TextComponentString("Vous avez déjà envoyé une invitation à ce joueur, attendez un peu."));
                                    return null;
                                }

                                ITextComponent invite = new TextComponentString("Vous avez reçu une invitation à la guilde (60s pour accepter) : ");
                                    ITextComponent guildName = new TextComponentString(gSender.getName());
                                    guildName.getStyle().setColor(TextFormatting.YELLOW);
                                invite.appendSibling(guildName);
                                invite.appendText("\n");

                                ITextComponent row = new TextComponentString("");

                                    ITextComponent accept = new TextComponentString("[Accepter]");
                                    accept.getStyle().setColor(TextFormatting.GREEN);
                                    accept.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Cliquez pour rejoindre la guilde")));
                                    accept.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pgaccept " + sender));
                                    row.appendSibling(accept);

                                row.appendSibling(new TextComponentString("     "));

                                    ITextComponent refuse = new TextComponentString("[Refuser]");
                                    refuse.getStyle().setColor(TextFormatting.RED);
                                    refuse.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Cliquez pour rejoindre la guilde")));
                                    refuse.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pgrefuse " + sender));
                                    row.appendSibling(refuse);

                                invite.appendSibling(row);

                                playerTarget.sendMessage(invite);
                            }
                        }
                    } else {
                        //Probablement un hack du client a 99.99%
                        playerSender.sendMessage(new TextComponentString("Vous n'avez pas les droits d'inviter dans votre guilde."));
                    }
                }
                //Hack aussi
            }

            return null;
        }
    }
}

package fr.snyker.pixor.command;

import fr.snyker.pixor.guild.Guild;
import fr.snyker.pixor.guild.GuildMember;
import fr.snyker.pixor.server.GuildInvite;
import fr.snyker.pixor.server.GuildManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandGuildAccept extends CommandBase {

    @Override
    public String getName() {
        return "pgaccept";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/pgaccept <player[Send of request]>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {

        if(commandSender instanceof EntityPlayer) {

            //On oublie pas de rafraichir
            GuildManager.refreshInvites();

            final EntityPlayer sender = getPlayer(server, commandSender, args[0]);
            final GuildInvite invite = GuildManager.getInviteByTarget(commandSender.getName(), sender.getName());

            if(invite != null) {

                Guild guild = invite.getGuildSender();

                if(guild.isAlive()) {

                    if (guild.addMember(new GuildMember((EntityPlayer) commandSender, -1))) {

                        commandSender.sendMessage(new TextComponentString("Vous avez rejoins la guilde : " + guild.getName()));
                        guild.save();
                        GuildManager.destroyInvite(invite);

                    } else {
                        commandSender.sendMessage(new TextComponentString("Vous ne pouvez pas rejoindre la guilde car elle est pleine."));
                    }

                } else {
                    commandSender.sendMessage(new TextComponentString("La guilde que vous essayez de rejoindre n'existe plus."));
                }

            } else {
                commandSender.sendMessage(new TextComponentString("Vous n'avez pas d'invitation de ce joueur."));
            }

        } else {
            commandSender.sendMessage(new TextComponentString("Seulement un joueur peut faire cette commande."));
        }

    }
}

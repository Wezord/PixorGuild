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

public class CommandGuildRefuse extends CommandBase {
    @Override
    public String getName() {
        return "pgrefuse";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/pgrefuse <player[Sender of request]>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) throws CommandException {

        if(commandSender instanceof EntityPlayer) {

            //On oublie pas de rafraichir
            GuildManager.refreshInvites();

            final EntityPlayer sender = getPlayer(server, commandSender, args[0]);
            final GuildInvite invite = GuildManager.getInviteByTarget(commandSender.getName(), sender.getName());

            if(invite != null) {
                GuildManager.destroyInvite(invite);
                commandSender.sendMessage(new TextComponentString("Vous avez refusé l'invitation de guilde."));
            } else {
                commandSender.sendMessage(new TextComponentString("Vous n'avez pas d'invitation de ce joueur."));
            }

        } else {
            commandSender.sendMessage(new TextComponentString("Seulement un joueur peut faire cette commande."));
        }


    }
}

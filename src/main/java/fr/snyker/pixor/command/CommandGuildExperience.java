package fr.snyker.pixor.command;

import fr.snyker.pixor.guild.Guild;
import fr.snyker.pixor.guild.GuildMember;
import fr.snyker.pixor.server.GuildManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandGuildExperience extends CommandBase {
    @Override
    public String getName() {
        return "pgexp";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/pgexp <player> <amount>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if(args.length >= 2) {

            EntityPlayer target = getPlayer(server, sender, args[0]);

            if(target != null) {

                Guild guild = GuildManager.getByPlayer(target.getName());

                if(guild != null) {
                    GuildMember member = guild.getMember(target.getName());

                    float value = 0;

                    try {
                        value = Float.parseFloat(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(new TextComponentString("L'argument doit être un nombre."));
                    }

                    member.addExpGiven(value);
                    guild.giveExperience(value);
                    guild.save();

                    sender.sendMessage(new TextComponentString("Vous avez attribué " + value + " à la guilde " + guild.getName()));

                } else {
                    sender.sendMessage(new TextComponentString("Le joueur n'a pas de guilde."));
                }

            }

        } else {
            sender.sendMessage(new TextComponentString("Il manque des arguments."));
        }

    }
}

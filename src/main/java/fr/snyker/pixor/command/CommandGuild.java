package fr.snyker.pixor.command;

import fr.snyker.pixor.Constants;
import fr.snyker.pixor.guild.Guild;
import fr.snyker.pixor.server.GuildManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class CommandGuild extends CommandBase {

    @Override
    public String getName() {
        return "pguilde";
    }

    @Override
    public String getUsage(ICommandSender iCommandSender) {
        return Constants.MODID + ":commands.pixor.guild.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer minecraftServer, ICommandSender sender, String[] args) throws CommandException {

        if(args.length > 0) {

            final String param = args[0];

            switch (param) {
                case "create":

                    if(args.length >= 3) {

                        //On récupère le joueur
                        final EntityPlayer entityPlayer = getPlayer(minecraftServer, sender, args[1]);

                        //Si il n'est pas dans une guilde
                        if(!GuildManager.containsPlayer(entityPlayer.getName())) {

                            //On récupère le nom de la guilde
                            StringBuilder builder = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                builder.append(args[i]);
                                if (i + 1 < args.length) builder.append(" ");
                            }

                            if (Pattern.matches("([a-zA-Z\\-\\s']+)", builder.toString()) && builder.toString().length() <= 28) {

                                if(GuildManager.exists(builder.toString())) {
                                    throw new CommandException(Constants.MODID + ":commands.pixor.guild.exists");
                                }

                                final Guild guild = new Guild(builder.toString(), entityPlayer);
                                GuildManager.guildList.add(guild);
                                guild.save();

                                sender.sendMessage(new TextComponentString("Votre guilde a bien été crée."));
                            } else {
                                sender.sendMessage(new TextComponentString("Vous ne pouvez utiliser que les caractères, tirets, apostrophe, espace et 28 caractères max."));
                                throw new WrongUsageException(Constants.MODID + ":commands.pixor.guild.create.pattern");
                            }
                        } else {
                            sender.sendMessage(new TextComponentString("Le joueur est déjà dans une guilde."));
                        }
                    } else
                        throw new WrongUsageException(Constants.MODID + ":commands.pixor.guild.create.arguments");

                    break;
                case "kick":
                    break;
                case "ban":
                    break;
                case "leave":

                    if(sender instanceof EntityPlayer) {
                        final EntityPlayer itself = getCommandSenderAsPlayer(sender);

                        if(GuildManager.containsPlayer(itself.getName())) {
                            final Guild guild = GuildManager.getByPlayer(itself.getName());
                            if(guild.removeMember(itself)) {
                                guild.save();
                                sender.sendMessage(new TextComponentString("Vous avez quitté votre guilde."));
                            } else {
                                sender.sendMessage(new TextComponentString("Vous ne pouvez pas quitter cette guilde."));
                            }
                        } else {
                            sender.sendMessage(new TextComponentString("Vous devez possédez une guilde pour faire cette action."));
                        }

                    } else {
                        sender.sendMessage(new TextComponentString("Vous devez être un joueur pour faire cette commande."));
                    }

                    break;
            }

        } else {
            throw new WrongUsageException(Constants.MODID + ":commands.pixor.guild.usage");
        }

    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return args.length == 2 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.emptyList();
    }
}

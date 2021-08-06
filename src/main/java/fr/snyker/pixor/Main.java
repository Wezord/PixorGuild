package fr.snyker.pixor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.snyker.CommonProxy;
import fr.snyker.pixor.command.CommandGuild;
import fr.snyker.pixor.command.CommandGuildAccept;
import fr.snyker.pixor.command.CommandGuildExperience;
import fr.snyker.pixor.command.CommandGuildRefuse;
import fr.snyker.pixor.guild.Guild;
import fr.snyker.pixor.network.client.CPacketAction;
import fr.snyker.pixor.network.client.CPacketModifyDescription;
import fr.snyker.pixor.network.client.CPacketOpenGuild;
import fr.snyker.pixor.network.client.CPacketOpenMember;
import fr.snyker.pixor.network.server.*;
import fr.snyker.pixor.server.GuildManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Mod(modid = Constants.MODID, name = Constants.NAME, version = Constants.VERSION)
public class Main {

    public static Logger LOGGER;
    public static File FOLDER = new File("./guilds");
    public static Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .excludeFieldsWithoutExposeAnnotation()
            .create();


    @SidedProxy(serverSide = "fr.snyker.pixor.server.ServerProxy", clientSide = "fr.snyker.pixor.client.ClientProxy")
    public static CommonProxy commonProxy;

    public static SimpleNetworkWrapper NETWORK;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        LOGGER = event.getModLog();

        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MODID);

        int idNetwork = 0;
        NETWORK.registerMessage(SPacketOpenGuild.Handler.class,SPacketOpenGuild.class, idNetwork++, Side.SERVER);
        NETWORK.registerMessage(CPacketOpenGuild.Handler.class, CPacketOpenGuild.class, idNetwork++, Side.CLIENT);
        NETWORK.registerMessage(SPacketOpenMember.Handler.class, SPacketOpenMember.class, idNetwork++, Side.SERVER);
        NETWORK.registerMessage(CPacketOpenMember.Handler.class, CPacketOpenMember.class, idNetwork++, Side.CLIENT);
        NETWORK.registerMessage(SPacketModifyDescription.Handler.class, SPacketModifyDescription.class, idNetwork++, Side.SERVER);
        NETWORK.registerMessage(CPacketModifyDescription.Handler.class, CPacketModifyDescription.class, idNetwork++, Side.CLIENT);
        NETWORK.registerMessage(SPacketInviteMember.Handler.class, SPacketInviteMember.class, idNetwork++, Side.SERVER);
        NETWORK.registerMessage(SPacketKickMember.Handler.class, SPacketKickMember.class, idNetwork++, Side.SERVER);
        NETWORK.registerMessage(SPacketBanMember.Handler.class, SPacketBanMember.class, idNetwork++, Side.SERVER);
        NETWORK.registerMessage(SPacketSelectRoleMember.Handler.class, SPacketSelectRoleMember.class, idNetwork++, Side.SERVER);
        NETWORK.registerMessage(SPacketChangeRoleMember.Handler.class, SPacketChangeRoleMember.class, idNetwork++, Side.SERVER);
        NETWORK.registerMessage(CPacketAction.Handler.class, CPacketAction.class, idNetwork++, Side.CLIENT);
        NETWORK.registerMessage(SPacketUpdateRole.Handler.class, SPacketUpdateRole.class, idNetwork++, Side.SERVER);
        NETWORK.registerMessage(SPacketDeleteRole.Handler.class, SPacketDeleteRole.class, idNetwork++, Side.SERVER);
        NETWORK.registerMessage(SPacketLeaveMember.Handler.class, SPacketLeaveMember.class, idNetwork++, Side.SERVER);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        commonProxy.init();
    }

    @Mod.EventHandler
    public void serverInit(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandGuild());
        event.registerServerCommand(new CommandGuildAccept());
        event.registerServerCommand(new CommandGuildRefuse());
        event.registerServerCommand(new CommandGuildExperience());

        if(!FOLDER.exists()) {
            FOLDER.mkdir();
        }

        for (File file : FOLDER.listFiles()) {
            if(file.canRead() && !file.isDirectory()) {

                try {
                    FileReader fileReader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    final Guild guild = GSON.fromJson(bufferedReader, Guild.class);
                    if(guild != null) {
                        GuildManager.guildList.add(guild);
                    }

                    bufferedReader.close();
                    fileReader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

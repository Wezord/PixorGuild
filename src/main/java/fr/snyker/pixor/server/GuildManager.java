package fr.snyker.pixor.server;

import fr.snyker.pixor.guild.Guild;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class GuildManager {

    public static List<Guild> guildList = new ArrayList<>();
    private static final List<GuildInvite> invites = new ArrayList<>();

    public static boolean containsPlayer(String namePlayer) {
        return guildList.stream().anyMatch(g -> g.getMembers().stream().anyMatch(m -> m.getName().equals(namePlayer)));
    }

    public static Guild getByPlayer(String namePlayer) {
        return guildList.stream().filter(g -> g.getMembers().stream().anyMatch(m -> m.getName().equals(namePlayer)))
                .findFirst().orElse(null);
    }

    public static boolean exists(String nameGuild) {
        return guildList.stream().anyMatch(g -> g.getName().equalsIgnoreCase(nameGuild));
    }

    public static boolean invite(GuildInvite guildInvite) {
        refreshInvites();

        if(invites.stream().noneMatch(invite -> invite.equals(guildInvite))) {
            invites.add(guildInvite);
            return true;
        }
        return false;
    }

    public static void refreshInvites() {
        //Toutes les invitations de plus de 60 secondes sont resets
        invites.removeIf(i -> i.getInvitedAt() + 60 * 1000L < System.currentTimeMillis());
    }

    public static GuildInvite getInviteByTarget(String target, String sender) {
        return invites.stream().filter(i -> i.contains(target, sender)).findFirst().orElse(null);
    }

    public static void destroyInvite(GuildInvite invite) {
        invites.removeIf(i -> i.equals(invite));
        refreshInvites();
    }


}

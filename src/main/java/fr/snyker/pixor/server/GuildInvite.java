package fr.snyker.pixor.server;

import fr.snyker.pixor.guild.Guild;

import java.util.Objects;

public class GuildInvite {

    private final String sender;
    private final String target;
    private final Guild gSender;

    private final long invitedAt;

    public GuildInvite(String sender, String target, Guild gSender) {
        this.sender = sender;
        this.target = target;
        this.gSender = gSender;

        invitedAt = System.currentTimeMillis();
    }

    public Guild getGuildSender() {
        return gSender;
    }

    public String getSender() {
        return sender;
    }

    public String getTarget() {
        return target;
    }

    public long getInvitedAt() {
        return invitedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuildInvite that = (GuildInvite) o;
        return Objects.equals(sender, that.sender) && Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, target);
    }

    public boolean contains(String target, String sender) {
        return this.sender.equals(sender) && this.target.equals(target);
    }
}

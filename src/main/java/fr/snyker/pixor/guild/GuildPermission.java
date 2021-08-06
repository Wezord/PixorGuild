package fr.snyker.pixor.guild;

public enum GuildPermission {

    INVITE("Inviter un joueur"),
    KICK("Exclure un joueur"),
    BAN("Bannir un joueur"),
    CREATE_ROLE("Créer un rôle"),
    GIVE_ROLE("Donner un rôle"),
    DELETE_ROLE("Supprimer un rôle"),
    MODIFY_DESCRIPTION("Modifier la description");

    private final String info;

    GuildPermission(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}

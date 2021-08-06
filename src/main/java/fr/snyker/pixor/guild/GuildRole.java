package fr.snyker.pixor.guild;

import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;

public class GuildRole {

    @Expose
    private final int idRole;
    @Expose
    private String name;
    @Expose
    private boolean canBeDeleted = true;
    @Expose
    private boolean owner = false;
    @Expose
    private Map<GuildPermission, Boolean> permissions = new HashMap<>();

    public GuildRole(int capacity, String name) {
        this(capacity, name, false);
    }

    protected GuildRole(int capacity, String name, boolean owner) {
        this.owner = owner;
        this.idRole = capacity;
        this.name = name;
        this.canBeDeleted = !owner;
        initPermissions();
    }

    protected void initPermissions() {
        for (GuildPermission value : GuildPermission.values()) {
            permissions.put(value, owner);
        }
    }

    public int getIdRole() {
        return idRole;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCanBeDeleted() {
        return canBeDeleted;
    }

    public boolean isOwner() {
        return owner;
    }

    public Map<GuildPermission, Boolean> getPermissions() {
        return permissions;
    }

    public boolean hasPermission(GuildPermission permission) {
        return permissions.get(permission);
    }

    public void setPermission(GuildPermission permission, boolean value) {
        this.permissions.replace(permission, value);
    }

    public void update(GuildRole role) {
        this.name = role.getName();
        this.permissions.clear();
        this.permissions.putAll(role.getPermissions());
    }
}

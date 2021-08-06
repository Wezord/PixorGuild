package fr.snyker.pixor.guild;

import com.google.gson.annotations.Expose;
import net.minecraft.entity.player.EntityPlayer;

public class GuildMember {

    @Expose
    private String name;
    @Expose
    private int idRole = -1;
    @Expose
    private double expGiven = 0;

    public GuildMember(EntityPlayer player, int idRole) {
        this.name = player.getName();
        this.idRole = idRole;
    }

    public String getName() {
        return name;
    }

    public int getIdRole() {
        return idRole;
    }

    public void setRole(GuildRole role) {
        setIdRole(role.getIdRole());
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }

    public double getExpGiven() {
        return expGiven;
    }

    public void addExpGiven(float value) {
        this.expGiven += value;
    }
}

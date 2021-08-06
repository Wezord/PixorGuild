package fr.snyker.pixor.guild;

import com.google.gson.annotations.Expose;
import fr.snyker.pixor.Main;
import fr.snyker.pixor.config.Configuration;
import fr.snyker.pixor.server.GuildManager;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class Guild {

    @Expose
    private String name;
    @Expose
    private String description;
    @Expose
    private int level;
    @Expose
    private float experience, totalExperience;
    @Expose
    private List<GuildMember> members = new ArrayList<>();
    @Expose
    private List<GuildRole> roles = new ArrayList<>();
    @Expose
    private List<String> banned = new ArrayList<>();
    @Expose
    private int defaultRole;

    public Guild(String name, EntityPlayer playerOwner) {
        //Nom de la guilde
        this.name = name;
        this.level = 1;
        this.description = Configuration.defaultDescription;

        //Ajout des roles par défaut
        roles.add(new GuildRole(0, Configuration.roleOwnerName, true));
        roles.add(new GuildRole(1, Configuration.roleNewerName));
        this.defaultRole = 1; //On définit le role par défaut au nouveau role

        //Création du chef de guilde
        final GuildMember member = new GuildMember(playerOwner, 0); //0 fait référence a l'owner
        members.add(member);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public float getExperience() {
        return experience;
    }

    public float getTotalExperience() {
        return totalExperience;
    }

    public List<GuildMember> getMembers() {
        return members;
    }

    public GuildMember getMember(String name) {
        return members.stream().filter(m -> m.getName().equals(name)).findFirst().orElse(null);
    }

    //On part du principe que le joueur est déjà dans la guilde
    public boolean removeMember(EntityPlayer toRemove) {
        GuildMember member = getMember(toRemove.getName());
        return kickMember(member);
    }

    public boolean addMember(GuildMember member) {
        if(getMembers().size() + 1 <= getMaxMembers()) {

            if(member.getIdRole() < 0) {
                member.setIdRole(defaultRole);
            }

            members.add(member);

            return true;
        }
        return false;
    }

    public boolean kickMember(GuildMember member) {
        GuildRole role = getRole(member.getIdRole());

        //Si le chef il peut pas quitter sa guilde
        if(role.isOwner()) return false;
        return members.removeIf(m -> m.equals(member) || m.getName().equals(member.getName()));
    }

    public boolean banMember(GuildMember member) {
        GuildRole role = getRole(member.getIdRole());

        //Si le chef il peut pas quitter sa guilde
        if(role.isOwner()) return false;

        banned.add(member.getName());

        return members.removeIf(m -> m.equals(member) || m.getName().equals(member.getName()));
    }

    public List<GuildRole> getRoles() {
        return roles;
    }

    public GuildRole getRole(int idRole) {
        return roles.stream().filter(r -> r.getIdRole() == idRole).findFirst().orElse(new GuildRole(-1, "ERROR"));
    }

    public GuildRole getDefaultRole() {
        return getRole(this.defaultRole);
    }

    public boolean addRole(GuildRole role) {

        if(roles.size() + 1 > 40) {
            return false;
        }

        if(role.getIdRole() < -1) {
            AtomicInteger capacity = new AtomicInteger(-1);
            getRoles().forEach(r -> {
                if (capacity.get() <= r.getIdRole())
                    capacity.set(r.getIdRole() + 1);
            });
            GuildRole copy = new GuildRole(capacity.get(), role.getName());
            copy.update(role);
            roles.add(copy);
            return true;
        }

        roles.add(role);
        return role.getIdRole() > 0;
    }

    public boolean deleteRole(int idRole) {
        GuildRole role = getRole(idRole);
        if(role.getIdRole()>0) {

            if(roles.size() - 1 >= 2) {
                roles.remove(role);
                if(defaultRole == idRole)
                    this.defaultRole = roles.get(1).getIdRole();

                members.stream().filter(m -> m.getIdRole() == idRole)
                        .forEach(m -> m.setIdRole(defaultRole));

                return true;
            }

        }
        return false;
    }

    public boolean updateRole(GuildRole role) {
        if(getRoles().stream().anyMatch(r -> r.getIdRole() == role.getIdRole())) {
            getRoles().stream().filter(r -> r.getIdRole() == role.getIdRole()).findFirst()
                    .get().update(role);
            return true;
        }
        return false;
    }

    public boolean hasRole(@Nonnull GuildRole role) {
        return getRoles().stream().anyMatch(r -> r.getIdRole() == role.getIdRole());
    }

    public boolean hasRole(int idRole) {
        return getRoles().stream().anyMatch(r -> r.getIdRole() == idRole);
    }

    public List<String> getBannedList() {
        return banned;
    }

    public int getMaxMembers() {
        if(Configuration.memberLimit) {
            return Configuration.maxMembers + ((level-1)*Configuration.memberIncrease);
        }
        return Configuration.maxMembers;
    }

    private void addLevel(int level) {
        this.level += level;
        this.level = Math.min(Configuration.maxLevel, this.level);
    }

    private void giveTotalExperience(float exp) {
        this.totalExperience += exp;
    }

    public void giveExperience(float experience) {
        float experienceGuild = this.getExperience();
        this.giveTotalExperience(experience);
        if (experienceGuild + experience < this.getExperienceToUp()) {
            this.experience = experience + experienceGuild;
        } else if (experienceGuild + experience == this.getExperienceToUp()) {
            this.experience = 0;
            this.addLevel(Math.min(Configuration.maxLevel - this.getLevel(), 1));
        } else {
            int levelAdd = 0;
            float experienceAdd = experienceGuild + experience;

            do {
                experienceAdd -= this.getExperienceToUp(this.getLevel() + levelAdd);
                ++levelAdd;
            } while(experienceAdd > this.getExperienceToUp(this.getLevel() + levelAdd));

            int newLevel = levelAdd + this.getLevel();
            if (newLevel >= Configuration.maxLevel) {
                this.addLevel(Math.min(Configuration.maxLevel - this.getLevel(), levelAdd));
                this.experience = 0;
            } else {
                this.addLevel(levelAdd);
                this.experience = experienceAdd;
            }
        }

    }

    public float getExperienceToUp() {
        return getExperienceToUp(getLevel());
    }

    public float getExperienceToUp(int level) {
        return 1100 * level + 1100 * (level - 1);
    }

    @SideOnly(Side.SERVER)
    public void save() {
        File save = new File(Main.FOLDER, name.toLowerCase(Locale.ROOT)
                .replace("'","_").replace(" ", "_") + ".json");

        try {
            if (!save.exists()) {
                save.createNewFile();
            }

            String json = Main.GSON.toJson(this);

            FileWriter fileWriter = new FileWriter(save);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(json);
            bufferedWriter.flush();
            bufferedWriter.close();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasPermission(GuildMember member, GuildPermission permission) {
        if(member == null) return false;
        return getRole(member.getIdRole()).hasPermission(permission);
    }

    public boolean isAlive() {
        return members.size() > 0;
    }

    @SideOnly(Side.SERVER)
    public void disband() {
        GuildManager.guildList.removeIf(g -> g.name.equalsIgnoreCase(this.name));

        File save = new File(Main.FOLDER, name.toLowerCase(Locale.ROOT)
                .replace("'","_").replace(" ", "_") + ".json");

        if(save.exists()) {
            save.delete();
        }
    }
}

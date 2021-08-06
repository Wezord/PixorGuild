package fr.snyker.pixor.client.gui;

import fr.snyker.pixor.Constants;
import fr.snyker.pixor.Main;
import fr.snyker.pixor.client.ClientProxy;
import fr.snyker.pixor.client.gui.button.GuiCloseButton;
import fr.snyker.pixor.client.gui.button.GuiEditButton;
import fr.snyker.pixor.client.gui.button.GuiEditMemberButton;
import fr.snyker.pixor.client.gui.button.GuiInviteButton;
import fr.snyker.pixor.guild.Guild;
import fr.snyker.pixor.guild.GuildMember;
import fr.snyker.pixor.guild.GuildPermission;
import fr.snyker.pixor.guild.GuildRole;
import fr.snyker.pixor.network.server.*;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class GuiGuild extends GuiScreen {

    private final ResourceLocation PLAYER_BORDER = new ResourceLocation(Constants.MODID, "textures/gui/player_border.png");
    private final ResourceLocation BACKGROUND = new ResourceLocation(Constants.MODID, "textures/gui/background.png");

    private final int backgroundDivide = 3;
    private int startX, startY;

    private GuiButton addRole;
    private GuiButton listRole;
    private GuiEditButton editDescription; //Edit la description
    private GuiButton changeRoleMember;
    private GuiButton kickMember;
    private GuiButton leaveMember;
    private GuiButton banMember;
    private GuiCloseButton closeMember; //Ferme le membre en train d'être vu
    private GuiCloseButton closeRoles; //Ferme les roles

    private GuiType type;
    protected final Guild guild;
    protected GuildMember viewer;
    protected GuildMember edit;

    public GuiGuild(Guild guild) {
        this.guild = guild;
        this.type = GuiType.DESCRIPTION;
    }

    @Override
    public void initGui() {
        super.initGui();

        startX = (width - 1080/backgroundDivide) / 2;
        startY = (height - 719/backgroundDivide) / 2;

        if(guild != null) {
            viewer = guild.getMember(mc.player.getName());

            assert viewer != null;

            //On vérifie que le perso ait la permission d'inviter
            if(guild.hasPermission(viewer, GuildPermission.INVITE)) {
                if (guild.getMembers().size() < guild.getMaxMembers()) {
                    int i = startY + 18 + 14 + guild.getMembers().size() * 60 / backgroundDivide; //60 fait reference a la hauteur du sprite du fond du membre
                    addButton(new GuiInviteButton(0, (width / 2 - (477 / backgroundDivide)) - 6, i));
                }
            }

            //On verifie que le perso ait la permission de gérer les roles
            if(guild.hasPermission(viewer, GuildPermission.CREATE_ROLE) || guild.hasPermission(viewer,GuildPermission.DELETE_ROLE)) {
                addButton(new GuiEditButton(1, (width + 1080 / backgroundDivide) / 2 - 48 / backgroundDivide - 16, startY + 36));
            }

            editDescription = addButton(new GuiEditButton(2, (width + 1080 / backgroundDivide) / 2 - 48 / backgroundDivide - 16, startY + 94));
            editDescription.visible = false;
            //On vérifie que le perso ait la permission de gérer la description
            if(guild.hasPermission(viewer, GuildPermission.MODIFY_DESCRIPTION)) {
                editDescription.visible = type == GuiType.DESCRIPTION;
            }

            //On vérifie que le perso ait la permission de donner des roles
            if(guild.hasPermission(viewer, GuildPermission.GIVE_ROLE) || viewer.getName().equals(mc.player.getName())) {
                for (int i = 0; i < Math.min(10, guild.getMembers().size()); i++) {
                    int y = startY + 18 + 16 + i * 60 / backgroundDivide; //60 fait reférence a la hauteur du sprite du fond du membre
                    //On commence a 2 le recensement des boutons des joueurs
                    addButton(new GuiEditMemberButton(10 + i, width / 2 - 48/3 - 12, y, guild.getMembers().get(i)));
                }
            }

            addRole = addButton(new GuiButton(3, width / 2 + 8, startY + 130, 1080 / backgroundDivide / 2 - 24, 20, "Ajouter un rôle"));
            addRole.visible = type == GuiType.ROLE;
            addRole.enabled = guild.hasPermission(viewer, GuildPermission.CREATE_ROLE);

            addButton(listRole = new GuiButton(4, width / 2 + 8, startY + 154, 1080 / backgroundDivide / 2 - 24, 20, "Gérer les rôles"));
            listRole.visible = type == GuiType.ROLE;

            addButton(closeRoles = new GuiCloseButton(5, editDescription.x, editDescription.y));
            closeRoles.visible = type == GuiType.ROLE;

            closeMember = addButton(new GuiCloseButton(6, editDescription.x, editDescription.y));
            closeMember.visible = type == GuiType.MEMBER; //On met pas la variable edit au cas ou

            changeRoleMember = addButton(new GuiButton(7, width / 2 + 8, startY + 150, 1080 / backgroundDivide / 2 - 24, 20, "Changer le role"));
            changeRoleMember.enabled = guild.hasPermission(viewer, GuildPermission.GIVE_ROLE) && edit != null;
            changeRoleMember.visible = edit != null;
            kickMember = addButton(new GuiButton(8, width / 2 + 8, startY + 174, 1080 / backgroundDivide / 2 - 24, 20,"Exclure"));
            kickMember.enabled = guild.hasPermission(viewer, GuildPermission.KICK) && edit != null;
            kickMember.visible = edit != null;
            banMember = addButton(new GuiButton(9, width / 2 + 8, startY + 198, 1080 / backgroundDivide / 2 - 24, 20,TextFormatting.RED + "Bannir"));
            banMember.enabled = guild.hasPermission(viewer, GuildPermission.BAN) && edit != null;
            banMember.visible = edit != null && !viewer.getName().equals(edit.getName());
            leaveMember = addButton(new GuiButton(9, banMember.x, banMember.y, banMember.width, banMember.height, TextFormatting.RED+"Partir"));
            leaveMember.enabled = edit != null && viewer.getName().equals(edit.getName());
            leaveMember.visible = !banMember.visible;
        }

        if(guild != null && viewer == null) {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        this.drawDefaultBackground();
        mc.getTextureManager().bindTexture(BACKGROUND);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        //On place le background
        drawScaledCustomSizeModalRect(startX, startY, 0, 0, 1080, 719,1080/backgroundDivide, 719/backgroundDivide, 1080,  719);

        int titleX = width / 2;
        int titleY = startY + 18;

        int colorTitle = new Color(20, 20, 20).getRGB();

        DecimalFormat decimalFormat = new DecimalFormat("##");

        //Guilde
        if(guild != null) {
            drawCenterStringWithoutShadow(guild.getName(), titleX, titleY-2, colorTitle);

            //Members
            drawMembers(titleY);

            //Sidebar top
            int sTopX = titleX + 8;
            int sTopY = titleY + 26;

            fontRenderer.drawString("Niveau: " + guild.getLevel(), sTopX, sTopY,  Color.BLACK.getRGB());
            fontRenderer.drawString("Membres: " + guild.getMembers().size() + "/10", sTopX, sTopY += fontRenderer.FONT_HEIGHT + 1,  Color.BLACK.getRGB());
            fontRenderer.drawString("Experience: " + decimalFormat.format(guild.getExperience())+"/"+decimalFormat.format(guild.getExperienceToUp()), sTopX, sTopY += fontRenderer.FONT_HEIGHT + 1,  Color.BLACK.getRGB());

            //Sidebar down
            int sBotX = titleX + 8;
            int sBotY = titleY + 78;

            if(type == GuiType.DESCRIPTION) {
                fontRenderer.drawString("Description:", sBotX, sBotY, Color.BLACK.getRGB());

                String[] descriptions = guild.getDescription().split(";");
                int descY = 0;
                for (String description : descriptions) {
                    List<String> lines = fontRenderer.listFormattedStringToWidth(description, 154);
                    for (String line : lines) {
                        if(descY < fontRenderer.FONT_HEIGHT * 12) {
                            fontRenderer.drawString(line, sBotX, sBotY + descY + 20, Color.BLACK.getRGB());
                            descY += fontRenderer.FONT_HEIGHT;
                        }
                    }
                }
            } else if(type == GuiType.ROLE) {
                fontRenderer.drawString("Rôle:", sBotX, sBotY, Color.BLACK.getRGB());
            } else {
                fontRenderer.drawString("Membre:", sBotX, sBotY, Color.BLACK.getRGB());

                fontRenderer.drawString(edit.getName(), sBotX, sBotY + 14, Color.BLACK.getRGB());
                GuildRole role = guild.getRole(edit.getIdRole());
                fontRenderer.drawString(role.getName(), sBotX, sBotY + 24, Color.RED.getRGB());
                fontRenderer.drawString("Exp: " + decimalFormat.format(edit.getExpGiven()), sBotX, sBotY + 34, Color.BLUE.getRGB());

            }
        }
        //Pas de guilde
        else {
            drawCenterStringWithoutShadow("Menu de Guilde", titleX, titleY-2, colorTitle);

            //Info principale
            fontRenderer.drawString( "Vous n'avez pas de guilde.", startX + 16, height / 2 - 2, -1);

            //Sidebar
            fontRenderer.drawString( "Aucune information a afficher.", titleX + 8, titleY + 32, -1);
            fontRenderer.drawString( "Aucune description a afficher.", titleX + 8, titleY + 128, -1);
        }


        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawCenterStringWithoutShadow(String str, int x,int y, int color) {
        fontRenderer.drawString(str, x - fontRenderer.getStringWidth(str)/2, y , color);
    }

    private void drawMembers(int titleY) {
        int i = 0;
        for (GuildMember member : guild.getMembers()) {

            int memberX = (width / 2 - (477 / backgroundDivide))-6;
            int memberY = titleY + 14 + i * 60/backgroundDivide;

            mc.getTextureManager().bindTexture(PLAYER_BORDER);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            //On place le background
            drawScaledCustomSizeModalRect(memberX, memberY, 0, 0, 477, 59, 477 / backgroundDivide, 59 / backgroundDivide, 477, 59);

            //Rendu du nom
            fontRenderer.drawString(member.getName(), memberX + 5, memberY += fontRenderer.FONT_HEIGHT/2+1, Color.BLACK.getRGB());

            //Rendu du rôle
            int roleX = memberX + fontRenderer.getStringWidth(member.getName()) + 13;
            GuildRole guildRole = guild.getRole(member.getIdRole());
            fontRenderer.drawString(guildRole.getName(), roleX, memberY+2, Color.GRAY.getRGB());

            //Le bouton ne se fait pas ici

            i++;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1 || keyCode == ClientProxy.keyBindings[0].getKeyCode())
        {
            this.mc.displayGuiScreen((GuiScreen)null);

            if (this.mc.currentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

        if(button.enabled) {
            switch (button.id) {
                case 0:
                    //Invitation d'un joueur
                    showMember(null); //reset
                    mc.displayGuiScreen(new GuiInvite(this));
                    break;
                case 1:
                    showRole();
                    break;
                case 2:
                    //Modifier la description
                    mc.displayGuiScreen(new GuiDescription(this, guild.getDescription()));
                    break;
                //Roles gestion
                case 3:
                    mc.displayGuiScreen(new GuiRole(this, true, 0));
                    break;
                case 4:
                    mc.displayGuiScreen(new GuiListRole(this));
                    break;
                case 5:
                    confirmClicked(false, 2);
                    break;
                //Membres gestion boutons
                case 6:
                    //On ferme l'interaction du membre
                    showMember(null);
                    break;
                case 7:
                    //Changer rôle
                    if (edit != null)
                        //Main.NETWORK.sendToServer(new SPacketSelectRoleMember(edit));
                        mc.displayGuiScreen(new GuiSelectRoleMember(this));
                    break;
                case 8:
                    //Exclure
                    if (edit != null)
                        Main.NETWORK.sendToServer(new SPacketKickMember(edit));
                    break;
                case 9:
                    //Bannir
                    if(banMember.visible) {
                        if (edit != null)
                            Main.NETWORK.sendToServer(new SPacketBanMember(edit));
                    }
                    if(leaveMember.visible) {
                        Main.NETWORK.sendToServer(new SPacketLeaveMember());
                    }
                    break;
                default:
                    //Gestion des membres
                    //[Idem que les rôles]
                    if (button instanceof GuiEditMemberButton)
                        Main.NETWORK.sendToServer(new SPacketOpenMember(((GuiEditMemberButton) button).getMember()));
                    break;
            }
        }
    }

    public void showRole() {
        //Affiche les boutons de role
        type = GuiType.ROLE;
        this.edit = null;
        mc.displayGuiScreen(this);
    }

    public void showMember(String member) {
        type = GuiType.MEMBER;

        this.edit = guild.getMember(member);
        //On reset
        if(edit == null) {
            type = GuiType.DESCRIPTION;
        }

        mc.displayGuiScreen(this);
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        super.confirmClicked(result, id);

        //Invitation
        if(id == 0) {
            type = GuiType.DESCRIPTION;
        }
        //Role
        if(id == 1) {
            type = GuiType.ROLE;
        }
        //Description
        if(id == 2) {
            type = GuiType.DESCRIPTION;
        }

        if(id == 3) {
            type = GuiType.MEMBER;
        }

        mc.displayGuiScreen(this);
    }

    public void allowChangeRole() {
        if(edit != null) {
            mc.displayGuiScreen(new GuiSelectRoleMember(this));
        }
    }

    private static enum GuiType {
        DESCRIPTION,
        ROLE,
        MEMBER
    }
}

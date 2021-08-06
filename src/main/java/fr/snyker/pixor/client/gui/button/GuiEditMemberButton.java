package fr.snyker.pixor.client.gui.button;

import fr.snyker.pixor.guild.GuildMember;

public class GuiEditMemberButton extends GuiEditButton {

    private final GuildMember member;

    public GuiEditMemberButton(int buttonId, int x, int y, GuildMember member) {
        super(buttonId, x, y);
        this.member = member;
    }

    public GuildMember getMember() {
        return member;
    }
}

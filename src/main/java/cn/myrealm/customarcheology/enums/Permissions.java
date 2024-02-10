package cn.myrealm.customarcheology.enums;

import org.bukkit.command.CommandSender;

/**
 * @author rzt1020
 */

public enum Permissions {
    // root
    ROOT("customarcheology."),
    // commands
    COMMAND(ROOT + "command."),
    COMMAND_HELP(COMMAND + "help"),
    COMMAND_RELOAD(COMMAND + "reload"),
    COMMAND_GIVE(COMMAND + "give"),
    COMMAND_ARCHIFY(COMMAND + "archify"),
    COMMAND_DEARCHIFY(COMMAND + "dearchify"),
    COMMAND_PLACE(COMMAND + "place"),
    // plays
    PLAY(ROOT + "play."),
    PLAY_ARCHEOLOGY(PLAY + "archeology"),
    PLAY_HIGHLIGHT(PLAY + "highlight");


    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return permission;
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(this.permission);
    }
}

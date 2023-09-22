package cn.myrealm.customarcheology.enums;

import org.bukkit.command.CommandSender;

/**
 * @author rzt1020
 */

public enum Permissions {
    // root
    ROOT("customarcheology."),
    //commands
    COMMAND(ROOT + "command."),
    HELP(COMMAND + "help"),
    RELOAD(COMMAND + "reload"),
    GIVE(COMMAND + "give"),
    ARCHIFY(COMMAND + "archify"),
    DEARCHIFY(COMMAND + "dearchify");


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

package cn.myrealm.customarcheology.utils.enums;

import org.bukkit.command.CommandSender;

/**
 * @author rzt10
 */

public enum Permissions {
    // root
    ROOT("customarcheology."),
    COMMAND(ROOT + "command."),
    // help command
    HELP(COMMAND + "help"),
    // give command
    GIVE(COMMAND + "give");


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

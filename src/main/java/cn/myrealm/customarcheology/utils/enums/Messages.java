package cn.myrealm.customarcheology.utils.enums;

import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.managers.managers.LanguageManager;

/**
 * @author rzt10
 */

public enum Messages {
    // information
    ENABLE_MESSAGE("info.enable-message"),
    RELOAD_SUCCESS("info.reload-success"),
    // errors
    ERROR_INCORRECT_COMMAND("error.incorrect-command"),
    ERROR_NO_PERMISSION("error.no-permission"),
    // command descriptions
    COMMAND_HELP("command.help"),
    COMMAND_RELOAD("command.reload"),
    COMMAND_GIVE("command.give"),
    // command help messages
    COMMAND_HELP_HEAD("command.help-head"),
    COMMAND_HELP_DETAIL("command.help-detail");

    private final String key;
    Messages(String key) {
        this.key = key;
    }

    public String getMessage(String... args) {
        LanguageManager languageManager = LanguageManager.getInstance();
        String message;
        try {
            message = languageManager.getMessage(key);
        } catch (Exception e) {
            CustomArcheology.plugin.getLogger().warning(e.getMessage());
            message = "&cMissing message for key: &4" + key;
        }

        for (int i = 0; i < args.length; i += 2) {
            String var = "{" + args[i] + "}";
            message = message.replace(var, args[i + 1]);
        }

        return message;
    }
}

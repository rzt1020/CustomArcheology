package cn.myrealm.customarcheology.enums;

import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.managers.managers.system.LanguageManager;

/**
 * @author rzt1020
 */

public enum Messages {
    // prefix
    PREFIX("prefix"),
    // information
    ENABLE_MESSAGE("info.enable-message"),
    RELOAD_SUCCESS("info.reload-success"),
    TEXTURE_PACK_CREATED("info.texture-pack-created"),
    WORLD_HEAD("info.world-head"),
    WORLD_DETAIL("info.world-detail"),
    BLOCK_LOADED("info.block-loaded"),
    LOOTTABLE_LOADED("info.loottable-loaded"),
    TOOL_LOADED("info.tool-loaded"),
    // errors
    ERROR_INCORRECT_COMMAND("error.incorrect-command"),
    ERROR_EXECUTING_COMMAND("error.executing-command"),
    ERROR_FAILED_TO_CREATE_TEXTURE_PACK("error.failed-to-create-texture-pack"),
    ERROR_WORLD_NOT_FOUND("error.world-not-found"),
    ERROR_BLOCK_NOT_FOUND("error.block-not-found"),
    ERROR_BIOMES_NOT_FOUND("error.biomes-not-found"),
    ERROR_TOOL_NOT_FOUND("error.tool-not-found"),
    ERROR_PLAYER_NOT_ONLINE("error.player-not-online"),
    // command descriptions
    COMMAND_HELP("command.help"),
    COMMAND_RELOAD("command.reload"),
    COMMAND_GIVE("command.give"),
    COMMAND_PLACE("command.place"),
    // command help messages
    COMMAND_HELP_HEAD("command.help-head"),
    COMMAND_HELP_DETAIL("command.help-detail"),
    COMMAND_ARCHIFY("command.archify"),
    COMMAND_DEARCHIFY("command.dearchify"),
    GAME_BRUSH_NO_PERMISSION("game.brush-no-permission"),
    GAME_CAN_NOT_BRUSH("game.can-not-brush"),
    GAME_ALREADY_BRUSHING("game.already-brushing"),
    GAME_GIVE("game.give"),
    GAME_PLACE("game.place");

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
            message = "Missing message for key: " + key;
        }

        for (int i = 0; i < args.length; i += 2) {
            String var = "{" + args[i] + "}";
            message = message.replace(var, args[i + 1]);
        }

        return message;
    }
    public String getMessageWithPrefix(String... args) {
        if (this.equals(Messages.PREFIX)) {
            return getMessage();
        }
        return Messages.PREFIX.getMessage() + getMessage(args);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}

package cn.myrealm.customarcheology.enums;

import cn.myrealm.customarcheology.CustomArcheology;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author rzt1020
 */

public enum Config {
    // log
    LOG_GENERATED_BLOCK("log-generated-block", false),
    // config_files
    CONFIG_FILES_GENERATE_DEFAULT_FILES("config-files.generate-default-files", true),
    CONFIG_FILES_LANGUAGE("config-files.language", "en_US"),
    // debug
    DEBUG("debug", false),
    // database
    USE_MYSQL("database.use-mysql", false),
    MYSQL_HOST("database.mysql.host", "localhost"),
    MYSQL_PORT("database.mysql.port", 3306),
    MYSQL_USER("database.mysql.user", "root"),
    MYSQL_PASSWORD("database.mysql.password", "root"),
    MYSQL_DATABASE("database.mysql.database", "minecraft"),
    // settings
    TOOL_START_CUSTOM_MODEL_DATA("settings.start-custom-model-data.tool", 10000),
    BLOCK_START_CUSTOM_MODEL_DATA("settings.start-custom-model-data.block", 10000),
    VISIBLE_DISTANCE("settings.visible-distance", 8),
    ITEM_SCALE("settings.item-scale", 0.5),
    BLOCK_SCALE("settings.block-scale", 0.25),
    BLOCK_SAVE("settings.block-save", "UUID"),
    HOOK_BETTERSTRUCTURES("settings.hook.betterstructures", true),
    // auto copy resource pack
    AUTO_COPY_RESOURCEPACK_ENABLED("auto-copy-resourcepack.enabled", true),
    AUTO_COPY_RESOURCEPACK_PLUGIN("auto-copy-resourcepack.plugin", "ItemsAdder"),
    AUTO_COPY_RESOURCEPACK_PATH("auto-copy-resourcepack.path", "/contents/customarcheology/resourcepack/"),
    // minecraft locate file
    CONFIG_FILES_MINECRAFT_LOCATE_FILE_ENABLED("config-files.minecraft-locate-file.enabled", true),
    CONFIG_FILES_MINECRAFT_LOCATE_FILE_GENERATE_NEW_ONE("config-files.minecraft-locate-file.generate-new-one", false),
    CONFIG_FILES_MINECRAFT_LOCATE_FILE_FILE("config-files.minecraft-locate-file.file", "zh_cn.json");

    private final String key;
    private final Object def;

    Config(String key, Object def) {
        this.key = key;
        this.def = def;
    }

    public String asString() {
        FileConfiguration config = CustomArcheology.plugin.getConfig();
        return config.getString(key, (String) def);
    }

    public int asInt() {
        FileConfiguration config = CustomArcheology.plugin.getConfig();
        return config.getInt(key, (Integer) def);
    }

    public boolean asBoolean() {
        FileConfiguration config = CustomArcheology.plugin.getConfig();
        return config.getBoolean(key, (Boolean) def);
    }

    public double asDouble() {
        FileConfiguration config = CustomArcheology.plugin.getConfig();
        return config.getDouble(key, (Double) def);
    }
}

package cn.myrealm.customarcheology.enums;

import cn.myrealm.customarcheology.CustomArcheology;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author rzt1020
 */

public enum Config {
    // config_files
    CONFIG_FILES_GENERATE_DEFAULT_FILES("config-files.generate-default-files", true),
    CONFIG_FILES_LANGUAGE("config-files.language", "en_US"),
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
    STRUCTURE_DISTANCE("settings.structure-distance", 16),
    ITEM_SCALE("settings.item-scale", 0.5),
    BLOCK_SCALE("settings.block-scale", 0.25),
    //symbols
    VANILLA_SYMBOL("settings.symbols.vanilla", "minecraft"),
    CUSTOM_TOOL_SYMBOL("settings.symbols.custom-tool", "ca_tool"),
    CUSTOM_BLOCK_SYMBOL("settings.symbols.custom-block", "ca_block"),
    MYTHICMOBS_SYMBOL("settings.symbols.mythicmobs", "mythicmobs"),
    ITEMSADDER_SYMBOL("settings.symbols.itemsadder", "itemsadder"),
    ORAXEN_SYMBOL("settings.symbols.oraxen", "oraxen"),
    MMOITEMS_SYMBOL("settings.symbols.mmoitems", "mmoitems"),
    ECO_SYMBOL("settings.symbols.eco", "eco"),
    NEIGEITEMS_SYMBOL("settings.symbols.neigeitems", "neigeitems"),
    // auto copy resource pack
    AUTO_COPY_RESOURCEPACK_ENABLED("auto-copy-resourcepack.enabled", true),
    AUTO_COPY_RESOURCEPACK_PLUGIN("auto-copy-resourcepack.plugin", "ItemsAdder"),
    AUTO_COPY_RESOURCEPACK_PATH("auto-copy-resourcepack.path", "/contents/customarcheology/resourcepack/");


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

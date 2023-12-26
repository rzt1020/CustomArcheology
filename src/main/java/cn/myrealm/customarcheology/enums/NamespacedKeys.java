package cn.myrealm.customarcheology.enums;

import cn.myrealm.customarcheology.CustomArcheology;
import org.bukkit.NamespacedKey;

/**
 * @author rzt1020
 */

public enum NamespacedKeys {
    // block
    ARCHIFY_ARRAY("archify_array"),
    ARCHEOLOGY_ARRAY("archeology_array"),
    ARCHEOLOGY_BLOCK_LOC("archeology_block_loc.{0}"),
    ARCHEOLOGY_BLOCK_ITEM("archeology_block_item.{0}"),
    // item
    IS_ARCHEOLOGY_ITEM("is_archeology_item"),
    ARCHEOLOGY_BLOCK_ID("archeology_block_id"),
    IS_ARCHEOLOGY_TOOL("is_archeology_tool"),
    ARCHEOLOGY_TOOL_ID("archeology_tool_id"),
    // other
    ARCHEOLOGY_EXECUTE_ACTIONS_SPAWN("archeology_execute_action_spawn"),
    ARCHEOLOGY_EXECUTE_ACTIONS_PICK("archeology_execute_action_pick"),
    ARCHEOLOGY_REAL_ITEM("archeology_real_item");

    private final String key;
    NamespacedKeys(String key) {
        this.key = key;
    }

    public NamespacedKey getNamespacedKey(String... args) {
        String key = this.key;
        for (int i = 0; i < args.length; i++) {
            key = key.replace("{" + i + "}", args[i]);
        }
        return new NamespacedKey(CustomArcheology.plugin, key);
    }
}

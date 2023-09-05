package cn.myrealm.customarcheology.enums;

import cn.myrealm.customarcheology.CustomArcheology;
import org.bukkit.NamespacedKey;

/**
 * @author rzt10
 */

public enum NamespacedKeys {
    // block
    ARCHEOLOGY_DATA("archeology_{0}"),
    // item
    IS_ARCHEOLOGY_ITEM("is_archeology_item"),
    ARCHEOLOGY_BLOCK_ID("archeology_block_id");

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

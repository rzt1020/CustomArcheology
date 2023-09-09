package cn.myrealm.customarcheology.managers.managers;


import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.managers.AbstractManager;
import cn.myrealm.customarcheology.mechanics.CustomLootTable;
import org.bukkit.loot.LootTables;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * @author rzt10
 */
public class LootManager extends AbstractManager {
    private static LootManager instance;
    public LootManager(JavaPlugin plugin) {
        super(plugin);
        instance = this;
    }

    public static LootManager getInstance() {
        return instance;
    }

    public CustomLootTable getCustomLootTable(String name) {
        if (name.startsWith(Config.VANILLA_SYMBOL.asString())) {
            LootTables tables = LootTables.valueOf(name.replace(Config.VANILLA_SYMBOL.asString(), "").toUpperCase());
            return new CustomLootTable(tables.getLootTable());
        }
        return null;
    }
}



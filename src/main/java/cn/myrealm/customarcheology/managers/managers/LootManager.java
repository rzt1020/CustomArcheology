package cn.myrealm.customarcheology.managers.managers;


import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.enums.Messages;
import cn.myrealm.customarcheology.managers.BaseManager;
import cn.myrealm.customarcheology.mechanics.CustomLootTable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.loot.LootTables;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author rzt1020
 */
public class LootManager extends BaseManager {
    private static LootManager instance;
    private Map<String, CustomLootTable> lootTableMap;
    public LootManager(JavaPlugin plugin) {
        super(plugin);
        instance = this;
    }

    public static LootManager getInstance() {
        return instance;
    }

    private static final String LOOT_TABLE_PATH = "plugins/CustomArcheology/loottables/";
    private static final String YML = ".yml";
    @Override
    protected void onInit() {
        lootTableMap = new HashMap<>(5);
        Path lootTableDirPath = Paths.get(LOOT_TABLE_PATH);
        if (Files.exists(lootTableDirPath)) {
            if (Files.isDirectory(lootTableDirPath)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(lootTableDirPath, "*" + YML)) {
                    for (Path entry : stream) {
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(entry.toFile());
                        if (config.contains("rewards")) {
                            String lootTableName = entry.getFileName().toString().replace(YML, "");
                            lootTableMap.put(lootTableName, new CustomLootTable(Objects.requireNonNull(config.getConfigurationSection("rewards"))));
                            Bukkit.getConsoleSender().sendMessage(Messages.LOOTTABLE_LOADED.getMessageWithPrefix("loottable-id", lootTableName));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                Files.createDirectories(lootTableDirPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public CustomLootTable getCustomLootTable(String name) {
        if (name.startsWith(Config.VANILLA_SYMBOL.asString())) {
            LootTables tables = LootTables.valueOf(name.replace(Config.VANILLA_SYMBOL.asString() + ":", "").toUpperCase());
            return new CustomLootTable(tables.getLootTable());
        } else if (lootTableMap.containsKey(name)) {
            return lootTableMap.get(name);
        }
        return null;
    }
}



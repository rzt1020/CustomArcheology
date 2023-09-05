package cn.myrealm.customarcheology.managers.managers;

import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.managers.AbstractManager;
import cn.myrealm.customarcheology.mechanics.ArcheologyBlock;
import cn.myrealm.customarcheology.utils.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

/**
 * @author rzt10
 */
public class BlockManager extends AbstractManager {
    private static BlockManager instance;
    private Map<String, ArcheologyBlock> blocksMap;
    public BlockManager(JavaPlugin plugin) {
        super(plugin);
        instance = this;
    }

    @Override
    public void onInit() {
        blocksMap = new HashMap<>(5);
        File[] files = new File(plugin.getDataFolder(), "blocks").listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.getName().endsWith(".yml")) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                ArcheologyBlock block = new ArcheologyBlock(config,  file.getName().replace(".yml", ""));
                if (block.isValid()) {
                    blocksMap.put(block.getName(), block);
                }
            }
        }
    }

    public static BlockManager getInstance() {
        return instance;
    }

    public Set<String> getBlocksName() {
        return blocksMap.keySet();
    }

    public boolean isBlockExists(String name) {
        return blocksMap.containsKey(name);
    }

    public ItemStack generateItemStack(String name, int amount) {
        return blocksMap.get(name).generateItemStack(amount);
    }
    public void placeBlock(String blockId, Location location) {
        blocksMap.get(blockId).placeBlock(location);
        Bukkit.getScheduler().runTaskLater(CustomArcheology.plugin, () -> {
            for (Entity entity : Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, 20, 20, 20)) {
                if (entity.getType().equals(EntityType.PLAYER)) {
                    PacketUtil.changeBlock((Player) entity, location, Material.BARRIER);
                }
            }
        }, 1);

    }

}

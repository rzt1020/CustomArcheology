package cn.myrealm.customarcheology.managers.managers;

import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.managers.AbstractManager;
import cn.myrealm.customarcheology.mechanics.ArcheologyBlock;
import cn.myrealm.customarcheology.mechanics.FakeTileBlock;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        if (!blocksMap.containsKey(blockId)) {
            return;
        }
        ArcheologyBlock block = blocksMap.get(blockId);
        block.placeBlock(location);
        Bukkit.getScheduler().runTaskLater(CustomArcheology.plugin, () -> registerNewBlock(blockId, location), 1);
    }
    public void registerNewBlock(String blockId, Location location) {
        if (!blocksMap.containsKey(blockId)) {
            return;
        }
        ArcheologyBlock block = blocksMap.get(blockId);
        location = location.getBlock().getLocation();
        ChunkManager chunkManager = ChunkManager.getInstance();
        Chunk chunk = location.getChunk();
        chunkManager.registerNewBlock(chunk, block, location);
    }

    public ArcheologyBlock getBlock(String blockId) {
        return blocksMap.get(blockId);
    }

    public void updateBlocks() {
        ChunkManager chunkManager = ChunkManager.getInstance();
        List<FakeTileBlock> fakeTileBlocks = chunkManager.getFakeTileBlocks();
        for (FakeTileBlock fakeTileBlock : fakeTileBlocks) {
            fakeTileBlock.placeBlock();
        }
    }

}

package cn.myrealm.customarcheology.managers.managers;

import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.SQLs;
import cn.myrealm.customarcheology.managers.AbstractManager;
import cn.myrealm.customarcheology.managers.managers.system.DatabaseManager;
import cn.myrealm.customarcheology.mechanics.ArcheologyBlock;
import cn.myrealm.customarcheology.mechanics.FakeTileBlock;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author rzt10
 */
public class BlockManager extends AbstractManager {
    private static BlockManager instance;
    private Map<String, ArcheologyBlock> blocksMap;
    private Map<UUID, Set<String>> worldBlocksMap;
    public BlockManager(JavaPlugin plugin) {
        super(plugin);
        instance = this;
    }

    @Override
    public void onInit() {
        blocksMap = new HashMap<>(5);
        worldBlocksMap = new HashMap<>(5);

        File blockFilesFolder = new File(plugin.getDataFolder(), "blocks");
        File[] files = blockFilesFolder.listFiles(file -> file.getName().endsWith(".yml"));

        if (files != null) {
            for (File file : files) {
                try {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    ArcheologyBlock block = new ArcheologyBlock(config, file.getName().replace(".yml", ""));
                    if (block.isValid()) {
                        blocksMap.put(block.getName(), block);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        DatabaseManager.getInstance().executeAsyncQuery(SQLs.QUERY_WORLD_TABLE.getSql(), new DatabaseManager.Callback<>() {
            @Override
            public void onSuccess(List<Map<String, Object>> results) {
                for (Map<String, Object> result : results) {
                    Object blockIdObj = result.get("block_id");
                    Object worldUidObj = result.get("world_uuid");

                    if (Objects.nonNull(blockIdObj) && Objects.nonNull(worldUidObj)) {
                        UUID uuid = UUID.fromString((String) worldUidObj);
                        String blockId = (String) blockIdObj;

                        worldBlocksMap.computeIfAbsent(uuid, k -> new HashSet<>()).add(blockId);
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void setWorldBlock(World world, String blockId) throws Exception {
        UUID worldUid = world.getUID();
        String uuid = worldUid.toString();

        if (blocksMap.containsKey(blockId)) {
            var worldBlocks = worldBlocksMap.computeIfAbsent(worldUid, k -> new HashSet<>());
            worldBlocks.add(blockId);

            String sql = SQLs.INSERT_WORLD_TABLE.getSql(uuid, blockId);
            DatabaseManager.getInstance().executeAsyncUpdate(sql);
        } else {
            throw new Exception("Block not found");
        }
    }

    public List<ArcheologyBlock> getBlocks(World world) {
        return Optional.ofNullable(worldBlocksMap.get(world.getUID()))
                .map(blockIds -> blockIds.stream()
                        .map(blocksMap::get)
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }

    public void removeWorldBlock(World world, String blockId) throws Exception {
        UUID worldUid = world.getUID();
        String uuid = worldUid.toString();

        if (blocksMap.containsKey(blockId)) {
            var worldBlocks = worldBlocksMap.get(worldUid);
            if (Objects.nonNull(worldBlocks)) {
                worldBlocks.remove(blockId);
            }

            String sql = SQLs.DELETE_WORLD_TABLE.getSql(uuid, blockId);
            DatabaseManager.getInstance().executeAsyncUpdate(sql);
        } else {
            throw new Exception("Block not found");
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

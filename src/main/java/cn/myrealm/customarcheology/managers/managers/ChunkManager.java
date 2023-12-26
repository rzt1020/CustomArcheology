package cn.myrealm.customarcheology.managers.managers;


import cn.myrealm.customarcheology.managers.AbstractManager;
import cn.myrealm.customarcheology.mechanics.cores.ArcheologyBlock;
import cn.myrealm.customarcheology.mechanics.ArcheologyChunkSpawner;
import cn.myrealm.customarcheology.mechanics.cores.FakeTileBlock;
import cn.myrealm.customarcheology.mechanics.cores.PersistentDataChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author rzt1020
 */
public class ChunkManager extends AbstractManager {
    private static ChunkManager instance;
    private Map<Chunk, PersistentDataChunk> loadedChunks;
    private BukkitRunnable loadUnloadTask;
    public ChunkManager(JavaPlugin plugin) {
        super(plugin);
        instance = this;
    }

    @Override
    protected void onInit() {
        loadedChunks = new HashMap<>(5);
        loadUnloadTask = new BukkitRunnable() {
            @Override
            public void run() {
                Set<Chunk> newChunks = Bukkit.getOnlinePlayers().stream()
                        .flatMap(player -> {
                            Location loc = player.getLocation();
                            return Stream.of(
                                    loc.clone().add(8, 0, 8).getChunk(),
                                    loc.clone().add(8, 0, -8).getChunk(),
                                    loc.clone().add(-8, 0, 8).getChunk(),
                                    loc.clone().add(-8, 0, -8).getChunk()
                            );
                        })
                        .collect(Collectors.toSet());

                Set<Chunk> toUnload = new HashSet<>(loadedChunks.keySet());
                toUnload.removeAll(newChunks);
                toUnload.forEach(instance::unloadChunk);

                newChunks.removeAll(loadedChunks.keySet());
                newChunks.forEach(instance::loadChunk);

                BlockManager.getInstance().updateBlocks();
            }
        };
        loadUnloadTask.runTaskTimer(plugin, 10, 20);
    }


    @Override
    protected void onDisable() {
        loadUnloadTask.cancel();
        for (PersistentDataChunk dataChunk : loadedChunks.values()) {
            dataChunk.saveChunk();
        }
    }
    public void removeBlock(Location location) {
        Chunk chunk = location.getChunk();
        if (chunkUnloaded(chunk)) {
            loadChunk(chunk);
        }
        loadedChunks.get(chunk).removeBlock(location);
    }
    public void registerNewBlock(Chunk chunk, ArcheologyBlock block, Location location) {
        if (chunkUnloaded(chunk)) {
            loadChunk(chunk);
        }
        PersistentDataChunk dataChunk = loadedChunks.get(chunk);
        dataChunk.registerNewBlock(block, location);

    }

    public void loadChunk(Chunk chunk) {
        PersistentDataChunk dataChunk = new PersistentDataChunk(chunk);
        new ArcheologyChunkSpawner(chunk, dataChunk);
        loadedChunks.put(chunk, dataChunk);
    }


    public void unloadChunk(Chunk chunk) {
        if (chunkUnloaded(chunk)) {
            return;
        }
        for (Entity entity : chunk.getEntities()) {
            if (entity.getType().equals(EntityType.PLAYER)) {
                return;
            }
        }
        loadedChunks.get(chunk).saveChunk();
        loadedChunks.remove(chunk);
    }
    public boolean chunkUnloaded(Chunk chunk) {
        return !loadedChunks.containsKey(chunk);
    }
    public static ChunkManager getInstance() {
        return instance;
    }

    public boolean isArcheologyBlock(Location location) {
        PersistentDataChunk dataChunk = getPersistentDataChunk(location);
        return dataChunk.isArcheologyBlock(location);
    }

    public ArcheologyBlock getArcheologyBlock(Location location) {
        PersistentDataChunk dataChunk = getPersistentDataChunk(location);
        return dataChunk.getArcheologyBlock(location);
    }

    public PersistentDataChunk getPersistentDataChunk(Location location) {
        location = location.getBlock().getLocation();
        Chunk chunk = location.getChunk();
        if (chunkUnloaded(chunk)) {
            loadChunk(chunk);
        }
        return loadedChunks.get(chunk);
    }


    public List<FakeTileBlock> getFakeTileBlocks() {
        List<FakeTileBlock> fakeTileBlocks = new ArrayList<>();
        for (PersistentDataChunk dataChunk : loadedChunks.values()) {
            fakeTileBlocks.addAll(dataChunk.getFakeTileBlocks());
        }
        return fakeTileBlocks;
    }

    public FakeTileBlock getFakeTileBlock(Location location) {
        Chunk chunk = location.getChunk();
        if (chunkUnloaded(chunk)) {
            loadChunk(chunk);
        }
        return loadedChunks.get(chunk).getFakeTileBlock(location);
    }


}

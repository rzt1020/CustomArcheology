package cn.myrealm.customarcheology.managers.managers;


import cn.myrealm.customarcheology.managers.AbstractManager;
import cn.myrealm.customarcheology.mechanics.ArcheologyBlock;
import cn.myrealm.customarcheology.mechanics.PersistentDataChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * @author rzt10
 */
public class ChunkManager extends AbstractManager {
    private static ChunkManager instance;
    private Map<Chunk, PersistentDataChunk> loadedChunks;
    private BukkitRunnable chunkUnloadTask;
    public ChunkManager(JavaPlugin plugin) {
        super(plugin);
        instance = this;
    }

    @Override
    protected void onInit() {
        chunkUnloadTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (Objects.isNull(loadedChunks) || loadedChunks.isEmpty()) {
                    return;
                }
                System.out.println(loadedChunks.keySet());
                List<Chunk> retainedChunks = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Chunk chunk = player.getLocation().getChunk();
                    if (loadedChunks.containsKey(chunk)) {
                        retainedChunks.add(chunk);
                    }
                }
                Set<Chunk> loadedChunksSet = new HashSet<>(loadedChunks.keySet());
                for (Chunk chunk : loadedChunksSet) {
                    if (!retainedChunks.contains(chunk)) {
                        loadedChunks.get(chunk).saveChunk();
                        loadedChunks.remove(chunk);
                    }
                }
            }
        };
        loadedChunks = new HashMap<>(5);
        chunkUnloadTask.runTaskTimer(plugin, 0, 100);
    }

    @Override
    protected void onDisable() {
        chunkUnloadTask.cancel();
        for (PersistentDataChunk dataChunk : loadedChunks.values()) {
            dataChunk.saveChunk();
        }
    }
    public void removeBlock(Location location) {
        Chunk chunk = location.getChunk();
        if (!chunkIsLoaded(chunk)) {
            loadChunk(chunk);
        }
        loadedChunks.get(chunk).removeBlock(location);
    }
    public void registerNewBlock(Chunk chunk, ArcheologyBlock block, Location location) {
        if (!chunkIsLoaded(chunk)) {
            loadChunk(chunk);
        }
        PersistentDataChunk dataChunk = loadedChunks.get(chunk);
        dataChunk.registerNewBlock(block, location);

    }

    public void loadChunk(Chunk chunk) {
        loadedChunks.put(chunk, new PersistentDataChunk(chunk));
    }

    public void unloadChunk(Chunk chunk) {
        if (!loadedChunks.containsKey(chunk)) {
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
    public boolean chunkIsLoaded(Chunk chunk) {
        return loadedChunks.containsKey(chunk);
    }
    public static ChunkManager getInstance() {
        return instance;
    }
}

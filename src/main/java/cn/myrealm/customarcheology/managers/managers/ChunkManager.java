package cn.myrealm.customarcheology.managers.managers;


import cn.myrealm.customarcheology.managers.AbstractManager;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rzt10
 */
public class ChunkManager extends AbstractManager {
    private static ChunkManager instance;
    private List<Chunk> loadedChunks;
    public ChunkManager(JavaPlugin plugin) {
        super(plugin);
        instance = this;
    }

    @Override
    protected void onInit() {
        loadedChunks = new ArrayList<>();
    }

    public void loadChunk(Chunk chunk) {
        loadedChunks.add(chunk);

    }

    public void unloadChunk(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (entity.getType().equals(EntityType.PLAYER)) {
                return;
            }
        }
        loadedChunks.remove(chunk);
    }

    public static ChunkManager getInstance() {
        return instance;
    }
}

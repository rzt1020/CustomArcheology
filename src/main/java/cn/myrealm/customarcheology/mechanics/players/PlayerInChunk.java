package cn.myrealm.customarcheology.mechanics.players;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.managers.managers.ChunkManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

/**
 * @author rzt10
 */
public class PlayerInChunk implements PlayerTask{
    private final Player player;
    private final BukkitRunnable inChunkTask;
    private Chunk inChunk;
    public PlayerInChunk(Player player) {
        this.player = player;
        inChunkTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateChunk();
            }
        };
        inChunkTask.runTaskTimer(CustomArcheology.plugin, 0, 10);
    }

    public void updateChunk() {
        Chunk chunk = player.getLocation().getChunk();
        if (Objects.isNull(inChunk)) {
            inChunkChange(null, chunk);
            inChunk = chunk;
            return;
        }
        if (!inChunk.equals(chunk)){
            inChunkChange(inChunk, chunk);
            inChunk = chunk;
        }
    }

    public void inChunkChange(Chunk before, Chunk after) {
        ChunkManager chunkManager = ChunkManager.getInstance();
        chunkManager.loadChunk(after);
        Bukkit.getScheduler().runTaskLater(CustomArcheology.plugin, () -> chunkManager.unloadChunk(before),200);
    }

    @Override
    public void cancelTask() {
        inChunkTask.cancel();
    }

}
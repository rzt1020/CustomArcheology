package cn.myrealm.customarcheology.mechanics;

import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Permissions;
import cn.myrealm.customarcheology.managers.managers.ChunkManager;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

/**
 * @author rzt1020
 */
public class PlayerLookAt {
    private final Player player;
    private Block lookAtBlock;
    private final BukkitRunnable lookAtBlockTask;
    private BukkitRunnable task;
    public PlayerLookAt(Player player) {
        this.player = player;
        lookAtBlockTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateLookAtBlock();
            }
        };
        lookAtBlockTask.runTaskTimer(CustomArcheology.plugin, 0, 1);
    }

    public void cancelTask() {
        lookAtBlockTask.cancel();
    }
    public void setTask(BukkitRunnable task) {
        this.task = task;
    }
    public void updateLookAtBlock() {
        Block block = player.getTargetBlock(null, 10);
        if (Objects.isNull(lookAtBlock)) {
            lookAtBlockChange(null, block);
            lookAtBlock = block;
            return;
        }
        if (!lookAtBlock.equals(block)) {
            lookAtBlockChange(lookAtBlock, block);
            lookAtBlock = block;
        }
    }
    public void lookAtBlockChange(Block before, Block after) {
        if (Objects.nonNull(task)) {
            task.run();
            task = null;
        }
    }
}

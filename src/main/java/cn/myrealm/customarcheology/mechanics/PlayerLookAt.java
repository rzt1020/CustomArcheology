package cn.myrealm.customarcheology.mechanics;

import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Config;
import org.bukkit.Bukkit;
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
        Block block = null;
        try {
            block = player.getTargetBlock(null, 10);
        } catch (Exception ignored) {
        }
        if (Objects.isNull(lookAtBlock)) {
            lookAtBlockChange();
            lookAtBlock = block;
            return;
        }
        if (!lookAtBlock.equals(block)) {
            lookAtBlockChange();
            lookAtBlock = block;
        }
    }
    public void lookAtBlockChange() {
        if (Objects.nonNull(task)) {
            if (Config.DEBUG.asBoolean()) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §fChange look at block.");
            }
            task.run();
            task = null;
        }
    }
}

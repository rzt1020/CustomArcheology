package cn.myrealm.customarcheology.mechanics.players;

import cn.myrealm.customarcheology.CustomArcheology;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

/**
 * @author rzt10
 */
public class PlayerLookAt implements PlayerTask {
    private final Player player;
    private Block lookAtBlock;
    private final BukkitRunnable lookAtBlockTask;
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

    @Override
    public void cancelTask() {
        lookAtBlockTask.cancel();
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
        if (after.getType().equals(Material.DIAMOND_BLOCK)) {
            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1);
            after.getWorld().spawnParticle(Particle.REDSTONE, after.getLocation().add(0.5, 0.5, 0.5), 10, 0.5, 0.5, 0.5, 0, dustOptions);
        }
    }
}

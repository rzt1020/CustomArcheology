package cn.myrealm.customarcheology.listeners.bukkit;


import cn.myrealm.customarcheology.listeners.AbstractListener;
import cn.myrealm.customarcheology.managers.managers.ChunkManager;
import cn.myrealm.customarcheology.utils.PacketUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.Objects;

/**
 * @author rzt1020
 */
public class BlockBreakListener extends AbstractListener {

    public BlockBreakListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        ChunkManager chunkManager = ChunkManager.getInstance();
        if (chunkManager.isArcheologyBlock(loc)) {
            chunkManager.removeBlock(loc);
        }
    }

    @EventHandler
    public void onBlockBreakByTnt(EntityExplodeEvent event) {
        ChunkManager chunkManager = ChunkManager.getInstance();
        for (Block block : event.blockList()) {
            Location loc = block.getLocation();
            if (chunkManager.isArcheologyBlock(loc)) {
                chunkManager.removeBlock(loc);
            }
        }
    }


    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Location loc = Objects.requireNonNull(event.getClickedBlock()).getLocation();
        ChunkManager chunkManager = ChunkManager.getInstance();
        if (chunkManager.isArcheologyBlock(loc)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                PacketUtil.changeBlock(Collections.singletonList(event.getPlayer()), loc, Material.BARRIER);
            },1);
        }
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.LEFT_CLICK_BLOCK) || !event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
            return;
        }
        Location loc = Objects.requireNonNull(event.getClickedBlock()).getLocation();
        ChunkManager chunkManager = ChunkManager.getInstance();
        if (chunkManager.isArcheologyBlock(loc)) {
            loc.getBlock().setType(Material.AIR);
            chunkManager.removeBlock(loc);
            event.getPlayer().playSound(event.getPlayer(), Sound.BLOCK_SUSPICIOUS_SAND_BREAK, 1, 1);
        }
    }
}

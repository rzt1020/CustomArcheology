package cn.myrealm.customarcheology.listeners.bukkit;


import cn.myrealm.customarcheology.listeners.AbstractListener;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import cn.myrealm.customarcheology.managers.managers.ChunkManager;
import cn.myrealm.customarcheology.utils.PacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * @author rzt10
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

}

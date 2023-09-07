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
    public static Map<Location, Integer> entityIdMap = new HashMap<>();
    public static void addEntityId(Location loc, int id) {
        entityIdMap.put(loc, id);
    }
    public static void removeEntityId(Location loc) {
        entityIdMap.remove(loc);
    }

    public BlockBreakListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        if (entityIdMap.containsKey(loc)) {
            int id = entityIdMap.get(loc);
            BlockManager blockManager = BlockManager.getInstance();
            blockManager.removeEntity(id, loc);
            ChunkManager chunkManager = ChunkManager.getInstance();
            chunkManager.removeBlock(loc);
            removeEntityId(loc);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        Location loc = Objects.requireNonNull(event.getClickedBlock()).getLocation();
        if (entityIdMap.containsKey(loc)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                PacketUtil.changeBlock(Collections.singletonList(event.getPlayer()), loc, Material.BARRIER);
            },1);
        }
    }

}

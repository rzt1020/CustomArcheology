package cn.myrealm.customarcheology.listeners.bukkit;


import cn.myrealm.customarcheology.listeners.AbstractListener;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

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
            removeEntityId(loc);
        }
    }

}

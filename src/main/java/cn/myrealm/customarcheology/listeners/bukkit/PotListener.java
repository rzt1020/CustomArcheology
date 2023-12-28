package cn.myrealm.customarcheology.listeners.bukkit;


import cn.myrealm.customarcheology.listeners.BaseListener;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author rzt1020
 */
public class PotListener extends BaseListener {
    public PotListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        assert event.getClickedBlock() != null;
        if (event.getClickedBlock().getType().equals(Material.DECORATED_POT)) {

        }
    }
}

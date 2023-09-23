package cn.myrealm.customarcheology.listeners.bukkit;

import cn.myrealm.customarcheology.listeners.AbstractListener;
import cn.myrealm.customarcheology.managers.managers.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author rzt1020
 */
public class PlayerJoinListener extends AbstractListener{
    public PlayerJoinListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        playerManager.playerJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerManager playerManager = PlayerManager.getInstance();
        playerManager.playerQuit(event.getPlayer());
    }
}

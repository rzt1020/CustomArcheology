package cn.myrealm.customarcheology.managers.managers;

import cn.myrealm.customarcheology.managers.AbstractManager;
import cn.myrealm.customarcheology.util.player.PlayerLookAt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PlayerManager extends AbstractManager {
    private Map<Player, PlayerLookAt> playerCacheMap;

    public PlayerManager(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onInit() {
        super.onInit();
        playerCacheMap = new HashMap<>();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            playerCacheMap.put(player,new PlayerLookAt(player));
        }
    }

    public PlayerLookAt getPlayerCache(Player player) {
        return playerCacheMap.get(player);
    }

    public void playerJoin(Player player) {
        playerCacheMap.put(player,new PlayerLookAt(player));
    }

    public void playerQuit(Player player) {
        if(playerCacheMap.containsKey(player)) {
            playerCacheMap.get(player).save();
            playerCacheMap.get(player).cancelTask();
            playerCacheMap.remove(player);
        }
    }
}

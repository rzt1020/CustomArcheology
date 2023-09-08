package cn.myrealm.customarcheology.utils;


import cn.myrealm.customarcheology.enums.Config;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author rzt10
 */
public class PlayerUtil {
    public static List<Player> getNearbyPlayers(Location location) {
        int visibleDistance = Config.VISIBLE_DISTANCE.asInt();
        Collection<Entity> entities = Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, visibleDistance, visibleDistance, visibleDistance);
        List<Player> players = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                players.add(player);
            }
        }
        return players;
    }
}

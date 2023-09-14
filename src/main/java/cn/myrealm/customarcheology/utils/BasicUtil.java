package cn.myrealm.customarcheology.utils;


import cn.myrealm.customarcheology.enums.Config;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rzt10
 */
public class BasicUtil {
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
    private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d+)\\s*~\\s*(\\d+)");
    public static Point parseRange(String input) {
        if (Objects.isNull(input)) {
            return null;
        }
        Matcher matcher = RANGE_PATTERN.matcher(input);

        if (matcher.find()) {
            int start = Integer.parseInt(matcher.group(1));
            int end = Integer.parseInt(matcher.group(2));

            return new Point(start, end);
        }

        return null;
    }

    public static Block getRandomBlock(Chunk chunk, Point range) {
        Random rand = new Random();

        int x = rand.nextInt(16);
        int z = rand.nextInt(16);

        int y = range.x + rand.nextInt(range.y - range.x + 1);

        World world = chunk.getWorld();

        int actualX = chunk.getX() * 16 + x;
        int actualZ = chunk.getZ() * 16 + z;

        return world.getBlockAt(actualX, y, actualZ);
    }
}

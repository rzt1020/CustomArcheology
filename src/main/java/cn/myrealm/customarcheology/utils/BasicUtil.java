package cn.myrealm.customarcheology.utils;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Config;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rzt1020
 */
public class BasicUtil {
    private BasicUtil() {
    }
    public static List<Player> getNearbyPlayers(Location location) {
        int visibleDistance = Config.VISIBLE_DISTANCE.asInt();
        Collection<Entity> entities = Objects.requireNonNull(location.getWorld()).getNearbyEntities(location, visibleDistance, visibleDistance, visibleDistance);
        List<Player> players = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity instanceof Player player) {
                players.add(player);
            }
        }
        return players;
    }
    private static final Pattern RANGE_PATTERN = Pattern.compile("(\\d+)(?:\\s*~\\s*(\\d+))?");

    public static Point parseRange(String input) {
        if (Objects.isNull(input)) {
            return null;
        }

        Matcher matcher = RANGE_PATTERN.matcher(input);

        if (matcher.find()) {
            int start = Integer.parseInt(matcher.group(1));
            int end;

            if (matcher.group(2) != null) {
                end = Integer.parseInt(matcher.group(2));
            } else {
                end = start;
            }
            return new Point(start, end);
        }

        return null;
    }

    public static Block getRandomBlock(Chunk chunk, Point range) {
        int x = CustomArcheology.RANDOM.nextInt(16);
        int z = CustomArcheology.RANDOM.nextInt(16);

        int y = range.x + CustomArcheology.RANDOM.nextInt(range.y - range.x + 1);

        World world = chunk.getWorld();

        int actualX = chunk.getX() * 16 + x;
        int actualZ = chunk.getZ() * 16 + z;

        return world.getBlockAt(actualX, y, actualZ);
    }

    public static int getRandomIntFromPoint(Point point) {
        return CustomArcheology.RANDOM.nextInt((point.y - point.x) + 1) + point.x;
    }

    public static Block getGaussianRandomBlock(Chunk chunk, Point range, double gaussianMean, double gaussianStdDev) {
        int yValue = (int) Math.round(gaussianMean + CustomArcheology.RANDOM.nextGaussian() * gaussianStdDev);
        if (yValue < chunk.getWorld().getMinHeight() || yValue > chunk.getWorld().getMaxHeight()  || yValue < range.x || yValue > range.y) {
            return getGaussianRandomBlock(chunk, range, gaussianMean, gaussianStdDev);
        }
        Block newBlock = getRandomBlock(chunk, range);
        return newBlock.getWorld().getBlockAt(newBlock.getX(), yValue, newBlock.getZ());
    }

    public static Block getGaussianRandomBlock(Location location, double structureStdDev) {
        int xValue = (int) Math.round(location.getBlockX() + CustomArcheology.RANDOM.nextGaussian() * structureStdDev),
            zValue = (int) Math.round(location.getBlockZ() + CustomArcheology.RANDOM.nextGaussian() * structureStdDev);
        Block newBlock = getRandomBlock(location.getChunk(),  new Point(location.getBlockY() - 5, location.getBlockY() + 5));
        return newBlock.getWorld().getBlockAt(xValue, newBlock.getY(), zValue);
    }

    public static YamlConfiguration stringToYaml(String yamlString) {
        YamlConfiguration yamlConfig = new YamlConfiguration();
        try {
            yamlConfig.loadFromString(yamlString);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        return yamlConfig;
    }
}

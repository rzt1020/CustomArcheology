package cn.myrealm.customarcheology.utils;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.managers.managers.system.LanguageManager;
import cn.myrealm.customarcheology.utils.hooks.MythicMobs;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pers.neige.neigeitems.utils.ItemUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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

    public static String getItemName(ItemStack displayItem) {
        if (displayItem == null || displayItem.getItemMeta() == null) {
            return "";
        }
        if (Bukkit.getPluginManager().isPluginEnabled("NeigeItems")) {
            return ItemUtils.getItemName(displayItem);
        }
        if (displayItem.getItemMeta().hasDisplayName()) {
            return displayItem.getItemMeta().getDisplayName();
        }
        StringBuilder result = new StringBuilder();
        for (String word : displayItem.getType().name().toLowerCase().split("_")) {
            if (!word.isEmpty()) {
                char firstChar = Character.toUpperCase(word.charAt(0));
                String restOfWord = word.substring(1);
                result.append(firstChar).append(restOfWord).append(" ");
            }
        }
        return result.toString();
    }

    public static void runAction(Player player, Location location, ItemStack reward, String action) {
        if (player != null) {
            action = action.replace("{player}", player.getName());
        }
        if (reward != null) {
            action = action.replace("{reward}", getItemName(reward));
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            action = PlaceholderAPI.setPlaceholders(player, action);
        }
        if (action.startsWith("sound: ") && player != null) {
            // By: iKiwo
            String soundData = action.substring(7);
            String[] soundParts = soundData.split(";;");
            if (soundParts.length >= 1) {
                String soundName = soundParts[0];
                float volume = 1.0f;
                float pitch = 1.0f;
                if (soundParts.length >= 2) {
                    try {
                        volume = Float.parseFloat(soundParts[1]);
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (soundParts.length >= 3) {
                    try {
                        pitch = Float.parseFloat(soundParts[2]);
                    } catch (NumberFormatException ignored) {
                    }
                }
                player.playSound(player.getLocation(), soundName, volume, pitch);
            }
        } else if (action.startsWith("message: ") && player != null) {
            player.sendMessage(LanguageManager.parseColor(action.substring(9)));
        } else if (action.startsWith("announcement: ")) {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            for (Player p : players) {
                p.sendMessage(LanguageManager.parseColor(action.substring(14)));
            }
        } else if (action.startsWith("effect: ") && player != null) {
            try {
                if (PotionEffectType.getByName(action.substring(8).split(";;")[0].toUpperCase()) != null) {
                    PotionEffect effect = new PotionEffect(PotionEffectType.getByName(action.split(";;")[0].toUpperCase()),
                            Integer.parseInt(action.substring(8).split(";;")[2]),
                            Integer.parseInt(action.substring(8).split(";;")[1]) - 1,
                            true,
                            true,
                            true);
                    player.addPotionEffect(effect);
                }
            }
            catch (ArrayIndexOutOfBoundsException ignored) {
            }
        } else if (action.startsWith("teleport: ") && player != null) {
            try {
                if (action.split(";;").length == 4) {
                    Location loc = new Location(Bukkit.getWorld(action.substring(10).split(";;")[0]),
                            Double.parseDouble(action.substring(10).split(";;")[1]),
                            Double.parseDouble(action.substring(10).split(";;")[2]),
                            Double.parseDouble(action.substring(10).split(";;")[3]),
                            player.getLocation().getYaw(),
                            player.getLocation().getPitch());
                    player.teleport(loc);
                }
                else if (action.split(";;").length == 6) {
                    Location loc = new Location(Bukkit.getWorld(action.split(";;")[0]),
                            Double.parseDouble(action.substring(10).split(";;")[1]),
                            Double.parseDouble(action.substring(10).split(";;")[2]),
                            Double.parseDouble(action.substring(10).split(";;")[3]),
                            Float.parseFloat(action.substring(10).split(";;")[4]),
                            Float.parseFloat(action.substring(10).split(";;")[5]));
                    player.teleport(loc);
                }
            }
            catch (ArrayIndexOutOfBoundsException ignored) {
            }
        } else if (action.startsWith("entity_spawn: ")) {
            EntityType entity = EntityType.valueOf(action.substring(14).toUpperCase());
            if (location.getWorld() != null && entity != null) {
                location.getWorld().spawnEntity(location, entity);
            }
        } else if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs") && action.startsWith("mythicmobs_spawn: ")) {
            try {
                if (action.substring(18).split(";;").length == 1) {
                    MythicMobs.summonMythicMobs(location,
                            action.substring(18).split(";;")[0],
                            1);
                }
                else if (action.substring(18).split(";;").length == 2) {
                    MythicMobs.summonMythicMobs(location,
                            action.substring(18).split(";;")[0],
                            Integer.parseInt(action.substring(18).split(";;")[1]));
                }
                else if (action.substring(18).split(";;").length == 5) {
                    World world = Bukkit.getWorld(action.substring(18).split(";;")[1]);
                    Location loc = new Location(world,
                            Double.parseDouble(action.substring(18).split(";;")[2]),
                            Double.parseDouble(action.substring(18).split(";;")[3]),
                            Double.parseDouble(action.substring(18).split(";;")[4])
                    );
                    MythicMobs.summonMythicMobs(loc,
                            action.substring(18).split(";;")[0],
                            1);
                }
                else if (action.substring(18).split(";;").length == 6) {
                    World world = Bukkit.getWorld(action.substring(18).split(";;")[2]);
                    Location loc = new Location(world,
                            Double.parseDouble(action.substring(18).split(";;")[3]),
                            Double.parseDouble(action.substring(18).split(";;")[4]),
                            Double.parseDouble(action.substring(18).split(";;")[5])
                    );
                    MythicMobs.summonMythicMobs(loc,
                            action.substring(18).split(";;")[0],
                            Integer.parseInt(action.substring(18).split(";;")[1]));
                }
            }
            catch (ArrayIndexOutOfBoundsException ignored) {
            }
        } else if (action.startsWith("console_command: ")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.substring(17));
        } else if (action.startsWith("player_command: ") && player != null) {
            Bukkit.dispatchCommand(player, action.substring(16));
        } else if (action.startsWith("op_command: ") && player != null) {
            boolean playerIsOp = player.isOp();
            try {
                player.setOp(true);
                Bukkit.dispatchCommand(player, action.substring(12));
            } finally {
                player.setOp(playerIsOp);
            }
        }
    }
}

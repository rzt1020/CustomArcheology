package cn.myrealm.customarcheology.mechanics;

import cn.myrealm.customarcheology.managers.managers.system.LanguageManager;
import cn.myrealm.customarcheology.utils.CommonUtil;
import cn.myrealm.customarcheology.utils.ItemUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

public class Action {
    public static void runAction(Player player, Location location, ItemStack reward, String action) {
        if (player != null) {
            action = action.replace("{player}", player.getName());
        }
        if (reward != null) {
            action = action.replace("{reward}", ItemUtil.getItemName(reward));
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
            PotionEffectType potionEffectType = PotionEffectType.getByName(action.substring(8).split(";;")[0].toUpperCase());
            if (potionEffectType != null) {
                PotionEffect effect = new PotionEffect(potionEffectType,
                        Integer.parseInt(action.substring(8).split(";;")[2]),
                        Integer.parseInt(action.substring(8).split(";;")[1]) - 1,
                        true,
                        true,
                        true);
                player.addPotionEffect(effect);
            }
        } else if (action.startsWith("teleport: ") && player != null) {
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
        } else if (action.startsWith("entity_spawn: ")) {
            if (action.split(";;").length == 1 && player != null) {
                EntityType entity = EntityType.valueOf(action.substring(14).split(";;")[0].toUpperCase());
                player.getLocation().getWorld().spawnEntity(player.getLocation(), entity);
            } else if (action.split(";;").length == 5) {
                World world = Bukkit.getWorld(action.substring(14).split(";;")[1]);
                Location tempLocation = new Location(world,
                        Double.parseDouble(action.substring(14).split(";;")[2]),
                        Double.parseDouble(action.substring(14).split(";;")[3]),
                        Double.parseDouble(action.substring(14).split(";;")[4]));
                EntityType entity = EntityType.valueOf(action.substring(14).split(";;")[0].toUpperCase());
                if (tempLocation.getWorld() == null) {
                    return;
                }
                location.getWorld().spawnEntity(location, entity);
            }
        } else if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs") && action.startsWith("mythicmobs_spawn: ")) {
            if (action.substring(18).split(";;").length == 1) {
                CommonUtil.summonMythicMobs(location,
                        action.substring(18).split(";;")[0],
                        1);
            }
            else if (action.substring(18).split(";;").length == 2) {
                CommonUtil.summonMythicMobs(location,
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
                CommonUtil.summonMythicMobs(loc,
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
                CommonUtil.summonMythicMobs(loc,
                        action.substring(18).split(";;")[0],
                        Integer.parseInt(action.substring(18).split(";;")[1]));
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

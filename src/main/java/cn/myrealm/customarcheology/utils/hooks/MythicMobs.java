package cn.myrealm.customarcheology.utils.hooks;

import io.lumine.mythic.api.items.ItemManager;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;


/**
 * @author rzt1020
 */
public class MythicMobs {
    private static final String PLUGIN_NAME = "MythicMobs";
    private MythicMobs() {
    }
    public static boolean isLoaded() {
        return Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME);
    }
    public static ItemStack getItemStackByItemIdentifier(String itemIdentifier) {
        if (!isLoaded()) {
            throw new IllegalStateException(PLUGIN_NAME + " is not loaded");
        }
       return MythicBukkit.inst().getItemManager().getItemStack(itemIdentifier);
    }

    public static void spawnMythicMobs(String mobType, Location location, int amount) {
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(mobType).orElse(null);
        if(mob != null){
            for (int i = 0; i < amount; i++) {
                mob.spawn(BukkitAdapter.adapt(location),1);
            }
        }
    }
}

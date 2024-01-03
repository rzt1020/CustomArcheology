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
    public static void summonMythicMobs(Location location, String mobID, int level) {
        try {
            MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(mobID).orElse(null);
            if (mob != null) {
                mob.spawn(BukkitAdapter.adapt(location), level);
            }
        }
        catch (NoClassDefFoundError ep) {
            io.lumine.xikage.mythicmobs.mobs.MythicMob mob = io.lumine.xikage.mythicmobs.MythicMobs.inst().getMobManager().getMythicMob(mobID);
            if (mob != null) {
                mob.spawn(io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter.adapt(location), level);
            }
        }
    }
}

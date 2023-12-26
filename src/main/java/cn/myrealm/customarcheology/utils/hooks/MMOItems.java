package cn.myrealm.customarcheology.utils.hooks;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
 * @author rzt1020
 */
public class MMOItems {
    private static final String PLUGIN_NAME = "MMOItems";
    private MMOItems() {
    }
    public static boolean isLoaded() {
        return Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME);
    }
    public static ItemStack getItemStackByItemIdentifier(String typeIdentifier, String itemIdentifier) {
        if (!isLoaded()) {
            throw new IllegalStateException(PLUGIN_NAME + " is not loaded");
        }
        if (net.Indyuce.mmoitems.MMOItems.plugin.getTypes().get(typeIdentifier) == null) {
            throw new IllegalStateException("Item type not found");
        } else if (net.Indyuce.mmoitems.MMOItems.plugin.getItem(typeIdentifier, itemIdentifier) == null) {
            throw new IllegalStateException("Item not found");
        }
        return net.Indyuce.mmoitems.MMOItems.plugin.getItem(itemIdentifier, itemIdentifier);
    }
}

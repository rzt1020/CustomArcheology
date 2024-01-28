package cn.myrealm.customarcheology.utils.hooks;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import pers.neige.neigeitems.manager.ItemManager;

import java.util.Objects;


/**
 * @author rzt1020
 */
public class NeigeItems {
    private static final String PLUGIN_NAME = "NeigeItems";
    private NeigeItems() {
    }
    public static boolean isLoaded() {
        return Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME);
    }
    public static ItemStack getItemStackByItemIdentifier(String itemIdentifier) {
        if (!isLoaded()) {
            throw new IllegalStateException(PLUGIN_NAME + " is not loaded");
        }
        if (Objects.nonNull(ItemManager.INSTANCE.getItemStack(itemIdentifier))) {
            return ItemManager.INSTANCE.getItemStack(itemIdentifier);
        } else {
            throw new IllegalStateException("Item not found");
        }
    }
}

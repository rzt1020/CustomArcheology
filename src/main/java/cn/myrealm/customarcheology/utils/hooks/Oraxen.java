package cn.myrealm.customarcheology.utils.hooks;

import dev.lone.itemsadder.api.CustomStack;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;


/**
 * @author rzt1020
 */
public class Oraxen {
    private static final String PLUGIN_NAME = "Oraxen";
    private Oraxen() {
    }
    public static boolean isLoaded() {
        return Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME);
    }
    public static ItemStack getItemStackByItemIdentifier(String itemIdentifier) {
        if (!isLoaded()) {
            throw new IllegalStateException(PLUGIN_NAME + " is not loaded");
        }

        ItemBuilder builder = OraxenItems.getItemById(itemIdentifier);
        if (Objects.nonNull(builder)) {
            return builder.build();
        } else {
            throw new IllegalStateException("Item not found");
        }
    }
}

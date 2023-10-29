package cn.myrealm.customarcheology.utils.hooks;


import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * @author rzt1020
 */
public class ItemsAdder {
    private static final String PLUGIN_NAME = "ItemsAdder";
    private ItemsAdder() {
    }
    public static boolean isLoaded() {
        return Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME);
    }
    public static ItemStack getItemStackByItemIdentifier(String itemIdentifier) {
        if (!isLoaded()) {
            throw new IllegalStateException(PLUGIN_NAME + " is not loaded");
        }

        CustomStack customStack = CustomStack.getInstance(itemIdentifier);
        if (Objects.nonNull(customStack)) {
            return customStack.getItemStack();
        } else {
            throw new IllegalStateException("Item not found");
        }
    }
}

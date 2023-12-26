package cn.myrealm.customarcheology.utils.hooks;

import com.willfp.eco.core.items.Items;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * @author rzt1020
 */
public class Eco {
    private static final String PLUGIN_NAME = "eco";
    private Eco() {
    }
    public static boolean isLoaded() {
        return Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME);
    }
    public static ItemStack getItemStackByItemIdentifier(String itemIdentifier) {
        if (!isLoaded()) {
            throw new IllegalStateException(PLUGIN_NAME + " is not loaded");
        }

        if (Objects.nonNull(Items.lookup(itemIdentifier).getItem())) {
            return Items.lookup(itemIdentifier).getItem();
        } else {
            throw new IllegalStateException("Item not found");
        }
    }
}

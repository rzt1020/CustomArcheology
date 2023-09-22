package cn.myrealm.customarcheology.utils;

import cn.myrealm.customarcheology.enums.Config;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * @author rzt10
 */
public class ItemUtil {
    private final static int SPLIT_NUM = 2;
    public static ItemStack getItemStack(String itemIdentifier) {
        String[] split = itemIdentifier.split(":");
        if (split.length != SPLIT_NUM) {
            return null;
        }
        if (Objects.equals(Config.VANILLA_SYMBOL.asString(), split[0])) {
            System.out.println(split[1].toUpperCase());
            return new ItemStack(Objects.requireNonNull(Material.getMaterial(split[1].toUpperCase())));
        }
        return null;
    }

    public static Material getMaterial(String itemIdentifier) {
        return Objects.requireNonNull(getItemStack(itemIdentifier)).getType();
    }

}

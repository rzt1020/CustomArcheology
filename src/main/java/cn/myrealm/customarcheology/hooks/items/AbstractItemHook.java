package cn.myrealm.customarcheology.hooks.items;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public abstract class AbstractItemHook {

    protected String pluginName;

    public AbstractItemHook(String pluginName) {
        this.pluginName = pluginName;
    }

    public abstract ItemStack getHookItemByID(String itemID);

    public ItemStack returnNullItem(String itemID) {
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: Can not get "
                + pluginName + " item: " + itemID + "!");
        return null;
    }

    public abstract String getIDByItemStack(ItemStack hookItem);

    public String getPluginName() {
        return pluginName;
    }
}

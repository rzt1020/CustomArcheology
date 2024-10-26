package cn.myrealm.customarcheology.hooks.items;

import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class ItemOraxenHook extends AbstractItemHook {

    public ItemOraxenHook() {
        super("Oraxen");
    }

    @Override
    public ItemStack getHookItemByID(String hookItemID) {
        ItemBuilder itemBuilder = OraxenItems.getItemById(hookItemID);
        if (itemBuilder == null) {
            return returnNullItem(hookItemID);
        }
        return itemBuilder.build();
    }

    @Override
    public String getIDByItemStack(ItemStack hookItem) {
        return OraxenItems.getIdByItem(hookItem);
    }
}

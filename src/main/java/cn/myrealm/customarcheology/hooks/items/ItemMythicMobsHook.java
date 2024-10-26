package cn.myrealm.customarcheology.hooks.items;

import cn.myrealm.customarcheology.utils.CommonUtil;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ItemMythicMobsHook extends AbstractItemHook {

    public int mythicMobsVersion = 0;

    public ItemMythicMobsHook() {
        super("MythicMobs");
        if (CommonUtil.getClass("io.lumine.mythic.bukkit.MythicBukkit")) {
            mythicMobsVersion = 5;
        } else if (CommonUtil.getClass("io.lumine.xikage.mythicmobs.MythicMobs")) {
            mythicMobsVersion = 4;
        }
    }

    @Override
    public ItemStack getHookItemByID(String hookItemID) {
        if (mythicMobsVersion == 5) {
            ItemStack mmItem = MythicBukkit.inst().getItemManager().getItemStack(hookItemID);
            if (mmItem == null) {
                return returnNullItem(hookItemID);
            }
            return mmItem;
        } else if (mythicMobsVersion == 4) {
            ItemStack mmItem = MythicMobs.inst().getItemManager().getItemStack(hookItemID);
            if (mmItem == null) {
                return returnNullItem(hookItemID);
            }
            return mmItem;
        }
        return null;
    }

    @Override
    public String getIDByItemStack(ItemStack hookItem) {
        if (mythicMobsVersion == 5) {
            return MythicBukkit.inst().getItemManager().getMythicTypeFromItem(hookItem);
        }
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: Your MythicMobs is too old, we can not parse the item from " +
                "old version of MythicMobs.");
        return null;
    }
}

package cn.myrealm.customarcheology.hooks;

import cn.myrealm.customarcheology.utils.CommonUtil;
import com.willfp.eco.core.items.Items;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.ArmorSets;
import com.willfp.ecoarmor.sets.ArmorSlot;
import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.EcoItems;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import pers.neige.neigeitems.manager.ItemManager;

public class ItemsHook {

    public static int mythicMobsVersion = 0;

    public static ItemStack getHookItem(String pluginName, String itemID) {
        if (!CommonUtil.checkPluginLoad(pluginName)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: Your server don't have " + pluginName +
                    " plugin, but your loot table config try use its hook!");
            return null;
        }
        switch (pluginName) {
            case "ItemsAdder":
                CustomStack customStack = CustomStack.getInstance(itemID);
                if (customStack == null) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: Can not get "
                            + pluginName + " item: " + itemID + "!");
                    return null;
                } else {
                    return customStack.getItemStack();
                }
            case "Oraxen":
                ItemBuilder itemBuilder = OraxenItems.getItemById(itemID);
                if (itemBuilder == null) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: Can not get "
                            + pluginName + " item: " + itemID + "!");
                    return null;
                } else {
                    return itemBuilder.build();
                }
            case "MMOItems":
                ItemStack resultItem = MMOItems.plugin.getItem(itemID.split(";;")[0], itemID.split(";;")[1]);
                if (resultItem == null) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: Can not get "
                            + pluginName + " item: " + itemID + "!");
                    return null;
                }
                return resultItem;
            case "EcoItems":
                EcoItem ecoItems = EcoItems.INSTANCE.getByID(itemID);
                if (ecoItems == null) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: Can not get "
                            + pluginName + " item: " + itemID + "!");
                    return null;
                } else {
                    return ecoItems.getItemStack();
                }
            case "EcoArmor":
                if (ArmorSets.getByID(itemID.split(";;")[0]) == null) {
                    return null;
                }
                ArmorSet armorSet = ArmorSets.getByID(itemID);
                if (armorSet == null) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: Can not get "
                                    + pluginName + " item: " + itemID + "!");
                    return null;
                }
                ArmorSlot armorSlot = ArmorSlot.getSlot(itemID.split(";;")[1].toUpperCase());
                if (armorSlot == null) {
                    Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: Can not get "
                                    + pluginName + " item: " + itemID + "!");
                    return null;
                }
                return armorSet.getItemStack(armorSlot);
            case "MythicMobs":
                if (mythicMobsVersion == 0) {
                    if (CommonUtil.getClass("io.lumine.mythic.bukkit.MythicBukkit")) {
                        mythicMobsVersion = 5;
                    } else if (CommonUtil.getClass("io.lumine.xikage.mythicmobs.MythicMobs")) {
                        mythicMobsVersion = 4;
                    }
                }
                if (mythicMobsVersion == 5) {
                    ItemStack mmItem = MythicBukkit.inst().getItemManager().getItemStack(itemID);
                    if (mmItem == null) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: Can not get "
                                + pluginName + " item: " + itemID + "!");
                        return null;
                    } else {
                        return mmItem;
                    }
                } else if (mythicMobsVersion == 4) {
                    ItemStack mmItem = MythicMobs.inst().getItemManager().getItemStack(itemID);
                    if (mmItem == null) {
                        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: Can not get "
                                + pluginName + " v4 item: " + itemID + "!");
                        return null;
                    } else {
                        return mmItem;
                    }
                } else {
                    return null;
                }
            case "eco":
                return Items.lookup(itemID).getItem();
            case "NeigeItems":
                return ItemManager.INSTANCE.getItemStack(itemID);
        }
        Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: You set hook plugin to "
                + pluginName + " in UI config, however for now FlipCard is not support it!");
        return null;
    }
}

package cn.myrealm.customarcheology.hooks;

import cn.myrealm.customarcheology.utils.CommonUtil;
import com.willfp.eco.core.items.Items;
import com.willfp.ecoarmor.sets.ArmorSet;
import com.willfp.ecoarmor.sets.ArmorSlot;
import com.willfp.ecoarmor.sets.ArmorUtils;
import com.willfp.ecoitems.items.EcoItem;
import com.willfp.ecoitems.items.ItemUtilsKt;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.th0rgal.oraxen.api.OraxenItems;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import pers.neige.neigeitems.manager.ItemManager;

public class CheckValidHook {

    public static String checkValid(String pluginName, ItemStack itemStack) {
        if (!CommonUtil.checkPluginLoad(pluginName)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: Your server don't have " + pluginName +
                    " plugin, but your loot table config try use its hook!");
            return null;
        }
        else if (pluginName.equals("ItemsAdder")) {
            CustomStack customStack = CustomStack.byItemStack(itemStack);
            if (customStack != null) {
                return customStack.getNamespacedID();
            }
            else {
                return null;
            }
        }
        else if (pluginName.equals("Oraxen")) {
            return OraxenItems.getIdByItem(itemStack);
        }
        else if (pluginName.equals("MMOItems")) {
            String tempVal1 = MMOItems.getID(itemStack);
            String tempVal2 = MMOItems.getTypeName(itemStack);
            if (tempVal1 == null || tempVal2 == null || tempVal1.isEmpty() || tempVal2.isEmpty()) {
                return null;
            }
            else {
                return tempVal2 + ";;" + tempVal1;
            }
        }
        else if (pluginName.equals("EcoItems")) {
            EcoItem tempVal1 = ItemUtilsKt.getEcoItem(itemStack);
            if (tempVal1 == null) {
                return null;
            }
            else {
                return tempVal1.getID();
            }
        }
        else if (pluginName.equals("EcoArmor")) {
            ArmorSet tempVal1 = ArmorUtils.getSetOnItem(itemStack);
            if (tempVal1 == null) {
                return null;
            }
            else {
                String tempVal2 = tempVal1.getId();
                ArmorSlot tempVal3 = ArmorSlot.getSlot(itemStack);
                if (tempVal3 == null) {
                    return null;
                }
                return tempVal2 + ";;" + tempVal3;
            }
        }
        else if (pluginName.equals("MythicMobs")) {
            return MythicBukkit.inst().getItemManager().getMythicTypeFromItem(itemStack);
        }
        else {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: You set hook plugin to "
                    + pluginName + " in loot table config, however for now SpinToWin is not support it!");
            return null;
        }
    }

    public static String[] checkValid(ItemStack itemStack) {
        if (CommonUtil.checkPluginLoad("ItemsAdder")) {
            CustomStack customStack = CustomStack.byItemStack(itemStack);
            if (customStack != null) {
                return new String[]{"ItemsAdder", customStack.getId()};
            }
        }
        if (CommonUtil.checkPluginLoad("Oraxen")) {
            String tempVal1 = OraxenItems.getIdByItem(itemStack);
            if (tempVal1 != null) {
                return new String[]{"Oraxen", tempVal1};
            }
        }
        if (CommonUtil.checkPluginLoad("MMOItems")) {
            String tempVal1 = MMOItems.getID(itemStack);
            String tempVal2 = MMOItems.getTypeName(itemStack);
            if (tempVal1 != null && !tempVal1.isEmpty() && tempVal2 != null && !tempVal2.isEmpty()) {
                return new String[]{"MMOItems", tempVal2 + ";;" + tempVal1};
            }
        }
        if (CommonUtil.checkPluginLoad("EcoItems")) {
            EcoItem tempVal1 = ItemUtilsKt.getEcoItem(itemStack);
            if (tempVal1 != null) {
                return new String[]{"EcoItems", tempVal1.getID()};
            }
        }
        if (CommonUtil.checkPluginLoad("EcoArmor")) {
            ArmorSet tempVal1 = ArmorUtils.getSetOnItem(itemStack);
            ArmorSlot tempVal2 = ArmorSlot.getSlot(itemStack);
            if (tempVal1 != null && tempVal2 != null) {
                return new String[]{"EcoArmor", tempVal1.getId() + tempVal2};
            }
        }
        if (CommonUtil.checkPluginLoad("eco")) {
            if (Items.getCustomItem(itemStack) != null) {
                return new String[]{"eco", Items.getCustomItem(itemStack).getKey().getKey()};
            }
        }
        if (CommonUtil.checkPluginLoad("MythicMobs")) {
            String tempVal1 = MythicBukkit.inst().getItemManager().getMythicTypeFromItem(itemStack);
            if (tempVal1 != null) {
                return new String[]{"MythicMobs", tempVal1};
            }
        }
        if (CommonUtil.checkPluginLoad("NeigeItems")) {
            if (ItemManager.INSTANCE.isNiItem(itemStack) != null) {
                return new String[]{"NeigeItems", ItemManager.INSTANCE.isNiItem(itemStack).getId()};
            }
        }
        return null;
    }
}

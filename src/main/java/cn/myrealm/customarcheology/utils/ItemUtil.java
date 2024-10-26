package cn.myrealm.customarcheology.utils;

import cn.myrealm.customarcheology.enums.NamespacedKeys;
import cn.myrealm.customarcheology.managers.managers.LocateManager;
import cn.myrealm.customarcheology.managers.managers.system.LanguageManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;
import pers.neige.neigeitems.utils.ItemUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author rzt10
 */
public class ItemUtil {
    private ItemUtil() {
    }

    public static String getItemName(ItemStack displayItem) {
        if (displayItem == null || displayItem.getItemMeta() == null) {
            return "ERROR: Unknown Item";
        }
        if (displayItem.getItemMeta().hasDisplayName()) {
            return displayItem.getItemMeta().getDisplayName();
        }
        if (LocateManager.enableThis() && LocateManager.instance != null) {
            return LocateManager.instance.getLocateName(displayItem);
        }
        if (CommonUtil.checkPluginLoad("NeigeItems")) {
            return ItemUtils.getItemName(displayItem);
        }
        return getItemNameWithoutVanilla(displayItem);
    }

    public static String getItemNameWithoutVanilla(ItemStack displayItem) {
        if (displayItem == null || displayItem.getItemMeta() == null) {
            return "ERROR: Unknown Item";
        }
        if (displayItem.getItemMeta().hasDisplayName()) {
            return displayItem.getItemMeta().getDisplayName();
        }
        StringBuilder result = new StringBuilder();
        for (String word : displayItem.getType().name().toLowerCase().split("_")) {
            if (!word.isEmpty()) {
                char firstChar = Character.toUpperCase(word.charAt(0));
                String restOfWord = word.substring(1);
                result.append(firstChar).append(restOfWord).append(" ");
            }
        }
        return result.toString();
    }

    public static ItemStack generateItemStack(Material material, int cmd, @Nullable String displayName, @Nullable List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        assert itemMeta != null;

        itemMeta.setCustomModelData(cmd);
        if (Objects.nonNull(displayName)) {
            itemMeta.setDisplayName(LanguageManager.parseColor("&r" + displayName));
        }
        if (Objects.nonNull(lore)) {
            itemMeta.setLore(lore.stream()
                    .map(line -> LanguageManager.parseColor("&r&f" + line))
                    .collect(Collectors.toList()));
        }
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static String getToolId(ItemStack tool) {
        assert tool.getItemMeta() != null;
        ItemMeta itemMeta = tool.getItemMeta();
        String toolId;
        if (itemMeta.getPersistentDataContainer().has(NamespacedKeys.IS_ARCHEOLOGY_TOOL.getNamespacedKey(), PersistentDataType.BOOLEAN) &&
                itemMeta.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_TOOL_ID.getNamespacedKey(), PersistentDataType.STRING)) {
            toolId = itemMeta.getPersistentDataContainer().get(NamespacedKeys.ARCHEOLOGY_TOOL_ID.getNamespacedKey(), PersistentDataType.STRING);
        } else {
            toolId = "brush";
        }
        return toolId;
    }
}

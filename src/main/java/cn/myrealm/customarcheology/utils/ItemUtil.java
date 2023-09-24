package cn.myrealm.customarcheology.utils;

import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.enums.NamespacedKeys;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import cn.myrealm.customarcheology.managers.managers.ToolManager;
import cn.myrealm.customarcheology.managers.managers.system.LanguageManager;
import cn.myrealm.customarcheology.utils.hooks.ItemsAdder;
import cn.myrealm.customarcheology.utils.hooks.Oraxen;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author rzt10
 */
public class ItemUtil {
    private ItemUtil() {
    }
    private final static int SPLIT_NUM = 2;
    public static ItemStack getItemStackByItemIdentifier(String itemIdentifier) {
        String[] split = itemIdentifier.split(":");
        if (split.length != SPLIT_NUM) {
            return null;
        }
        if (Objects.equals(Config.VANILLA_SYMBOL.asString(), split[0])) {
            return new ItemStack(Objects.requireNonNull(Material.getMaterial(split[1].toUpperCase())));
        }
        if (Objects.equals(Config.CUSTOM_BLOCK_SYMBOL.asString(), split[0])) {
            return BlockManager.getInstance().generateItemStack(split[1], 1);
        }
        if (Objects.equals(Config.CUSTOM_TOOL_SYMBOL.asString(), split[0])) {
            return ToolManager.getInstance().getTool(split[1]).generateItem();
        }
        if (Objects.equals(Config.ITEMSADDER_SYMBOL.asString(), split[0])) {
            return ItemsAdder.getItemStackByItemIdentifier(split[1]);
        }
        if (Objects.equals(Config.ORAXEN_SYMBOL.asString(), split[0])) {
            return Oraxen.getItemStackByItemIdentifier(split[1]);
        }
        return null;
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

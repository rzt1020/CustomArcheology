package cn.myrealm.customarcheology.managers.managers;

import cn.myrealm.customarcheology.hooks.items.*;
import cn.myrealm.customarcheology.hooks.protection.*;
import cn.myrealm.customarcheology.managers.BaseManager;
import cn.myrealm.customarcheology.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class HookManager extends BaseManager {

    public static HookManager instance;

    private Map<String, AbstractItemHook> itemHooks;

    private Map<String, AbstractProtectionHook> protectionHooks;

    public HookManager(JavaPlugin plugin) {
        super(plugin);
        instance = this;
    }

    public static HookManager getHookManager() {
        return instance;
    }

    @Override
    protected void onInit() {
        itemHooks = new HashMap<>();
        if (CommonUtil.checkPluginLoad("ItemsAdder")) {
            registerNewItemHook("ItemsAdder", new ItemItemsAdderHook());
        }
        if (CommonUtil.checkPluginLoad("Oraxen")) {
            registerNewItemHook("Oraxen", new ItemOraxenHook());
        }
        if (CommonUtil.checkPluginLoad("MMOItems")) {
            registerNewItemHook("MMOItems", new ItemMMOItemsHook());
        }
        if (CommonUtil.checkPluginLoad("EcoItems")) {
            registerNewItemHook("EcoItems", new ItemEcoItemsHook());
        }
        if (CommonUtil.checkPluginLoad("EcoArmor")) {
            registerNewItemHook("EcoArmor", new ItemEcoArmorHook());
        }
        if (CommonUtil.checkPluginLoad("MythicMobs")) {
            registerNewItemHook("MythicMobs", new ItemMythicMobsHook());
        }
        if (CommonUtil.checkPluginLoad("eco")) {
            registerNewItemHook("eco", new ItemecoHook());
        }
        if (CommonUtil.checkPluginLoad("NeigeItems")) {
            registerNewItemHook("NeigeItems", new ItemNeigeItemsHook());
        }
        if (CommonUtil.checkPluginLoad("ExecutableItems")) {
            registerNewItemHook("ExecutableItems", new ItemExecutableItemsHook());
        }
        if (CommonUtil.checkPluginLoad("Nexo")) {
            registerNewItemHook("Nexo", new ItemNexoHook());
        }

        protectionHooks = new HashMap<>();
        if (CommonUtil.checkPluginLoad("WorldGuard")) {
            registerNewProtectionHook("WorldGuard", new ProtectionWorldGuardHook());
        }
        if (CommonUtil.checkPluginLoad("Residence")) {
            registerNewProtectionHook("Residence", new ProtectionResidenceHook());
        }
        if (CommonUtil.checkPluginLoad("GriefPrevention")) {
            registerNewProtectionHook("GriefPrevention", new ProtectionGriefPreventionHook());
        }
        if (CommonUtil.checkPluginLoad("Lands")) {
            registerNewProtectionHook("Lands", new ProtectionLandsHook());
        }
        if (CommonUtil.checkPluginLoad("HuskTowns")) {
            registerNewProtectionHook("HuskTowns", new ProtectionHuskTownsHook());
        }
        if (CommonUtil.checkPluginLoad("HuskClaims")) {
            registerNewProtectionHook("HuskClaims", new ProtectionHuskClaimsHook());
        }
        if (CommonUtil.checkPluginLoad("PlotSquared")) {
            registerNewProtectionHook("PlotSquared", new ProtectionPlotSquaredHook());
        }
        if (CommonUtil.checkPluginLoad("Towny")) {
            registerNewProtectionHook("Towny", new ProtectionTownyHook());
        }
        if (CommonUtil.checkPluginLoad("BentoBox")) {
            registerNewProtectionHook("BentoBox", new ProtectionBentoBoxHook());
        }
        if (CommonUtil.checkPluginLoad("Dominion")) {
            registerNewProtectionHook("Dominion", new ProtectionDominionHook());
        }
    }

    public void registerNewItemHook(String pluginName,
                                    AbstractItemHook itemHook) {
        if (!itemHooks.containsKey(pluginName)) {
            itemHooks.put(pluginName, itemHook);
        }
    }

    public void registerNewProtectionHook(String pluginName,
                                          AbstractProtectionHook protectionHook) {
        if (!protectionHooks.containsKey(pluginName)) {
            protectionHooks.put(pluginName, protectionHook);
        }
    }

    public ItemStack getHookItem(String pluginName, String itemID) {
        if (!itemHooks.containsKey(pluginName)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: Can not hook into "
                    + pluginName + " plugin, maybe we do not support this plugin, or your server didn't correctly load " +
                    "this plugin!");
            return null;
        }
        AbstractItemHook itemHook = itemHooks.get(pluginName);
        return itemHook.getHookItemByID(itemID);
    }

    public String getHookItemID(String pluginName, ItemStack hookItem) {
        if (!hookItem.hasItemMeta()) {
            return null;
        }
        if (!itemHooks.containsKey(pluginName)) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §cError: Can not hook into "
                    + pluginName + " plugin, maybe we do not support this plugin, or your server didn't correctly load " +
                    "this plugin!");
            return null;
        }
        AbstractItemHook itemHook = itemHooks.get(pluginName);
        return itemHook.getIDByItemStack(hookItem);
    }

    public String[] getHookItemPluginAndID(ItemStack hookItem) {
        for (AbstractItemHook itemHook : itemHooks.values()) {
            String itemID = itemHook.getIDByItemStack(hookItem);
            if (itemID != null) {
                return new String[]{itemHook.getPluginName(), itemHook.getIDByItemStack(hookItem)};
            }
        }
        return null;
    }

    public boolean getProtectionCanBreak(Player player, Location location) {
        if (player.isOp() || player.hasPermission("customarcheology.bypass.protection")) {
            return true;
        }
        for (AbstractProtectionHook protectionHook : protectionHooks.values()) {
            if (!protectionHook.canBreak(player, location)) {
                return false;
            }
        }
        return true;
    }

    public boolean getProtectionCanPlace(Player player, Location location) {
        if (player.isOp() || player.hasPermission("customarcheology.bypass.protection")) {
            return true;
        }
        for (AbstractProtectionHook protectionHook : protectionHooks.values()) {
            if (!protectionHook.canPlace(player, location)) {
                return false;
            }
        }
        return true;
    }
}

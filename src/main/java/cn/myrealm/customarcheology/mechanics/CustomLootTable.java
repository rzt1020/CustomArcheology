package cn.myrealm.customarcheology.mechanics;

import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.NamespacedKeys;
import cn.myrealm.customarcheology.mechanics.persistent_data.ItemStackTagType;
import cn.myrealm.customarcheology.mechanics.persistent_data.StringArrayTagType;
import cn.myrealm.customarcheology.utils.CommonUtil;
import cn.myrealm.customarcheology.utils.Item.BuildItem;
import cn.myrealm.customarcheology.utils.Item.DebuildItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author rzt1020
 */
public class CustomLootTable {

    private static final String MATERIAL = "reward";
    private static final String DISPLAY = "display-item";
    private static final String AMOUNT = "amount";
    private static final String CHANCE = "chance";
    private static final String ACTIONS = "actions";
    private static final String SPAWN_ACTIONS = "spawn-actions";

    private final List<Reward> rewardsList;

    public CustomLootTable(LootTable lootTable) {
        Location location = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        this.rewardsList = lootTable.populateLoot(CustomArcheology.RANDOM, new LootContext.Builder(location).build())
                .stream().map(Reward::new).collect(Collectors.toList());
    }

    public CustomLootTable(ConfigurationSection config) {
        this.rewardsList = config.getKeys(false).stream()
                .map(config::getConfigurationSection)
                .filter(Objects::nonNull)
                .map(this::loadReward)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Reward loadReward(ConfigurationSection section) {
        ConfigurationSection itemIdentifier = section.getConfigurationSection(MATERIAL);
        ConfigurationSection displayIdentifier = section.getConfigurationSection(DISPLAY);
        Point amount = CommonUtil.parseRange(section.getString(AMOUNT, "1"));
        if (amount == null) {
            amount = new Point(1, 1);
        }
        List<String> actions = section.getStringList(ACTIONS);
        if (actions.isEmpty()) {
            actions = null;
        }
        List<String> spawnActions = section.getStringList(SPAWN_ACTIONS);
        if (spawnActions.isEmpty()) {
            spawnActions = null;
        }
        int chance = section.getInt(CHANCE, 1);
        if (Objects.isNull(itemIdentifier)) {
            if (Objects.isNull(displayIdentifier)) {
                return null;
            } else {
                return new Reward(displayIdentifier, null, actions, spawnActions, amount, chance);
            }
        }
        else {
            return new Reward(itemIdentifier, displayIdentifier, actions, spawnActions, amount, chance);
        }
    }

    public ItemStack generateItem() {
        Reward reward = Reward.randomReward(rewardsList);
        if (Objects.isNull(reward)) {
            return null;
        }
        return reward.generateItem();
    }

    public static class Reward {
        private final ConfigurationSection realItem;
        private final ConfigurationSection displayItem;
        private final Point amount;
        private final int chance;
        private final List<String> actions;
        private final List<String> spawnActions;

        public Reward(@NotNull ConfigurationSection realItem,
                      @Nullable ConfigurationSection displayItem,
                      @Nullable List<String> actions,
                      @Nullable List<String> spawnActions,
                      @NotNull Point amount,
                      int chance) {
            this.realItem = realItem;
            this.displayItem = displayItem;
            this.amount = amount;
            this.chance = chance;
            this.actions = actions;
            this.spawnActions = spawnActions;
        }

        public Reward(ItemStack itemStack) {
            this(DebuildItem.debuildItem(itemStack, new MemoryConfiguration()), null, null, null, new Point(itemStack.getAmount(), itemStack.getAmount()), 1);
        }


        public ItemStack generateItem() {
            ItemStack realItemStack = BuildItem.buildItemStack(realItem);
            int amountValue = CommonUtil.getRandomIntFromPoint(this.amount);
            realItemStack.setAmount(amountValue);
            if (Objects.nonNull(displayItem)) {
                ItemStack displayItemStack = BuildItem.buildItemStack(displayItem);
                Objects.requireNonNull(displayItemStack.getItemMeta());
                ItemMeta itemMeta = displayItemStack.getItemMeta();
                itemMeta.getPersistentDataContainer().set(NamespacedKeys.ARCHEOLOGY_REAL_ITEM.getNamespacedKey(), new ItemStackTagType(), realItemStack);
                displayItemStack.setItemMeta(itemMeta);
                if (Objects.nonNull(spawnActions)) {
                    itemMeta.getPersistentDataContainer().set(NamespacedKeys.ARCHEOLOGY_EXECUTE_ACTIONS_SPAWN.getNamespacedKey(), new StringArrayTagType(StandardCharsets.UTF_8), actions.toArray(new String[0]));
                    realItemStack.setItemMeta(itemMeta);
                }
                if (Objects.nonNull(actions)) {
                    Objects.requireNonNull(realItemStack.getItemMeta());
                    ItemMeta realItemMeta = realItemStack.getItemMeta();
                    realItemMeta.getPersistentDataContainer().set(NamespacedKeys.ARCHEOLOGY_EXECUTE_ACTIONS_PICK.getNamespacedKey(), new StringArrayTagType(StandardCharsets.UTF_8), spawnActions.toArray(new String[0]));
                    realItemStack.setItemMeta(itemMeta);
                }
                return displayItemStack;
            }
            if (Objects.nonNull(actions)) {
                Objects.requireNonNull(realItemStack.getItemMeta());
                ItemMeta itemMeta = realItemStack.getItemMeta();
                itemMeta.getPersistentDataContainer().set(NamespacedKeys.ARCHEOLOGY_EXECUTE_ACTIONS_PICK.getNamespacedKey(), new StringArrayTagType(StandardCharsets.UTF_8), actions.toArray(new String[0]));
                realItemStack.setItemMeta(itemMeta);
            }
            if (Objects.nonNull(spawnActions)) {
                Objects.requireNonNull(realItemStack.getItemMeta());
                ItemMeta itemMeta = realItemStack.getItemMeta();
                itemMeta.getPersistentDataContainer().set(NamespacedKeys.ARCHEOLOGY_EXECUTE_ACTIONS_SPAWN.getNamespacedKey(), new StringArrayTagType(StandardCharsets.UTF_8), spawnActions.toArray(new String[0]));
                realItemStack.setItemMeta(itemMeta);
            }
            return realItemStack;
        }

        public int getChance() {
            return chance;
        }

        public static Reward randomReward(List<Reward> rewardsList) {
            int totalChance = rewardsList.stream()
                    .mapToInt(Reward::getChance)
                    .sum();
            if (totalChance <= 0) {
                return rewardsList.get(0);
            }
            int randomChance = CustomArcheology.RANDOM.nextInt(totalChance);
            int accumulatedChance = 0;

            for (Reward reward : rewardsList) {
                accumulatedChance += reward.getChance();
                if (randomChance < accumulatedChance) {
                    return reward;
                }
            }
            return null;
        }
    }
}

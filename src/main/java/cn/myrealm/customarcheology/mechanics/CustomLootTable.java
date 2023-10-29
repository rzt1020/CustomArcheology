package cn.myrealm.customarcheology.mechanics;

import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.enums.NamespacedKeys;
import cn.myrealm.customarcheology.mechanics.persistent_data.ItemStackTagType;
import cn.myrealm.customarcheology.mechanics.persistent_data.StringArrayTagType;
import cn.myrealm.customarcheology.utils.BasicUtil;
import cn.myrealm.customarcheology.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author rzt1020
 */
public class CustomLootTable {

    private static final String MATERIAL = "material";
    private static final String DISPLAY = "display";
    private static final String AMOUNT = "amount";
    private static final String CHANCE = "chance";
    private static final String COMMANDS = "commands";

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
        String itemIdentifier = section.getString(MATERIAL, null);
        String displayIdentifier = section.getString(DISPLAY, null);
        Point amount = BasicUtil.parseRange(section.getString(AMOUNT, "1"));
        List<String> commands = section.getStringList(COMMANDS);
        int chance = section.getInt(CHANCE, 1);
        if (Objects.nonNull(itemIdentifier)) {
            if (Objects.nonNull(displayIdentifier)) {
                return new Reward(itemIdentifier, displayIdentifier, amount, chance);
            } else {
                return new Reward(itemIdentifier, amount, chance);
            }
        } else if (Objects.nonNull(displayIdentifier)) {
            return new Reward(displayIdentifier, commands, amount, chance);
        }

        return null;
    }

    public ItemStack generateItem() {
        Reward reward = Reward.randomReward(rewardsList);
        if (Objects.isNull(reward)) {
            return null;
        }
        return reward.generateItem();
    }

    public static class Reward {
        private final String realItem;
        private final String displayItem;
        private final Point amount;
        private final int chance;
        private final List<String> commands;

        Reward(String realItem, Point amount, int chance) {
            this.realItem = realItem;
            this.displayItem = null;
            this.amount = amount;
            this.chance = chance;
            this.commands = null;
        }

        Reward(String realItem, String displayItem, Point amount, int chance) {
            this.realItem = realItem;
            this.displayItem = displayItem;
            this.amount = amount;
            this.chance = chance;
            this.commands = null;
        }

        Reward(String realItem, List<String> commands, Point amount, int chance) {
            this.realItem = realItem;
            this.displayItem = null;
            this.amount = amount;
            this.chance = chance;
            this.commands = commands;
        }

        public Reward(ItemStack itemStack) {
            this(Config.VANILLA_SYMBOL.asString() + ":" + itemStack.getType(), new Point(itemStack.getAmount(), itemStack.getAmount()), 1);
        }


        public ItemStack generateItem() {
            ItemStack realItemStack = ItemUtil.getItemStackByItemIdentifier(realItem);
            if (Objects.isNull(realItemStack)) {
                return null;
            }
            int amountValue = BasicUtil.getRandomIntFromPoint(this.amount);
            realItemStack.setAmount(amountValue);
            if (Objects.nonNull(displayItem)) {
                ItemStack displayItemStack = ItemUtil.generateVanillaEntityItemStack(displayItem);
                Objects.requireNonNull(displayItemStack.getItemMeta());
                ItemMeta itemMeta = displayItemStack.getItemMeta();
                itemMeta.getPersistentDataContainer().set(NamespacedKeys.ARCHEOLOGY_REAL_ITEM.getNamespacedKey(), new ItemStackTagType(), realItemStack);
                displayItemStack.setItemMeta(itemMeta);
                return displayItemStack;
            }
            if (Objects.nonNull(commands)) {
                Objects.requireNonNull(realItemStack.getItemMeta());
                ItemMeta itemMeta = realItemStack.getItemMeta();
                itemMeta.getPersistentDataContainer().set(NamespacedKeys.ARCHEOLOGY_EXECUTE_COMMAND.getNamespacedKey(), new StringArrayTagType(StandardCharsets.UTF_8), commands.toArray(new String[0]));
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

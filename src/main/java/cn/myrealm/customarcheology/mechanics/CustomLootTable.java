package cn.myrealm.customarcheology.mechanics;

import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.utils.BasicUtil;
import cn.myrealm.customarcheology.utils.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author rzt1020
 */
public class CustomLootTable {

    private static final String MATERIAL = "material";
    private static final String AMOUNT = "amount";
    private static final String CHANCE = "chance";

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
        Point amount = BasicUtil.parseRange(section.getString(AMOUNT, "1"));
        int chance = section.getInt(CHANCE, 1);
        if (Objects.nonNull(itemIdentifier)) {
            return new Reward(ItemUtil.getItemStack(itemIdentifier), amount, chance);
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
        private final ItemStack itemStack;
        private final Point amount;
        private final int chance;

        Reward(ItemStack itemStack, Point amount, int chance) {
            this.itemStack = itemStack;
            this.amount = amount;
            this.chance = chance;
        }

        Reward(ItemStack itemStack) {
            this(itemStack, new Point(itemStack.getAmount(), itemStack.getAmount()), 1);
        }

        public ItemStack generateItem() {
            ItemStack clonedItemStack = itemStack.clone();
            int amountValue = BasicUtil.getRandomIntFromPoint(this.amount);
            clonedItemStack.setAmount(amountValue);
            return clonedItemStack;
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

package cn.myrealm.customarcheology.mechanics;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.utils.BasicUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author rzt10
 */
public class CustomLootTable {
    private final boolean isVanilla;
    private final List<Reward> rewardsList =  new ArrayList<>();
    private final ConfigurationSection config;
    public CustomLootTable(LootTable lootTable) {
        Location location = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        List<ItemStack> rewards = new ArrayList<> (lootTable.populateLoot(CustomArcheology.RANDOM, new LootContext.Builder(location).build()));
        rewards.forEach(item -> {
            rewardsList.add(new Reward(item));
        });
        config = null;
        isVanilla = true;
    }

    private static final String MATERIAL = "material";
    private static final String AMOUNT = "amount";
    private static final String CHANCE = "chance";

    public CustomLootTable(ConfigurationSection config) {
        this.config = config;
        isVanilla = false;
        config.getKeys(false).forEach(key -> {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (Objects.nonNull(section)) {
                loadReward(section);
            }
        });
    }

    public void loadReward(ConfigurationSection section) {
        Material material = Material.getMaterial(section.getString(MATERIAL, "air").replace(Config.VANILLA_SYMBOL.asString(), "").toUpperCase());
        Point amount = BasicUtil.parseRange(section.getString(AMOUNT, "1"));
        int chance = section.getInt(CHANCE, 1);
        if (Objects.nonNull(material)) {
            rewardsList.add(new Reward(new ItemStack(material), amount, chance));
        }
    }

    public ItemStack generateItem() {
        Reward reward = Reward.randomReward(rewardsList);
        if (Objects.isNull(reward)) {
            return null;
        }
        return reward.generateItem();
    }
}

class Reward {
    private final ItemStack itemStack;
    private final Point amount;
    private final int chance;
    Reward(ItemStack itemStack, Point amount, int chance) {
        this.itemStack = itemStack;
        this.amount = amount;
        this.chance = chance;
    }
    Reward(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.amount = new Point(itemStack.getAmount(), itemStack.getAmount());
        this.chance = 1;
    }

    public ItemStack generateItem() {
        ItemStack itemStack1 = itemStack.clone();
        int amount = BasicUtil.getRandomIntFromPoint(this.amount);
        itemStack1.setAmount(amount);
        return itemStack1;
    }

    public int getChance() {
        return chance;
    }

    public static Reward randomReward(List<Reward> rewardsList) {
        int totalChance = rewardsList.stream()
                .mapToInt(Reward::getChance)
                .sum();
        int randomChance = CustomArcheology.RANDOM.nextInt(0, totalChance);
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



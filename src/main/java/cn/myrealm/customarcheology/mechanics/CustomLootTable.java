package cn.myrealm.customarcheology.mechanics;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @author rzt10
 */
public class CustomLootTable {
    private final boolean isVanilla;
    private final List<ItemStack> rewards;
    public CustomLootTable(LootTable lootTable) {
        Location location = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        rewards = new ArrayList<> (lootTable.populateLoot(new Random(), new LootContext.Builder(location).build()));
        isVanilla = true;
    }

    public ItemStack generateItem() {
        if (isVanilla) {
            return rewards.get(new Random().nextInt(rewards.size()));
        }
        return null;
    }
}

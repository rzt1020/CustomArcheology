package cn.myrealm.customarcheology.mechanics;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;

import java.util.Collection;
import java.util.Random;

/**
 * @author rzt10
 */
public class CustomLootTable {
    public CustomLootTable(LootTable lootTable) {
        Location location = new Location(Bukkit.getWorld("world"), 0, 0, 0);
        Collection<ItemStack> context = lootTable.populateLoot(new Random(), new LootContext.Builder(location).build());
    }
}

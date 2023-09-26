package cn.myrealm.customarcheology.listeners.bukkit;


import cn.myrealm.customarcheology.enums.NamespacedKeys;
import cn.myrealm.customarcheology.listeners.AbstractListener;
import cn.myrealm.customarcheology.mechanics.persistent_data.StringArrayTagType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author rzt1020
 */
public class ItemListener extends AbstractListener {
    public ItemListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onItemSpawnEntity(ItemSpawnEvent event) {
        ItemMeta meta = event.getEntity().getItemStack().getItemMeta();
        if (Objects.isNull(meta)) {
            return;
        }

        String entityTypeStr = meta.getPersistentDataContainer().get(NamespacedKeys.ARCHEOLOGY_SPAWN_ENTITY.getNamespacedKey(), PersistentDataType.STRING);
        if (Objects.nonNull(entityTypeStr)) {
            Location loc = event.getEntity().getLocation();
            World world = loc.getWorld();

            Objects.requireNonNull(world, "World of location is null!");

            for (int i = 0; i < event.getEntity().getItemStack().getAmount(); i++) {
                world.spawnEntity(loc, EntityType.valueOf(entityTypeStr));
            }
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        ItemMeta meta = event.getItem().getItemStack().getItemMeta();
        StringArrayTagType strArray = new StringArrayTagType(StandardCharsets.UTF_8);
        if (Objects.nonNull(meta) && meta.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_EXECUTE_COMMAND.getNamespacedKey(), strArray)) {
            String[] commands = meta.getPersistentDataContainer().get(NamespacedKeys.ARCHEOLOGY_EXECUTE_COMMAND.getNamespacedKey(), strArray);
            if (Objects.nonNull(commands)) {
                for (int i = 0; i < event.getItem().getItemStack().getAmount(); i++) {
                    for (String command : commands) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", event.getEntity().getName()));
                    }
                }
            }
            event.getItem().remove();
            event.setCancelled(true);
        }
    }
}

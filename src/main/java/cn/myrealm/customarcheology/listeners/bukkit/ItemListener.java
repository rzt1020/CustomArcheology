package cn.myrealm.customarcheology.listeners.bukkit;


import cn.myrealm.customarcheology.enums.NamespacedKeys;
import cn.myrealm.customarcheology.listeners.AbstractListener;
import cn.myrealm.customarcheology.mechanics.persistent_data.StringArrayTagType;
import cn.myrealm.customarcheology.utils.BasicUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.meta.ItemMeta;
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
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Item item = (Item) Bukkit.getEntity(event.getEntity().getUniqueId());
            ItemMeta meta = null;
            if (item != null) {
                meta = item.getItemStack().getItemMeta();
            }
            StringArrayTagType strArray = new StringArrayTagType(StandardCharsets.UTF_8);
            if (Objects.nonNull(meta) && meta.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_EXECUTE_ACTIONS_SPAWN.getNamespacedKey(), strArray)) {
                boolean cancel = true;
                String[] actions = meta.getPersistentDataContainer().get(NamespacedKeys.ARCHEOLOGY_EXECUTE_ACTIONS_SPAWN.getNamespacedKey(), strArray);
                if (Objects.nonNull(actions)) {
                    for (int i = 0; i < event.getEntity().getItemStack().getAmount(); i++) {
                        for (String action : actions) {
                            if (action.equals("give-item")) {
                                cancel = false;
                                continue;
                            }
                            BasicUtil.runAction(null, event.getLocation(), action);
                        }
                    }
                }
                if (cancel) {
                    event.getEntity().remove();
                    event.setCancelled(true);
                }
            }
        }, 0L);
    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        ItemMeta meta = event.getItem().getItemStack().getItemMeta();
        StringArrayTagType strArray = new StringArrayTagType(StandardCharsets.UTF_8);
        if (Objects.nonNull(meta) && meta.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_EXECUTE_ACTIONS_PICK.getNamespacedKey(), strArray)) {
            boolean cancel = true;
            String[] actions = meta.getPersistentDataContainer().get(NamespacedKeys.ARCHEOLOGY_EXECUTE_ACTIONS_PICK.getNamespacedKey(), strArray);
            if (Objects.nonNull(actions)) {
                for (int i = 0; i < event.getItem().getItemStack().getAmount(); i++) {
                    for (String action : actions) {
                        if (action.equals("give-item")) {
                            cancel = false;
                            continue;
                        }
                        BasicUtil.runAction(player, event.getItem().getLocation(), action);
                    }
                }
            }
            if (cancel) {
                event.getItem().remove();
                event.setCancelled(true);
            }
        }
    }
}

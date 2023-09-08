package cn.myrealm.customarcheology.listeners.bukkit;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.NamespacedKeys;
import cn.myrealm.customarcheology.listeners.AbstractListener;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import cn.myrealm.customarcheology.utils.PacketUtil;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author rzt10
 */
public class PlayerPlaceBlockListener extends AbstractListener {

    public PlayerPlaceBlockListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerPlaceBlock(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) ) {
            return;
        }
        ItemStack itemStack = event.getItem();
        if (Objects.isNull(itemStack) || itemStack.getType().isAir()) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (Objects.isNull(itemMeta) ||
                !(itemMeta.getPersistentDataContainer().has(NamespacedKeys.IS_ARCHEOLOGY_ITEM.getNamespacedKey(), PersistentDataType.BOOLEAN) )) {
            return;
        }

        String blockId;
        if (itemMeta.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_BLOCK_ID.getNamespacedKey(),  PersistentDataType.STRING)) {
            blockId = itemMeta.getPersistentDataContainer().get(NamespacedKeys.ARCHEOLOGY_BLOCK_ID.getNamespacedKey(), PersistentDataType.STRING);
        } else {
            return;
        }
        BlockManager blockManager = BlockManager.getInstance();
        if (!blockManager.isBlockExists(blockId)) {
            return;
        }
        Location location = Objects.requireNonNull(event.getClickedBlock()).getRelative(event.getBlockFace()).getLocation();
        Bukkit.getScheduler().runTaskLater(CustomArcheology.plugin, () -> {
            Location playerLocation = event.getPlayer().getLocation().getBlock().getLocation();
            if (playerLocation.equals(location) || playerLocation.add(0, 1, 0).equals(location)) {
                return;
            }
            PacketUtil.swingItem(event.getPlayer());
            blockManager.placeBlock(blockId, location);
            event.getPlayer().playSound(event.getPlayer(), Sound.BLOCK_STONE_PLACE, 1, 1);
        },1);
        if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
            itemStack.setAmount(itemStack.getAmount() - 1);
            event.getPlayer().getInventory().setItemInMainHand(itemStack);
        }
    }



}

package cn.myrealm.customarcheology.listeners.bukkit;


import cn.myrealm.customarcheology.listeners.AbstractListener;
import cn.myrealm.customarcheology.managers.managers.ChunkManager;
import cn.myrealm.customarcheology.managers.managers.PlayerManager;
import cn.myrealm.customarcheology.mechanics.ArcheologyBlock;
import cn.myrealm.customarcheology.mechanics.FakeTileBlock;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * @author rzt10
 */
public class PlayerBrushListener extends AbstractListener {
    public PlayerBrushListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || Objects.isNull(event.getItem()) || !event.getItem().getType().equals(Material.BRUSH)) {
            return;
        }
        ChunkManager chunkManager = ChunkManager.getInstance();
        if (chunkManager.isArcheologyBlock(Objects.requireNonNull(event.getClickedBlock()).getLocation())) {
            FakeTileBlock fakeTileBlock = chunkManager.getFakeTileBlock(event.getClickedBlock().getLocation());
            PlayerManager playerManager = PlayerManager.getInstance();
            playerManager.setBrush(event.getPlayer(), fakeTileBlock, event.getBlockFace());
        }
    }
}

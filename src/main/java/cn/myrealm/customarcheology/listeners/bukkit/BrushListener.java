package cn.myrealm.customarcheology.listeners.bukkit;


import cn.myrealm.customarcheology.enums.Permissions;
import cn.myrealm.customarcheology.listeners.AbstractListener;
import cn.myrealm.customarcheology.managers.managers.ChunkManager;
import cn.myrealm.customarcheology.managers.managers.PlayerManager;
import cn.myrealm.customarcheology.mechanics.cores.FakeTileBlock;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * @author rzt1020
 */
public class BrushListener extends AbstractListener {
    public BrushListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || Objects.isNull(event.getItem()) || !event.getItem().getType().equals(Material.BRUSH)) {
            return;
        }
        if (!Permissions.PLAY_ARCHEOLOGY.hasPermission(event.getPlayer())) {
            event.getPlayer().sendMessage("Don't have permission");
            return;
        }
        ChunkManager chunkManager = ChunkManager.getInstance();
        if (chunkManager.isArcheologyBlock(Objects.requireNonNull(event.getClickedBlock()).getLocation())) {
            FakeTileBlock fakeTileBlock = chunkManager.getFakeTileBlock(event.getClickedBlock().getLocation());
            if (!fakeTileBlock.getArcheologyBlock().canBrush(event.getItem())) {
                event.getPlayer().sendMessage("Can't brush this block");
                return;
            }
            PlayerManager.getInstance().setBrush(event.getPlayer(), fakeTileBlock, event.getBlockFace(), event.getItem());
        }
    }
}

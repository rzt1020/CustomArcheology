package cn.myrealm.customarcheology.listeners.bukkit;


import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.enums.Messages;
import cn.myrealm.customarcheology.enums.Permissions;
import cn.myrealm.customarcheology.listeners.BaseListener;
import cn.myrealm.customarcheology.managers.managers.ChunkManager;
import cn.myrealm.customarcheology.managers.managers.PlayerManager;
import cn.myrealm.customarcheology.mechanics.cores.FakeTileBlock;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * @author rzt1020
 */
public class BrushListener extends BaseListener {
    public BrushListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || Objects.isNull(event.getItem()) || !event.getItem().getType().equals(Material.BRUSH)) {
            return;
        }
        if (!event.getPlayer().getGameMode().equals(GameMode.SURVIVAL) && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        ChunkManager chunkManager = ChunkManager.getInstance();
        if (Config.DEBUG.asBoolean()) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §fFound click event.");
        }
        if (chunkManager.isArcheologyBlock(Objects.requireNonNull(event.getClickedBlock()).getLocation())) {
            if (!Permissions.PLAY_ARCHEOLOGY.hasPermission(event.getPlayer())) {
                event.getPlayer().sendMessage(Messages.GAME_BRUSH_NO_PERMISSION.getMessageWithPrefix());
                return;
            }
            FakeTileBlock fakeTileBlock = chunkManager.getFakeTileBlock(event.getClickedBlock().getLocation());
            if (!fakeTileBlock.getArcheologyBlock().canBrush(event.getItem())) {
                event.getPlayer().sendMessage(Messages.GAME_CAN_NOT_BRUSH.getMessageWithPrefix());
                return;
            }
            PlayerManager.getInstance().setBrush(event.getPlayer(), fakeTileBlock, event.getBlockFace(), event.getItem());
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (Config.DEBUG.asBoolean()) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §fClick entity and cancel brush.");
        }
        PlayerManager playerManager = PlayerManager.getInstance();
        playerManager.cancelBrush(event.getPlayer());
    }
}

package cn.myrealm.customarcheology.listeners.protocol;

import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.listeners.BaseListener;
import cn.myrealm.customarcheology.managers.managers.PlayerManager;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author rzt1020
 */
public class DigListener extends BaseListener {

    public DigListener(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Client.PLAYER_DIGGING)) {
            if (Config.DEBUG.asBoolean()) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §fBreak and cancel brush.");
            }
            PlayerManager playerManager = PlayerManager.getInstance();
            playerManager.cancelBrush(event.getPlayer());
        }
    }
}

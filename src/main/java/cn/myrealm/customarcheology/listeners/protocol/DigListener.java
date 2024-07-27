package cn.myrealm.customarcheology.listeners.protocol;


import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.listeners.BaseListener;
import cn.myrealm.customarcheology.managers.managers.PlayerManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author rzt1020
 */
public class DigListener extends BaseListener {

    public DigListener(JavaPlugin plugin) {
        super(plugin, PacketType.Play.Client.BLOCK_DIG);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Client.BLOCK_DIG)) {
            if (Config.DEBUG.asBoolean()) {
                Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §fBreak and cancel brush.");
            }
            PlayerManager playerManager = PlayerManager.getInstance();
            playerManager.cancelBrush(event.getPlayer());
        }
    }
}

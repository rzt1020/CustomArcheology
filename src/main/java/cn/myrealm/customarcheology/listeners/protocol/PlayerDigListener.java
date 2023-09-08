package cn.myrealm.customarcheology.listeners.protocol;


import cn.myrealm.customarcheology.listeners.AbstractListener;
import cn.myrealm.customarcheology.managers.managers.PlayerManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author rzt10
 */
public class PlayerDigListener extends AbstractListener {

    public PlayerDigListener(JavaPlugin plugin) {
        super(plugin, PacketType.Play.Client.BLOCK_DIG);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacketType().equals(PacketType.Play.Client.BLOCK_DIG)) {
            PlayerManager playerManager = PlayerManager.getInstance();
            playerManager.cancelBrush(event.getPlayer());
        }
    }
}
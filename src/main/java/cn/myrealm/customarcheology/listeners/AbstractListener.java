package cn.myrealm.customarcheology.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author rzt10
 */
public abstract class AbstractListener extends PacketAdapter implements Listener {

    private final JavaPlugin plugin;

    public AbstractListener(JavaPlugin plugin, PacketType... types) {
        super(plugin, ListenerPriority.NORMAL, types);
        this.plugin = plugin;
    }

    public AbstractListener(JavaPlugin plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.RECIPES);
        this.plugin = plugin;
    }

    public void registerBukkitListener() {
        PluginManager pm = plugin.getServer().getPluginManager();
        pm.registerEvents(this, plugin);
    }

    public void registerProtocolListener() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(this);
    }

}


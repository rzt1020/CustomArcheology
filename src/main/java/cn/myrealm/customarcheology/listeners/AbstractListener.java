package cn.myrealm.customarcheology.listeners;

import cn.myrealm.customarcheology.CustomArcheology;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author rzt1020
 */
public abstract class AbstractListener extends PacketAdapter implements Listener {

    protected final JavaPlugin plugin;

    public AbstractListener(JavaPlugin plugin, PacketType... types) {
        super(plugin, ListenerPriority.NORMAL, types);
        this.plugin = plugin;
    }

    public AbstractListener(JavaPlugin plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.RECIPES);
        this.plugin = plugin;
    }

    public void registerBukkitListener() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void registerProtocolListener() {
        CustomArcheology.protocolManager.addPacketListener(this);
    }

}


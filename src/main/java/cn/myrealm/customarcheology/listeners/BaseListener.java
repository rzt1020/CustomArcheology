package cn.myrealm.customarcheology.listeners;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author rzt1020
 */
public abstract class BaseListener implements Listener, PacketListener {

    protected final JavaPlugin plugin;

    public BaseListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerBukkitListener() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void registerProtocolListener() {
        PacketEvents.getAPI().getEventManager().registerListener(
                this, PacketListenerPriority.NORMAL);
    }

}


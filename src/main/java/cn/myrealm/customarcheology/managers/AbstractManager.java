package cn.myrealm.customarcheology.managers;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author rzt1020
 */
public abstract class AbstractManager {

    protected final JavaPlugin plugin;

    private static AbstractManager instance;

    protected AbstractManager(JavaPlugin plugin) {
        this.plugin = plugin;
        instance = this;
        onInit();
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractManager> T getInstance() {
        return (T) instance;
    }

    protected void onInit() {}

    public void disable() {
        onDisable();
    }

    protected void onDisable() {}
}


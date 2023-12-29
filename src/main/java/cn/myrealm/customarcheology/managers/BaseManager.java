package cn.myrealm.customarcheology.managers;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author rzt1020
 */
public abstract class BaseManager {

    protected final JavaPlugin plugin;

    private static BaseManager instance;

    protected BaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
        instance = this;
        onInit();
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseManager> T getInstance() {
        return (T) instance;
    }

    protected void onInit() {}

    public void disable() {
        onDisable();
    }

    protected void onDisable() {}
}


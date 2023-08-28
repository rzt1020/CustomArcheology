package cn.myrealm.customarcheology.managers.managers;

import cn.myrealm.customarcheology.managers.AbstractManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rzt10
 */
public class BlockManager extends AbstractManager {
    public BlockManager(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void onInit() {

    }

    public List<String> getBlocksName() {
        return new ArrayList<>();
    }

}

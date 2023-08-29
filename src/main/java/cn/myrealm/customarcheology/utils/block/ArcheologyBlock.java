package cn.myrealm.customarcheology.utils.block;


import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author rzt10
 */
public class ArcheologyBlock {
    private final YamlConfiguration config;
    private final String name;

    public ArcheologyBlock(YamlConfiguration config, String name) {
        this.config = config;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isValid() {
        return true;
    }
}

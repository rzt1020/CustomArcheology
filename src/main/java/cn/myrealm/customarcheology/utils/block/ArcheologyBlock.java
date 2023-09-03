package cn.myrealm.customarcheology.utils.block;


import cn.myrealm.customarcheology.managers.managers.SysyemManager.TextureManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author rzt10
 */
public class ArcheologyBlock {
    private final YamlConfiguration config;
    private final String name;
    private String displayName;
    private boolean valid;
    private State defaultState,
                 finishedState;
    private List<State> states;

    public ArcheologyBlock(YamlConfiguration config, String name) {
        this.config = config;
        this.name = name;
        loadConfig();
    }

    private void loadConfig() {
        ConfigurationSection section = Keys.STATES.asSection(config);
        if (Objects.isNull(section)) {
            return;
        }
        Map<String,Object> stateSections = section.getValues(false);
        states = new ArrayList<>();
        for (String stateName : stateSections.keySet()) {
            State state = new State((ConfigurationSection) stateSections.get(stateName));
            if (state.isDefault) {
                defaultState = state;
            } else if (state.isFinished) {
                finishedState = state;
            } else {
                states.add(state);
            }
        }
        if (Objects.isNull(defaultState) || Objects.isNull(finishedState)) {
            return;
        }
        valid = true;
        displayName = Keys.DISPLAY_NAME.asString(config);

    }

    public String getName() {
        return name;
    }

    public boolean isValid() {
        return valid;
    }
}

enum Keys {
    // state keys
    TEXTURE("texture", null),
    HARDNESS("hardness", 1.0d),
    MATERIAL("material", "minecraft:stone"),
    // block keys
    DISPLAY_NAME("display_name", null),
    LOOT_TABLE("loot_table", null),
    BLUSH_TOOLS("blush_tools", null),
    STATES("states", null);

    private final String key;
    private final Object def;

    Keys(String key, Object def) {
        this.key = key;
        this.def = def;
    }

    public String asString(ConfigurationSection section) {
        if (Objects.isNull(section)) {
            return (String) def;
        }
        return section.getString(key, (String) def);
    }

    public ConfigurationSection asSection(ConfigurationSection section) {
        if (Objects.isNull(section)) {
            return null;
        }
        return section.getConfigurationSection(key);
    }

    public Double asDouble(ConfigurationSection section) {
        if (Objects.isNull(section)) {
            return (Double) def;
        }
        return section.getDouble(key, (Double) def);
    }
}

class State {
    private boolean valid;
    private final ConfigurationSection section;
    public boolean isDefault,
                   isFinished;
    private final String texture;
    private final String material;
    private final double hardness;

    private final static String DEFAULT_NAME = "default",
                                FINISHED_NAME = "finished";
    State(ConfigurationSection section) {
        this.section = section;
        if (DEFAULT_NAME.equals(section.getName())) {
            isDefault = true;
        }
        if (FINISHED_NAME.equals(section.getName())) {
            isFinished = true;
        }
        if (isFinished) {
            hardness = 0f;
            material = Keys.MATERIAL.asString(section);
            texture = null;
        } else {
            hardness = Keys.HARDNESS.asDouble(section);
            material = null;
            texture = Keys.TEXTURE.asString(section);
        }
        TextureManager textureManager = TextureManager.getInstance();
        if (!textureManager.isBlockTextureExists(texture)) {
            return;
        }
        valid = true;
    }


}

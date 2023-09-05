package cn.myrealm.customarcheology.mechanics;


import cn.myrealm.customarcheology.enums.NamespacedKeys;
import cn.myrealm.customarcheology.managers.managers.SysyemManager.LanguageManager;
import cn.myrealm.customarcheology.managers.managers.SysyemManager.TextureManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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
    private Material replace_block;
    private boolean valid;
    private State defaultState,
                  finishedState;
    private List<State> states;

    public ArcheologyBlock(YamlConfiguration config, String name) {
        this.config = config;
        this.name = name;
        loadConfig();
    }

    public ItemStack generateItemStack(int amount) {
        if (!isValid()) {
            throw  new IllegalStateException("This block is not valid");
        }
        ItemStack itemStack = new ItemStack(Material.BLUE_DYE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setCustomModelData(defaultState.getCustomModelData());
            itemMeta.setDisplayName(LanguageManager.parseColor(displayName));
            itemMeta.getPersistentDataContainer().set(NamespacedKeys.IS_ARCHEOLOGY_ITEM.getNamespacedKey(), PersistentDataType.BOOLEAN, true);
            itemMeta.getPersistentDataContainer().set(NamespacedKeys.ARCHEOLOGY_BLOCK_ID.getNamespacedKey(), PersistentDataType.STRING, name);
        }
        itemStack.setItemMeta(itemMeta);
        itemStack.setAmount(amount);
        return itemStack;
    }

    public void placeBlock(Location location) {
        location.getBlock().setType(replace_block);
    }
    private void loadConfig() {
        ConfigurationSection section = Keys.STATES.asSection(config);
        replace_block = Material.getMaterial(Keys.REPLACE_BLOCK.asString(config).toUpperCase());
        System.out.println(replace_block);
        if (Objects.isNull(section) || Objects.isNull(replace_block) || !replace_block.isBlock()) {
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
    REPLACE_BLOCK("replace_block", "stone"),
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

    public int getCustomModelData() {
        if (isFinished) {
            throw  new IllegalStateException("Cannot get custom model data from finished state");
        }
        TextureManager textureManager = TextureManager.getInstance();
        int customModelData = textureManager.getCustommodeldata(texture);
        if (customModelData == -1) {
            throw new IllegalStateException("Cannot get custom model data from texture " + texture);
        }
        return customModelData;
    }


}

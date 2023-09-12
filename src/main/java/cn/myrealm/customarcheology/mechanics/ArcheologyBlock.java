package cn.myrealm.customarcheology.mechanics;


import cn.myrealm.customarcheology.enums.NamespacedKeys;
import cn.myrealm.customarcheology.managers.managers.LootManager;
import cn.myrealm.customarcheology.managers.managers.system.LanguageManager;
import cn.myrealm.customarcheology.managers.managers.system.TextureManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author rzt10
 */
public class ArcheologyBlock {
    private final YamlConfiguration config;
    private final String name;
    private String displayName;
    private Material replaceBlock;
    private boolean valid;
    private State defaultState,
                  finishedState;
    private List<State> states;
    private List<CustomLootTable> customLootTables;

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
    public ItemStack generateItemStack(int amount, State state) {
        ItemStack itemStack = generateItemStack(amount);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setCustomModelData(state.getCustomModelData());
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public void placeBlock(Location location) {
        location.getBlock().setType(replaceBlock);
    }
    private void loadConfig() {
        ConfigurationSection section = Keys.STATES.asSection(config);
        replaceBlock = Material.getMaterial(Keys.REPLACE_BLOCK.asString(config).toUpperCase());
        if (Objects.isNull(section) || Objects.isNull(replaceBlock) || !replaceBlock.isBlock()) {
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
        states = states.stream().sorted(Comparator.comparing(State::getIndex)).collect(Collectors.toList());
        if (Objects.isNull(defaultState) || Objects.isNull(finishedState)) {
            return;
        }
        customLootTables = new ArrayList<>();
        LootManager lootManager = LootManager.getInstance();
        for (String lootTableName : Keys.LOOT_TABLES.asStringList(config)) {
            customLootTables.add(lootManager.getCustomLootTable(lootTableName));
        }
        if (customLootTables.isEmpty()) {
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

    public ItemStack roll() {
        CustomLootTable customLootTable = customLootTables.get(new Random().nextInt(customLootTables.size()));
        return customLootTable.generateItem();
    }

    public State getDefaultState() {
        return defaultState;
    }
    public State getFinishedState() {
        return finishedState;
    }
    public List<State> getStates() {
        return states;
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
    LOOT_TABLES("loot_tables", null),
    BLUSH_TOOLS("blush_tools", null),
    STATES("states", null),
    GENERATE_BIOMES("generate_biomes", "all"),
    DISTRIBUTION("distribution", null),
    MAX_PER_CHUNK("max_per_chunk", 3);

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

    public List<String> asStringList(ConfigurationSection section) {
        if (Objects.isNull(section)) {
            return new ArrayList<>();
        }
        return section.getStringList(key);
    }
}

class State {
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
    }
    public int getIndex() {
        return Integer.parseInt(section.getName().substring(section.getName().length() - 1));
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
    public double getHardness() {
        return hardness;
    }
    public Material getMaterial() {
        if (Objects.nonNull(material)) {
            return Material.valueOf(material.toUpperCase());
        }
        return null;
    }

}

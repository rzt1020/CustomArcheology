package cn.myrealm.customarcheology.mechanics.cores;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Messages;
import cn.myrealm.customarcheology.enums.NamespacedKeys;
import cn.myrealm.customarcheology.managers.managers.LootManager;
import cn.myrealm.customarcheology.managers.managers.system.LanguageManager;
import cn.myrealm.customarcheology.managers.managers.system.TextureManager;
import cn.myrealm.customarcheology.mechanics.CustomLootTable;
import cn.myrealm.customarcheology.utils.CommonUtil;
import cn.myrealm.customarcheology.utils.ItemUtil;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author rzt1020
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
    private List<Biome> biomes;
    private Point distribution;
    private int maxPerChunk;

    public ArcheologyBlock(YamlConfiguration config, String name) {
        this.config = config;
        this.name = name;
        loadConfig();
    }

    public ItemStack generateItemStack(int amount) {
        if (!isValid()) {
            throw new IllegalStateException("This block is not valid");
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
        if (Keys.GENERATE_BIOMES.isDef(config) ||
                (config.contains("general.generate_biomes") && config.getString("general.generate_biomes").equals("all"))) {
            biomes = null;
        } else {
            biomes = new ArrayList<>();
            List<String> biomesName = Keys.GENERATE_BIOMES.asStringList(config);
            if (biomesName.isEmpty()) {
                biomesName = config.getStringList("general.generate_biomes");
            }
            biomesName.forEach(name -> {
                try {
                    Biome biome = Biome.valueOf(name.toUpperCase());
                    biomes.add(biome);
                } catch (IllegalArgumentException e) {
                    Bukkit.getConsoleSender().sendMessage(Messages.ERROR_BIOMES_NOT_FOUND.getMessageWithPrefix("biomes-name", name));
                }
            });
        }
        distribution = CommonUtil.parseRange(Keys.DISTRIBUTION.asString(config));
        maxPerChunk =  Keys.MAX_PER_CHUNK.asInt(config);
    }

    public List<Biome> getBiomes() {
        return biomes;
    }
    public Point getDistribution() {
        return distribution;
    }
    public int getMaxPerChunk() {
        return maxPerChunk;
    }

    public String getName() {
        return name;
    }

    public boolean isValid() {
        return valid;
    }

    public ItemStack roll(ItemStack tool) {
        String toolId = ItemUtil.getToolId(tool);
        ConfigurationSection section = Objects.requireNonNull(Keys.BRUSH_TOOLS.asSection(config)).getConfigurationSection(toolId);
        String lootTableName = Keys.TOOL_LOOT_TABLES.asString(section);
        if (lootTableName == null || LootManager.getInstance().getCustomLootTable(lootTableName) == null) {
            CustomLootTable customLootTable = customLootTables.get(CustomArcheology.RANDOM.nextInt(customLootTables.size()));
            if (customLootTable.generateItem() == null) {
                return new ItemStack(Material.STONE);
            }
            return customLootTable.generateItem();
        }
        return LootManager.getInstance().getCustomLootTable(lootTableName).generateItem();
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

    public Material getType() {
        return replaceBlock;
    }

    public boolean canBrush(ItemStack tool) {
        String toolId = ItemUtil.getToolId(tool);
        return Objects.requireNonNull(Keys.BRUSH_TOOLS.asSection(config)).getKeys(false).contains(toolId);
    }

    public double getEfficiency(ItemStack tool) {
        String toolId = ItemUtil.getToolId(tool);
        ConfigurationSection section = Objects.requireNonNull(Keys.BRUSH_TOOLS.asSection(config)).getConfigurationSection(toolId);
        return Keys.EFFICIENCY.asDouble(section);
    }

    public boolean isGaussian() {
        return Objects.nonNull(Keys.GAUSSIAN.asSection(config));
    }
    public double getGaussianMean() {
        return Keys.MEAN.asDouble(config);
    }
    public double getGaussianStdDev() {
        return Keys.STANDARD_DEVIATION.asDouble(config);
    }

    public boolean isStructure() {
        return config.contains("general.structure.type");
    }
    public Structure getStructure() {
        String structureName = Keys.STRUCTURE_TYPE.asString(config);
        if (Objects.nonNull(structureName)) {
            return Registry.STRUCTURE.get(NamespacedKey.minecraft(structureName));
        }
        return null;
    }

    public boolean isBetterStructure() {
        return config.contains("general.region.betterstructures");
    }
    public boolean containsBetterStructure(String id) {
        return Keys.BETTERSTRUCTURE_TYPE.asStringList(config).contains(id) || Keys.BETTERSTRUCTURE_TYPE.asString(config).equals("all");
    }

    public Sound getPlaceSound() {
        return Registry.SOUNDS.get(CommonUtil.parseNamespacedKey(Keys.PLACE_SOUND.asString(config)));
    }
    public Sound getBrushSound() {
        return Registry.SOUNDS.get(CommonUtil.parseNamespacedKey(Keys.BRUSH_SOUND.asString(config).toUpperCase()));
    }
    public int getConsumeDurability() {
        return Keys.CONSUME_DURABILITY.asInt(config);
    }
}

enum Keys {
    // state keys
    TEXTURE("texture", null),
    HARDNESS("hardness", 1.0d),
    MATERIAL("material", "stone"),
    // block keys
    DISPLAY_NAME("general.display_name", null),
    REPLACE_BLOCK("general.replace_block", "stone"),
    LOOT_TABLES("general.loot_tables", null),
    BRUSH_TOOLS("brush_tools", null),
    EFFICIENCY("efficiency", 1.0d),
    TOOL_LOOT_TABLES("loot_table", null),
    STATES("states", null),
    GENERATE_BIOMES("general.biomes", "all"),
    DISTRIBUTION("general.distribution", null),
    MAX_PER_CHUNK("general.max_per_chunk", 0),
    GAUSSIAN("general.gaussian", null),
    MEAN("general.gaussian.mean", 0D),
    STANDARD_DEVIATION("general.gaussian.standard_deviation", 1D),
    STRUCTURE_TYPE("general.structure.type", null),
    BETTERSTRUCTURE_TYPE("general.region.betterstructures", null),
    PLACE_SOUND("general.sound.place", "BLOCK_STONE_PLACE"),
    BRUSH_SOUND("general.sound.brush", "BLOCK_SUSPICIOUS_SAND_BREAK"),
    CONSUME_DURABILITY("general.consume_durability", 1);

    private final String key;
    private final Object def;

    Keys(String key, Object def) {
        this.key = key;
        this.def = def;
    }

    public boolean isDef(ConfigurationSection section) {
        return Objects.equals(section.get(key), def) || Objects.equals(section.get(key.replace("_", "-")), def);
    }

    public String asString(ConfigurationSection section) {
        if (Objects.isNull(section)) {
            return (String) def;
        }
        if (Objects.nonNull(section.getString(key.replace("_", "-")))) {
            return section.getString(key.replace("_", "-"));
        }
        return section.getString(key, (String) def);
    }

    public ConfigurationSection asSection(ConfigurationSection section) {
        if (Objects.isNull(section)) {
            return null;
        }
        if (Objects.nonNull(section.getConfigurationSection(key.replace("_", "-")))) {
            return section.getConfigurationSection(key.replace("_", "-"));
        }
        return section.getConfigurationSection(key);
    }

    public Double asDouble(ConfigurationSection section) {
        if (Objects.isNull(section)) {
            return (Double) def;
        }
        if (section.contains(key.replace("_", "-"))) {
            return section.getDouble(key.replace("_", "-"));
        }
        return section.getDouble(key, (Double) def);
    }

    public List<String> asStringList(ConfigurationSection section) {
        if (Objects.isNull(section)) {
            return new ArrayList<>();
        }
        if (section.contains(key.replace("_", "-"))) {
            return section.getStringList(key.replace("_", "-"));
        }
        return section.getStringList(key);
    }

    public Integer asInt(ConfigurationSection section) {
        if (Objects.isNull(section)) {
            return (Integer) def;
        }
        if (section.contains(key.replace("_", "-"))) {
            return section.getInt(key.replace("_", "-"));
        }
        return section.getInt(key, (Integer) def);
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
            throw new IllegalStateException("Cannot get custom model data from finished state");
        }
        TextureManager textureManager = TextureManager.getInstance();
        int customModelData = textureManager.getBlockCustommodeldata(texture);
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

    public ConfigurationSection getSection() {
        return section;
    }

}

package cn.myrealm.customarcheology.mechanics.cores;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.enums.NamespacedKeys;
import cn.myrealm.customarcheology.mechanics.persistent_data.ItemStackTagType;
import cn.myrealm.customarcheology.mechanics.persistent_data.LocationTagType;
import cn.myrealm.customarcheology.mechanics.persistent_data.StringArrayTagType;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author rzt1020
 */
public class PersistentDataChunk {
    private final Chunk chunk;
    private List<String> blockNameList;
    private static final StringArrayTagType STRING_ARRAY_TYPE = new StringArrayTagType(StandardCharsets.UTF_8);
    private static final ItemStackTagType ITEM_STACK_TYPE = new ItemStackTagType();
    private static final LocationTagType LOCATION_TYPE = new LocationTagType();
    private final Map<Location, FakeTileBlock> loadedLocationBlocks = new HashMap<>();
    public PersistentDataChunk(Chunk chunk) {
        this.chunk = chunk;
        loadChunk();
    }

    public void loadChunk() {
        loadBlockNames();
        loadBlockLocations();
    }
    private void loadBlockNames() {
        if (chunk.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_ARRAY.getNamespacedKey(), STRING_ARRAY_TYPE)) {
            blockNameList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(chunk.getPersistentDataContainer().get(NamespacedKeys.ARCHEOLOGY_ARRAY.getNamespacedKey(), STRING_ARRAY_TYPE))));
        } else {
            blockNameList = new ArrayList<>();
        }
    }
    private void loadBlockLocations() {
        for (String blockName : blockNameList) {
            Location location = null;
            if (chunk.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_BLOCK_LOC.getNamespacedKey(blockName), LOCATION_TYPE)) {
                location = chunk.getPersistentDataContainer().get(NamespacedKeys.ARCHEOLOGY_BLOCK_LOC.getNamespacedKey(blockName), LOCATION_TYPE);
            }
            ItemStack reward = null;
            if (chunk.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_BLOCK_ITEM.getNamespacedKey(blockName), ITEM_STACK_TYPE)) {
                reward = chunk.getPersistentDataContainer().get(NamespacedKeys.ARCHEOLOGY_BLOCK_ITEM.getNamespacedKey(blockName), ITEM_STACK_TYPE);
            }
            if (Objects.nonNull(location)) {
                FakeTileBlock fakeTileBlock = new FakeTileBlock(blockName, location, reward);
                if (fakeTileBlock.isValid()) {
                    loadedLocationBlocks.put(location, fakeTileBlock);
                }
            }
        }
    }

    public void saveChunk() {
        if (Objects.isNull(blockNameList)) {
            return;
        }
        for (FakeTileBlock fakeTileBlock : loadedLocationBlocks.values()) {
            fakeTileBlock.removeBlock();
        }
        saveBlockNames();
        saveBlockLocations();
        saveBlockRewards();
    }

    private void saveBlockRewards() {
        for (FakeTileBlock fakeTileBlock : loadedLocationBlocks.values()) {
            if (Objects.nonNull(fakeTileBlock.getReward())) {
                chunk.getPersistentDataContainer().set(NamespacedKeys.ARCHEOLOGY_BLOCK_ITEM.getNamespacedKey(fakeTileBlock.getBlockName()), ITEM_STACK_TYPE, fakeTileBlock.getReward() );
            }
        }
    }

    private void saveBlockNames() {
        String [] array = new String[blockNameList.size()];
        blockNameList.toArray(array);
        chunk.getPersistentDataContainer().set(NamespacedKeys.ARCHEOLOGY_ARRAY.getNamespacedKey(), STRING_ARRAY_TYPE, array);
    }
    private void saveBlockLocations() {
        for (Location location : loadedLocationBlocks.keySet()) {
            FakeTileBlock block = loadedLocationBlocks.get(location);
            chunk.getPersistentDataContainer().set(NamespacedKeys.ARCHEOLOGY_BLOCK_LOC.getNamespacedKey(block.getBlockName()), LOCATION_TYPE, location);
        }
    }
    public void removeBlock(Location location) {
        location = location.getBlock().getLocation();
        if (!loadedLocationBlocks.containsKey(location)) {
            return;
        }
        String removedBlockData = loadedLocationBlocks.get(location).getBlockName();
        loadedLocationBlocks.get(location).removeBlock();
        blockNameList.remove(removedBlockData);
        loadedLocationBlocks.remove(location);
        saveBlockNames();
        if (chunk.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_BLOCK_LOC.getNamespacedKey(removedBlockData), LOCATION_TYPE)) {
            chunk.getPersistentDataContainer().remove(NamespacedKeys.ARCHEOLOGY_BLOCK_LOC.getNamespacedKey(removedBlockData));
        }
        if (chunk.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_BLOCK_ITEM.getNamespacedKey(removedBlockData), ITEM_STACK_TYPE)) {
            chunk.getPersistentDataContainer().remove(NamespacedKeys.ARCHEOLOGY_BLOCK_ITEM.getNamespacedKey(removedBlockData));
        }
    }

    public void registerNewBlock(ArcheologyBlock block, Location location) {
        String blockName;
        if (Config.BLOCK_SAVE.asString().equals("UUID")) {
            blockName = block.getName() + "_" + UUID.randomUUID();
        } else {
            blockName = block.getName() + "_" + CustomArcheology.RANDOM.nextInt();
        }
        blockNameList.add(blockName);
        FakeTileBlock fakeTileBlock = new FakeTileBlock(blockName, location, null);
        if (fakeTileBlock.isValid()) {
            fakeTileBlock.placeBlock();
            loadedLocationBlocks.put(location, fakeTileBlock);
        }
    }

    public boolean isArcheologyBlock(Location location) {
        return loadedLocationBlocks.containsKey(location);
    }

    public ArcheologyBlock getArcheologyBlock(Location location) {
        if (loadedLocationBlocks.containsKey(location)) {
            return loadedLocationBlocks.get(location).getArcheologyBlock();
        }
        return null;
    }


    public Collection<FakeTileBlock> getFakeTileBlocks() {
        return loadedLocationBlocks.values();
    }

    public FakeTileBlock getFakeTileBlock(Location location) {
        if (loadedLocationBlocks.containsKey(location)){
            return loadedLocationBlocks.get(location);
        }
        return null;
    }

}

package cn.myrealm.customarcheology.mechanics;


import cn.myrealm.customarcheology.enums.NamespacedKeys;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import cn.myrealm.customarcheology.mechanics.persistent_data.ItemStackTagType;
import cn.myrealm.customarcheology.mechanics.persistent_data.LocationTagType;
import cn.myrealm.customarcheology.mechanics.persistent_data.StringArrayTagType;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author rzt10
 */
public class PersistentDataChunk {
    private final Chunk chunk;
    private List<String> blockNameList;
    private final StringArrayTagType STRING_ARRAY_TYPE = new StringArrayTagType(StandardCharsets.UTF_8);
    private final ItemStackTagType ITEM_STACK_TYPE = new ItemStackTagType();
    private final LocationTagType LOCATION_TYPE = new LocationTagType();
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
            System.out.println(chunk.getX() + "," + chunk.getZ() + " loaded:" + blockNameList);
        } else {
            blockNameList = new ArrayList<>();
            System.out.println(chunk.getX() + "," + chunk.getZ() + " loaded:null");
        }
    }
    private void loadBlockLocations() {
        for (String  blockName : blockNameList) {
            Location location = null;
            if (chunk.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_BLOCK_LOC.getNamespacedKey(blockName), LOCATION_TYPE)) {
                location = chunk.getPersistentDataContainer().get(NamespacedKeys.ARCHEOLOGY_BLOCK_LOC.getNamespacedKey(blockName), LOCATION_TYPE);
            }
            if (Objects.nonNull(location)) {
                FakeTileBlock fakeTileBlock = new FakeTileBlock(blockName, location);
                if (fakeTileBlock.isValid()) {
                    loadedLocationBlocks.put(location, fakeTileBlock);
                    System.out.println(chunk.getX() + "," + chunk.getZ() + " loaded:" + location);
                    if (chunk.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_BLOCK_ITEM.getNamespacedKey(blockName), ITEM_STACK_TYPE)) {
                        ItemStack itemStack = chunk.getPersistentDataContainer().get(NamespacedKeys.ARCHEOLOGY_BLOCK_ITEM.getNamespacedKey(blockName), ITEM_STACK_TYPE);
                        if (Objects.nonNull(itemStack)) {
                            fakeTileBlock.setReward(itemStack);
                            System.out.println(chunk.getX() + "," + chunk.getZ() + "reward:" + itemStack);
                        }
                    }
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
        System.out.println(chunk.getX() + "," + chunk.getZ() + " saved:" + blockNameList);
        saveBlockLocations();
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
            System.out.println(chunk.getX() + "," + chunk.getZ() + " saved:" + location);
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
            System.out.println(chunk.getX()  + "," + chunk.getZ() + " removed:" + removedBlockData);
        }
    }

    public void registerNewBlock(ArcheologyBlock block, Location location) {
        String blockName = block.getName() + "_" + new Random().nextInt();
        System.out.println(blockName);
        blockNameList.add(blockName);
        FakeTileBlock fakeTileBlock = new FakeTileBlock(blockName, location);
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

    public ItemStack getReward(Location location) {
        if (loadedLocationBlocks.containsKey(location)) {
            String name = loadedLocationBlocks.get(location).getBlockName();
            if (chunk.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_BLOCK_ITEM.getNamespacedKey(name), ITEM_STACK_TYPE)) {
                return chunk.getPersistentDataContainer().get(NamespacedKeys.ARCHEOLOGY_BLOCK_ITEM.getNamespacedKey(name), ITEM_STACK_TYPE);
            }
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

package cn.myrealm.customarcheology.mechanics;


import cn.myrealm.customarcheology.enums.NamespacedKeys;
import cn.myrealm.customarcheology.mechanics.ArcheologyBlock;
import cn.myrealm.customarcheology.mechanics.persistent_data.LocationTagType;
import cn.myrealm.customarcheology.mechanics.persistent_data.StringArrayTagType;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author rzt10
 */
public class PersistentDataChunk {
    private final Chunk chunk;
    private List<String> loadedBlockDataList,
                        removedBlockDataList;
    private final StringArrayTagType tagType;
    private final Map<String, Location> loadedBlockLocations = new HashMap<>();
    public PersistentDataChunk(Chunk chunk) {
        this.chunk = chunk;
        tagType = new StringArrayTagType(StandardCharsets.UTF_8);
        loadChunk();
    }

    public void loadChunk() {
        if (chunk.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_ARRAY.getNamespacedKey(), tagType)) {
            loadedBlockDataList = new ArrayList<>(Arrays.asList(Objects.requireNonNull(chunk.getPersistentDataContainer().get(NamespacedKeys.ARCHEOLOGY_ARRAY.getNamespacedKey(), tagType))));
            System.out.println(chunk.getX() + "," + chunk.getZ() + " loaded:" + loadedBlockDataList);
        } else {
            loadedBlockDataList = new ArrayList<>();
            System.out.println(chunk.getX() + "," + chunk.getZ() + " loaded:null");
        }
        for (String  blockData : loadedBlockDataList) {
            Location location = null;
            if (chunk.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_BLOCK_LOC.getNamespacedKey(blockData), new LocationTagType())) {
                location = chunk.getPersistentDataContainer().get(NamespacedKeys.ARCHEOLOGY_BLOCK_LOC.getNamespacedKey(blockData), new LocationTagType());
            }
            if (Objects.nonNull(location)) {
                loadedBlockLocations.put(blockData, location);
                System.out.println(chunk.getX() + "," + chunk.getZ() + " loaded:" + location);
            }
        }
    }
    public void saveChunk() {
        if (Objects.isNull(loadedBlockDataList)) {
            return;
        }
        String [] array = new String[loadedBlockDataList.size()];
        loadedBlockDataList.toArray(array);
        chunk.getPersistentDataContainer().set(NamespacedKeys.ARCHEOLOGY_ARRAY.getNamespacedKey(), tagType, array);
        System.out.println(chunk.getX() + "," + chunk.getZ() + " saved:" + loadedBlockDataList);
        for (String blockData : loadedBlockDataList) {
            chunk.getPersistentDataContainer().set(NamespacedKeys.ARCHEOLOGY_BLOCK_LOC.getNamespacedKey(blockData), new LocationTagType(), loadedBlockLocations.get(blockData));
            System.out.println(chunk.getX() + "," + chunk.getZ() + " saved:" + loadedBlockLocations.get(blockData));
        }
    }
    public void removeBlock(Location location) {
        location = location.getBlock().getLocation();
        String removedBlockData = null;
        for (String  blockData : loadedBlockDataList) {
            if (loadedBlockLocations.get(blockData).equals(location)) {
                removedBlockData = blockData;
                break;
            }
        }
        if (Objects.nonNull(removedBlockData)) {
            loadedBlockDataList.remove(removedBlockData);
            loadedBlockLocations.remove(removedBlockData);
            if (chunk.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_ARRAY.getNamespacedKey(), tagType)) {
                String [] array = new String[loadedBlockDataList.size()];
                loadedBlockDataList.toArray(array);
                chunk.getPersistentDataContainer().set(NamespacedKeys.ARCHEOLOGY_ARRAY.getNamespacedKey(), tagType, array);
            }
            if (chunk.getPersistentDataContainer().has(NamespacedKeys.ARCHEOLOGY_BLOCK_LOC.getNamespacedKey(removedBlockData), new LocationTagType())) {
                chunk.getPersistentDataContainer().remove(NamespacedKeys.ARCHEOLOGY_BLOCK_LOC.getNamespacedKey(removedBlockData));
                System.out.println(chunk.getX()  + "," + chunk.getZ() + " removed:" + removedBlockData);
            }
        }
    }

    public void registerNewBlock(ArcheologyBlock block, Location location) {
        String blockName = block.getName() + "_" + new Random().nextInt();
        System.out.println(blockName);
        loadedBlockDataList.add(blockName);
        loadedBlockLocations.put(blockName, location);
    }

}

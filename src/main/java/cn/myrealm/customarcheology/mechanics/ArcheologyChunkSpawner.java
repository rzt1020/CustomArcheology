package cn.myrealm.customarcheology.mechanics;


import cn.myrealm.customarcheology.enums.NamespacedKeys;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import cn.myrealm.customarcheology.mechanics.cores.ArcheologyBlock;
import cn.myrealm.customarcheology.mechanics.cores.PersistentDataChunk;
import cn.myrealm.customarcheology.mechanics.persistent_data.StringArrayTagType;
import cn.myrealm.customarcheology.utils.BasicUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

/**
 * @author rzt10
 */
public class ArcheologyChunkSpawner {
    private static final StringArrayTagType STRING_ARRAY_TYPE = new StringArrayTagType(StandardCharsets.UTF_8);
    private final Chunk chunk;
    private final PersistentDataChunk dataChunk;
    private final List<ArcheologyBlock> blocks;
    private final List<String> spawnedBlocks;
    public ArcheologyChunkSpawner(Chunk chunk, PersistentDataChunk dataChunk) {
        this.chunk = chunk;
        this.dataChunk = dataChunk;
        World world = chunk.getWorld();
        this.blocks = BlockManager.getInstance().getBlocks(world);
        if (chunk.getPersistentDataContainer().has(NamespacedKeys.ARCHIFY_ARRAY.getNamespacedKey(), STRING_ARRAY_TYPE)) {
            spawnedBlocks = new ArrayList<>(Arrays.asList(Objects.requireNonNull(chunk.getPersistentDataContainer().get(NamespacedKeys.ARCHIFY_ARRAY.getNamespacedKey(), STRING_ARRAY_TYPE))));
        } else {
            spawnedBlocks = new ArrayList<>();
        }
        spawnBlocks();
    }

    private void spawnBlocks() {
        Set<Block> usedBlocks = new HashSet<>();
        blocks.forEach(block -> {
            if (spawnedBlocks.contains(block.getName())) {
                return;
            }
            int maxPerChunk = block.getMaxPerChunk();
            Point distribution = block.getDistribution();
            List<Biome> biomes = block.getBiomes();
            for (int i = 0; i < maxPerChunk; i++) {
                Block newBlock = BasicUtil.getRandomBlock(chunk, distribution);
                if (!usedBlocks.contains(newBlock) && Objects.equals(newBlock.getType(), block.getType())) {
                    if (Objects.isNull(biomes) || biomes.contains(newBlock.getBiome())) {
                        setBlock(newBlock.getLocation(), block);
                    }
                }
                usedBlocks.add(newBlock);
            }
        });
        spawnedBlocks.addAll(blocks.stream().map(ArcheologyBlock::getName).toList());
        String [] array = new String[spawnedBlocks.size()];
        spawnedBlocks.toArray(array);
        chunk.getPersistentDataContainer().set(NamespacedKeys.ARCHIFY_ARRAY.getNamespacedKey(), STRING_ARRAY_TYPE, array);
    }

    private void setBlock(Location location, ArcheologyBlock block) {
        dataChunk.registerNewBlock(block, location);
    }
}

package cn.myrealm.customarcheology.mechanics;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.NamespacedKeys;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import cn.myrealm.customarcheology.mechanics.cores.ArcheologyBlock;
import cn.myrealm.customarcheology.mechanics.cores.PersistentDataChunk;
import cn.myrealm.customarcheology.mechanics.persistent_data.StringArrayTagType;
import cn.myrealm.customarcheology.utils.CommonUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.generator.structure.GeneratedStructure;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;

/**
 * @author rzt1020
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
            if (block.isStructure() && CustomArcheology.canUseStructure) {
                for (GeneratedStructure gs : chunk.getStructures(block.getStructure())) {
                    for (int i = 0; i < maxPerChunk; i++) {
                        Block newBlock = CommonUtil.getRandomBlock(chunk, distribution);
                        if (!usedBlocks.contains(newBlock) && Objects.equals(newBlock.getType(), block.getType())) {
                            if (Objects.isNull(biomes) || biomes.contains(newBlock.getBiome())) {
                                if (!gs.getBoundingBox().contains(newBlock.getBoundingBox())) {
                                    continue;
                                }
                                setBlock(newBlock.getLocation(), block);
                            }
                        }
                        usedBlocks.add(newBlock);
                    }
                }
            } else if (block.isGaussian()) {
                for (int i = 0; i < maxPerChunk; i++) {
                    Block newBlock = CommonUtil.getGaussianRandomBlock(chunk, distribution, block.getGaussianMean(), block.getGaussianStdDev());
                    if (!usedBlocks.contains(newBlock) && Objects.equals(newBlock.getType(), block.getType())) {
                        if (Objects.isNull(biomes) || biomes.contains(newBlock.getBiome())) {
                            setBlock(newBlock.getLocation(), block);
                        }
                    }
                    usedBlocks.add(newBlock);
                }
            } else {
                for (int i = 0; i < maxPerChunk; i++) {
                    Block newBlock = CommonUtil.getRandomBlock(chunk, distribution);
                    if (!usedBlocks.contains(newBlock) && Objects.equals(newBlock.getType(), block.getType())) {
                        if (Objects.isNull(biomes) || biomes.contains(newBlock.getBiome())) {
                            setBlock(newBlock.getLocation(), block);
                        }
                    }
                    usedBlocks.add(newBlock);
                }
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
    /*private void setStructureBlock(Location location, ArcheologyBlock block) {
        ChunkManager.getInstance().getPersistentDataChunk(location).registerNewBlock(block, location);
    }
    private Block getChunkBlock() {
        return chunk.getWorld().getBlockAt(chunk.getX() * 16 + 8, 0, chunk.getZ() * 16 + 8);
    }
     */
}

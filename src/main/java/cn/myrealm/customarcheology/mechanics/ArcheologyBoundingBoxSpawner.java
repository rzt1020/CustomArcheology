package cn.myrealm.customarcheology.mechanics;

import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import cn.myrealm.customarcheology.managers.managers.ChunkManager;
import cn.myrealm.customarcheology.mechanics.cores.ArcheologyBlock;
import cn.myrealm.customarcheology.utils.CommonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;

import java.util.List;
import java.util.*;

/**
 * @author PQguanfang
 */
public class ArcheologyBoundingBoxSpawner {
    private final World world;
    private final List<ArcheologyBlock> blocks;
    private final BoundingBox boundingBox;
    private final String[] args;

    public ArcheologyBoundingBoxSpawner(World world, BoundingBox boundingBox, String... args) {
        this.world = world;
        this.boundingBox = boundingBox;
        this.blocks = BlockManager.getInstance().getBlocks(world);
        this.args = args;
        spawnBlocks();
    }

    private void spawnBlocks() {
        Set<Block> usedBlocks = new HashSet<>();
        blocks.forEach(block -> {
            int maxPerChunk = block.getMaxPerChunk();
            List<Biome> biomes = block.getBiomes();
            if (block.isBetterStructure() && args.length > 0) {
                if (!block.containsBetterStructure(args[0])) {
                    return;
                }
                for (int i = 0; i < maxPerChunk; i++) {
                    Block newBlock = CommonUtil.getRandomBlock(world, boundingBox);
                    if (!usedBlocks.contains(newBlock) && Objects.equals(newBlock.getType(), block.getType())) {
                        if (Objects.isNull(biomes) || biomes.contains(newBlock.getBiome())) {
                            Bukkit.getScheduler().runTaskLater(CustomArcheology.plugin, () -> {
                                setBlock(newBlock.getLocation(), block);
                            }, 2L);
                        }
                    }
                    usedBlocks.add(newBlock);
                }
            }
        });
    }

    private void setBlock(Location location, ArcheologyBlock block) {
        if (Config.LOG_GENERATED_BLOCK.asBoolean()) {
            Bukkit.getConsoleSender().sendMessage("§x§9§8§F§B§9§8[CustomArcheology] §fGenerated " + block.getName() + " archeology block at: " +
                    location.getWorld().getName() + ", " + location.getBlockX() + ", " +
                    location.getBlockY() + ", " + location.getBlockZ() + ", rule: BoundingBox!");
        }
        ChunkManager.getInstance().getPersistentDataChunk(location).registerNewBlock(block, location);
    }
}

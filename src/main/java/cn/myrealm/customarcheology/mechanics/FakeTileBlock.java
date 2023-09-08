package cn.myrealm.customarcheology.mechanics;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import cn.myrealm.customarcheology.utils.PacketUtil;
import cn.myrealm.customarcheology.utils.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

/**
 * @author rzt10
 */
public class FakeTileBlock {
    private final String blockName;
    private final ArcheologyBlock block;
    private final int entityId;
    private final Set<Player> sentPlayers = new HashSet<>();
    private final Location location;
    private ItemStack reward;
    private boolean isPlaying;
    public FakeTileBlock(String blockName, Location location) {
        this.blockName = blockName;
        this.location = location;
        String suffix = blockName.split("_")[blockName.split("_").length - 1];
        String blockId = blockName.replace("_" + suffix, "");
        BlockManager blockManager = BlockManager.getInstance();
        this.block = blockManager.getBlock(blockId);
        this.entityId = Integer.parseInt(suffix);
    }
    public String getBlockName() {
        return blockName;
    }
    public void placeBlock() {
        if (isPlaying) {
            return;
        }
        List<Player> players = PlayerUtil.getNearbyPlayers(location);
        players.removeAll(sentPlayers);
        sentPlayers.addAll(players);
        if (players.isEmpty()) {
            return;
        }
        PacketUtil.changeBlock(players, location, Material.BARRIER);
        PacketUtil.spawnItemDisplay(players, location, block.generateItemStack(1), entityId, null, null);
    }
    public void removeBlock() {
        List<Player> players = Objects.requireNonNull(location.getWorld()).getPlayers();
        if (players.isEmpty()) {
            return;
        }
        PacketUtil.removeEntity(players, entityId);
    }

    public boolean isValid() {
        return Objects.nonNull(block);
    }

    public ArcheologyBlock getArcheologyBlock() {
        return block;
    }

    public void setReward(ItemStack reward) {
        this.reward = reward;
    }

    public void play(BlockFace blockFace) {
        isPlaying = true;
        List<Player> players = PlayerUtil.getNearbyPlayers(location);
        if (Objects.isNull(reward)) {
            reward = block.roll();
        }
        Vector3f scale;
        if (reward.getType().isBlock()) {
            float size = (float) Config.BLOCK_SCALE.asDouble();
            scale = new Vector3f(size, size, size).normalize();
        } else {
            float size = (float) Config.ITEM_SCALE.asDouble();
            scale = new Vector3f(size, size, size).normalize();
        }
        Quaternionf rotation = null;
        if (blockFace.equals(BlockFace.NORTH) || blockFace.equals(BlockFace.SOUTH)) {
            rotation = new Quaternionf(0, 1, 0, 0).normalize();
        }
        Location rewardLocation = location.clone();
        PacketUtil.spawnItemDisplay(players, rewardLocation, reward, entityId+1, scale, rotation);
        Queue<BukkitRunnable> tasksQueue = new ArrayDeque<>();
        Stack<BukkitRunnable> tasksStack = new Stack<>();
        List<State> states = block.getStates();
        for (int i = 0; i < states.size(); i++) {
            int finalI = i;
            tasksQueue.add(new BukkitRunnable() {
                @Override
                public void run() {
                    List<Player> players = Objects.requireNonNull(location.getWorld()).getPlayers();
                    if (players.isEmpty()) {
                        return;
                    }
                    PacketUtil.removeEntity(players, entityId);
                    PacketUtil.spawnItemDisplay(players, location, block.generateItemStack(1, finalI), entityId, null, null);
                    BukkitRunnable task = tasksQueue.remove();
                    task.runTaskLater(CustomArcheology.plugin, (long) (20 * states.get(finalI).getHardness()));
                    tasksStack.push(this);
                }
            });
        }
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                List<Player> players = Objects.requireNonNull(location.getWorld()).getPlayers();
                if (players.isEmpty()) {
                    return;
                }
                PacketUtil.removeEntity(players, entityId);
                PacketUtil.spawnItemDisplay(players, location, block.generateItemStack(1), entityId, null, null);
                BukkitRunnable task = tasksQueue.remove();
                task.runTaskLater(CustomArcheology.plugin, (long) (20 * block.getDefaultState().getHardness()));
                tasksStack.push(this);
            }
        };
        task.run();
        tasksStack.push(task);
    }

    public void cancel() {
    }
}

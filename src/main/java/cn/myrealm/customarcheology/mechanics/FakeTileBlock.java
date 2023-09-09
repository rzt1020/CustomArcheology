package cn.myrealm.customarcheology.mechanics;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import cn.myrealm.customarcheology.managers.managers.ChunkManager;
import cn.myrealm.customarcheology.managers.managers.PlayerManager;
import cn.myrealm.customarcheology.utils.PacketUtil;
import cn.myrealm.customarcheology.utils.PlayerUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
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
    private boolean isPlaying = false,
                    isEffectInitialized = false;
    private final Stack<Integer> order = new Stack<>(), complete = new Stack<>();
    private final Map<Integer, EffectTask> taskMap = new HashMap<>();
    private EffectTask nextTask;


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
        PacketUtil.changeBlock(players, location, location.getBlock().getType());
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
        System.out.println("Play");
        isPlaying = true;
        if (!isEffectInitialized) {
            effectInit(blockFace);
            spawnReward(blockFace);
            int taskId = order.pop();
            EffectTask task = taskMap.get(taskId).cloneTask();
            task.run();
            complete.push(taskId);
        } else {
            if (Objects.nonNull(nextTask) && !nextTask.isCancelled()) {
                nextTask.cancel();
            }
            int taskId = order.pop();
            nextTask = taskMap.get(taskId).cloneTask();
            nextTask.runTaskLater(CustomArcheology.plugin, (long) (nextTask.getState().getHardness() * 20));
            complete.push(taskId);
        }

    }

    private void effectInit(BlockFace blockFace) {
        order.clear();
        complete.clear();
        Vector vector;
        switch (blockFace) {
            case UP:
                vector = new Vector(0, 0.2, 0);
                break;
            case DOWN:
                vector = new Vector(0, -0.2, 0);
                break;
            case NORTH:
                vector = new Vector(0, 0, -0.2);
                break;
            case SOUTH:
                vector = new Vector(0, 0, 0.2);
                break;
            case WEST:
                vector = new Vector(-0.2, 0, 0);
                break;
            case EAST:
                vector = new Vector(0.2, 0, 0);
                break;
            default:
                vector = new Vector(0, 0, 0);
                break;
        }
        Location loc = location.clone();
        taskMap.put(0, new EffectTask(this, block.getDefaultState(), loc.clone()));
        for (int i = 1; i <= block.getStates().size(); i++) {
            taskMap.put(i, new EffectTask(this, block.getStates().get(i-1), loc.add(vector).clone()));
        }
        taskMap.put(block.getStates().size()+1, new EffectTask(this, block.getFinishedState(), loc.clone()));
        for (int i = taskMap.size() - 1; i >= 0 ; i--) {
            order.push(i);
        }
        isEffectInitialized = true;
    }
    public void spawnReward(BlockFace blockFace) {
        List<Player> players = PlayerUtil.getNearbyPlayers(location);
        if (Objects.isNull(reward)) {
            reward = block.roll();
        }
        Vector3f scale;
        if (reward.getType().isBlock()) {
            float size = (float) Config.BLOCK_SCALE.asDouble();
            scale = new Vector3f(size, size, size);
        } else {
            float size = (float) Config.ITEM_SCALE.asDouble();
            scale = new Vector3f(size, size, size);
        }

        Quaternionf rotation = null;
        if (blockFace.equals(BlockFace.NORTH) || blockFace.equals(BlockFace.SOUTH)) {
            rotation = new Quaternionf(0, -1, 0, -1).normalize();
        }
        PacketUtil.spawnItemDisplay(players, location, reward, entityId+1, scale, rotation);
    }
    public void effect(State state, Location location) {
        List<Player> players = PlayerUtil.getNearbyPlayers(location);

        if (state.isFinished) {
            PacketUtil.removeEntity(players, entityId+1);
            PacketUtil.removeEntity(players, entityId);
            PacketUtil.changeBlock(players, this.location, state.getMaterial());
            Item item = (Item) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.DROPPED_ITEM);
            item.setItemStack(reward);
            PlayerManager playerManager = PlayerManager.getInstance();
            playerManager.cancelBlock(this);
            ChunkManager chunkManager = ChunkManager.getInstance();
            chunkManager.removeBlock(this.location);
        } else {
            PacketUtil.teleportEntity(players, entityId+1, location);
            PacketUtil.updateItemDisplay(players, block.generateItemStack(1, state), entityId,  null, null);
        }

        if (isPlaying) {
            if (!order.isEmpty()) {
                int taskId = order.pop();
                nextTask = taskMap.get(taskId).cloneTask();
                nextTask.runTaskLater(CustomArcheology.plugin, (long) (state.getHardness() * 20));
                complete.push(taskId);
            }
        } else {
            if (complete.isEmpty()) {
                PacketUtil.removeEntity(players, entityId+1);
                isEffectInitialized = false;
            } else {
                int taskId = complete.pop();
                nextTask = taskMap.get(taskId).cloneTask();
                nextTask.runTaskLater(CustomArcheology.plugin, 5L);
                order.push(taskId);
            }
        }
    }

    public void pause() {
        System.out.println("Pause");
        isPlaying = false;

        if (Objects.nonNull(nextTask) && !nextTask.isCancelled()) {
            nextTask.cancel();
        }
        if (complete.isEmpty()) {
            return;
        }
        int taskId = complete.pop();
        nextTask = taskMap.get(taskId).cloneTask();
        nextTask.runTaskLater(CustomArcheology.plugin, 20L);
        order.push(taskId);
    }
}

class EffectTask extends BukkitRunnable{
    private final FakeTileBlock block;
    private final State state;
    private final Location location;
    EffectTask(FakeTileBlock block, State state, Location location) {
        this.block = block;
        this.state = state;
        this.location = location;
    }

    @Override
    public void run() {
        block.effect(state, location);
    }
    public State getState() {
        return state;
    }
    public EffectTask cloneTask() {
        return new EffectTask(block, state, location);
    }
}

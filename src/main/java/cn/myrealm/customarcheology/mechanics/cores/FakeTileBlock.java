package cn.myrealm.customarcheology.mechanics.cores;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Config;
import cn.myrealm.customarcheology.managers.managers.BlockManager;
import cn.myrealm.customarcheology.managers.managers.ChunkManager;
import cn.myrealm.customarcheology.managers.managers.PlayerManager;
import cn.myrealm.customarcheology.managers.managers.ToolManager;
import cn.myrealm.customarcheology.utils.ItemUtil;
import cn.myrealm.customarcheology.utils.PacketUtil;
import cn.myrealm.customarcheology.utils.BasicUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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
 * @author rzt1020
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
    private BukkitRunnable particleTask;
    private double efficiency = 1.0;


    public FakeTileBlock(String blockName, Location location, ItemStack reward) {
        this.blockName = blockName;
        this.location = location;
        String[] nameParts = blockName.split("_");
        String suffix = nameParts[nameParts.length - 1];
        String blockId = blockName.replace("_" + suffix, "");
        this.block = BlockManager.getInstance().getBlock(blockId);
        this.entityId = Integer.parseInt(suffix);
        this.reward = reward;
    }

    public String getBlockName() {
        return blockName;
    }

    public void placeBlock() {
        if (isPlaying) {
            return;
        }

        List<Player> players = BasicUtil.getNearbyPlayers(location);
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

    public void play(BlockFace blockFace, ItemStack tool) {
        if (isPlaying) {
            return;
        }
        isPlaying = true;
        efficiency = block.getEfficiency(tool);
        if (!isEffectInitialized) {
            effectInit(blockFace);
            spawnReward(blockFace);
            int taskId = order.pop();
            nextTask = taskMap.get(taskId).cloneTask();
            nextTask.run();
        } else {
            if (Objects.nonNull(nextTask) ) {
                complete.push(nextTask.getTaskId());
            }
            int taskId = order.pop();
            nextTask = taskMap.get(taskId).cloneTask();
            nextTask.runTaskLater(CustomArcheology.plugin, (long) (nextTask.getState().getHardness() * 20 / efficiency));
        }
        playParticleEffect(blockFace);
    }

    private void playParticleEffect(BlockFace blockFace) {
        Location location = this.location.clone().add(0.5, 0.5, 0.5);
        Location finalLocation = switch (blockFace) {
            case UP -> location.add(0, 0.5, 0);
            case DOWN -> location.add(0, -0.5, 0);
            case NORTH -> location.add(0, 0, -0.5);
            case SOUTH -> location.add(0, 0, 0.5);
            case WEST -> location.add(-0.5, 0, 0);
            case EAST -> location.add(0.5, 0, 0);
            default -> location.add(0, 0, 0);
        };
        particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                Objects.requireNonNull(finalLocation.getWorld()).spawnParticle(Particle.BLOCK_DUST, finalLocation, 5, 0.1, 0.1, 0.1, block.getType().createBlockData());
            }
        };
        particleTask.runTaskTimer(CustomArcheology.plugin, 0, 10);
    }

    private void effectInit(BlockFace blockFace) {
        order.clear();
        complete.clear();
        Vector vector = switch (blockFace) {
            case UP -> new Vector(0, 0.2, 0);
            case DOWN -> new Vector(0, -0.2, 0);
            case NORTH -> new Vector(0, 0, -0.2);
            case SOUTH -> new Vector(0, 0, 0.2);
            case WEST -> new Vector(-0.2, 0, 0);
            case EAST -> new Vector(0.2, 0, 0);
            default -> new Vector(0, 0, 0);
        };
        Location loc = location.clone();
        taskMap.put(0, new EffectTask(this, block.getDefaultState(), loc.clone(), 0));
        for (int i = 1; i <= block.getStates().size(); i++) {
            taskMap.put(i, new EffectTask(this, block.getStates().get(i-1), loc.add(vector).clone(), i));
        }
        taskMap.put(block.getStates().size()+1, new EffectTask(this, block.getFinishedState(), loc.clone(), block.getStates().size()+1));
        for (int i = taskMap.size() - 1; i >= 0 ; i--) {
            order.push(i);
        }
        isEffectInitialized = true;
    }

    public void spawnReward(BlockFace blockFace) {
        List<Player> players = BasicUtil.getNearbyPlayers(location);
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

    public void effect(EffectTask task, State state, Location location) {
        if (Objects.isNull(task) || Objects.isNull(nextTask) || task.getTaskId() != nextTask.getTaskId()) {
            return;
        }

        List<Player> players = BasicUtil.getNearbyPlayers(location);

        if (state.isFinished) {
            if (Objects.nonNull(particleTask) && !particleTask.isCancelled()) {
                particleTask.cancel();
            }
            PacketUtil.removeEntity(players, entityId+1);
            PacketUtil.removeEntity(players, entityId);
            PacketUtil.changeBlock(players, this.location, state.getMaterial());
            Item item = (Item) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.DROPPED_ITEM);
            item.setItemStack(reward);
            PlayerManager playerManager = PlayerManager.getInstance();
            playerManager.cancelBlock(this);
            ChunkManager chunkManager = ChunkManager.getInstance();
            chunkManager.removeBlock(this.location);
            this.location.getBlock().setType(state.getMaterial());
            Objects.requireNonNull(this.location.getWorld()).spawnParticle(Particle.BLOCK_DUST, this.location.clone().add(0.5,0.5,0.5), 100, 0.3, 0.3, 0.3, block.getType().createBlockData());
        } else {
            PacketUtil.teleportEntity(players, entityId+1, location);
            PacketUtil.updateItemDisplay(players, block.generateItemStack(1, state), entityId,  null, null);
        }

        if (isPlaying) {
            complete.push(task.getTaskId());
            if (!order.isEmpty()) {
                int taskId = order.pop();
                nextTask = taskMap.get(taskId).cloneTask();
                nextTask.runTaskLater(CustomArcheology.plugin, (long) (state.getHardness() * 20 / efficiency));
            } else {
                nextTask = null;
            }
        } else {
            order.push(task.getTaskId());
            if (complete.isEmpty()) {
                PacketUtil.removeEntity(players, entityId+1);
                PacketUtil.updateItemDisplay(players, block.generateItemStack(1), entityId, null, null);
                isEffectInitialized = false;
                nextTask = null;
            } else {
                int taskId = complete.pop();
                nextTask = taskMap.get(taskId).cloneTask();
                nextTask.runTaskLater(CustomArcheology.plugin, 5L);
            }
        }
    }


    public void pause() {
        if (Objects.nonNull(particleTask) && !particleTask.isCancelled()) {
            particleTask.cancel();
        }
        if (!isPlaying) {
            return;
        }
        isPlaying = false;

        if (Objects.nonNull(nextTask)) {
            order.push(nextTask.getTaskId());
        }
        if (complete.isEmpty()) {
            return;
        }
        int taskId = complete.pop();
        nextTask = taskMap.get(taskId).cloneTask();
        nextTask.runTaskLater(CustomArcheology.plugin, 40L);

    }


    public ItemStack getReward() {
        return reward;
    }

    public void highlight(Player player) {
        if (sentPlayers.contains(player)) {
            PacketUtil.highlightEntity(List.of(player), entityId);
        }
    }
}

class EffectTask extends BukkitRunnable{
    private final FakeTileBlock block;
    private final State state;
    private final Location location;
    private final int taskId;
    EffectTask(FakeTileBlock block, State state, Location location, int taskId) {
        this.block = block;
        this.state = state;
        this.location = location;
        this.taskId = taskId;
    }

    @Override
    public void run() {
        block.effect(this, state, location);
    }
    public State getState() {
        return state;
    }
    public EffectTask cloneTask() {
        return new EffectTask(block, state, location, taskId);
    }
    @Override
    public int getTaskId() {
        return taskId;
    }
}

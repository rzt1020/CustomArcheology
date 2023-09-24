package cn.myrealm.customarcheology.utils;


import cn.myrealm.customarcheology.CustomArcheology;
import cn.myrealm.customarcheology.enums.Permissions;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;


/**
 * @author rzt1020
 */
public class PacketUtil {
    private PacketUtil() {
    }
    public static void swingItem(Player player) {
        PacketContainer animationPacket = CustomArcheology.protocolManager.createPacket(PacketType.Play.Server.ANIMATION);

        animationPacket.getIntegers().write(0, player.getEntityId());
        animationPacket.getIntegers().write(1, 0);

        CustomArcheology.protocolManager.sendServerPacket(player, animationPacket);
    }
    public static void changeBlock(List<Player> player, Location blockLocation, Material material) {
        PacketContainer blockChangePacket = CustomArcheology.protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);

        blockChangePacket.getBlockPositionModifier().write(0, new BlockPosition(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ()));
        blockChangePacket.getBlockData().write(0, WrappedBlockData.createData(material));

        for (Player p : player) {
            CustomArcheology.protocolManager.sendServerPacket(p, blockChangePacket);
        }
    }
    public static void spawnItemDisplay(List<Player> player, Location location, ItemStack displayItem, int entityId, @Nullable Vector3f scale, @Nullable Quaternionf rotation) {
        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        UUID entityUuid = UUID.randomUUID();

        spawnPacket.getIntegers()
                .write(0, entityId);
        spawnPacket.getDoubles().write(0, location.getX() + 0.5d)
                                .write(1, location.getY() + 0.5d)
                                .write(2, location.getZ() + 0.5d);

        spawnPacket.getUUIDs().write(0, entityUuid);
        spawnPacket.getEntityTypeModifier().write(0, EntityType.ITEM_DISPLAY);


        for (Player p : player) {
            CustomArcheology.protocolManager.sendServerPacket(p, spawnPacket);
        }
        updateItemDisplay(player, displayItem, entityId, scale, rotation);
    }
    public static void updateItemDisplay(List<Player> player, ItemStack displayItem, int entityId, @Nullable Vector3f scale, @Nullable Quaternionf rotation) {
        PacketContainer metaDataPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        PacketContainer glowMetaDataPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaDataPacket.getIntegers().write(0, entityId);
        glowMetaDataPacket.getIntegers().write(0, entityId);

        WrappedDataWatcher entityMetaData = new WrappedDataWatcher();
        if (Objects.nonNull(scale)) {
            entityMetaData.setObject(11, WrappedDataWatcher.Registry.get(Vector3f.class), scale);
        }
        if (Objects.nonNull(rotation)) {
            entityMetaData.setObject(12, WrappedDataWatcher.Registry.get(Quaternionf.class), rotation);
        }
        entityMetaData.setObject(22, WrappedDataWatcher.Registry.getItemStackSerializer(false), displayItem);

        List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();
        for (WrappedWatchableObject entry : entityMetaData.getWatchableObjects()) {
            if (entry != null) {
                WrappedDataWatcher.WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
                wrappedDataValueList.add(new WrappedDataValue(watcherObject.getIndex(), watcherObject.getSerializer(), entry.getRawValue()));
            }
        }
        metaDataPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);

        byte initialMeta = 0x40;
        entityMetaData.setObject(0, WrappedDataWatcher.Registry.get(Byte.class), initialMeta);

        wrappedDataValueList = new ArrayList<>();
        for (WrappedWatchableObject entry : entityMetaData.getWatchableObjects()) {
            if (entry != null) {
                WrappedDataWatcher.WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
                wrappedDataValueList.add(new WrappedDataValue(watcherObject.getIndex(), watcherObject.getSerializer(), entry.getRawValue()));
            }
        }
        glowMetaDataPacket.getDataValueCollectionModifier().write(0, wrappedDataValueList);

        for (Player p : player) {
            if (!Permissions.PLAY_HIGHLIGHT.hasPermission(p)) {
                CustomArcheology.protocolManager.sendServerPacket(p, metaDataPacket);
            } else {
                CustomArcheology.protocolManager.sendServerPacket(p, glowMetaDataPacket);
            }
        }


    }

    public static void removeEntity(List<Player> player, int entityId) {
        PacketContainer entityDestroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);

        entityDestroyPacket.getIntLists().write(0, Collections.singletonList(entityId));

        for (Player p : player) {
            CustomArcheology.protocolManager.sendServerPacket(p, entityDestroyPacket);
        }
    }

    public static void teleportEntity(List<Player> players, int entityId, Location location) {
        PacketContainer teleportPacket = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);


        teleportPacket.getIntegers().write(0, entityId);
        teleportPacket.getDoubles().write(0, location.getX() + 0.5);
        teleportPacket.getDoubles().write(1, location.getY() + 0.5);
        teleportPacket.getDoubles().write(2, location.getZ() + 0.5);


        for (Player player : players) {
            CustomArcheology.protocolManager.sendServerPacket(player, teleportPacket);
        }
    }

    public static void highlightEntity(List<Player> players, int entityId) {

        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer boolSerializer = WrappedDataWatcher.Registry.get(Boolean.class);

        // Index might vary based on MC version. Assuming index 0 is for entity glowing.
        watcher.setObject(0, boolSerializer, true);

        PacketContainer entityMetadataPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        entityMetadataPacket.getIntegers().write(0, entityId);
        entityMetadataPacket.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

        for (Player player : players) {
            CustomArcheology.protocolManager.sendServerPacket(player, entityMetadataPacket);
        }
    }
}

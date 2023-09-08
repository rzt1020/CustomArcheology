package cn.myrealm.customarcheology.utils;


import cn.myrealm.customarcheology.CustomArcheology;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.joml.Vector3f;
import org.joml.Quaternionf;

import java.util.*;


/**
 * @author rzt10
 */
public class PacketUtil {
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
    public static int spawnItemDisplay(List<Player> player, Location location, ItemStack displayItem, int entityId, @Nullable Vector3f scale, @Nullable Quaternionf rotation) {
        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        UUID entityUuid = UUID.randomUUID();

        spawnPacket.getIntegers()
                .write(0, entityId);
        spawnPacket.getDoubles().write(0, location.getX() + 0.5d)
                                .write(1, location.getY() + 0.5d)
                                .write(2, location.getZ() + 0.5d);

        spawnPacket.getUUIDs().write(0, entityUuid);
        spawnPacket.getEntityTypeModifier().write(0, EntityType.ITEM_DISPLAY);


        PacketContainer metaDataPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);
        metaDataPacket.getIntegers().write(0, entityId);

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
        for (Player p : player) {
            CustomArcheology.protocolManager.sendServerPacket(p, spawnPacket);
            CustomArcheology.protocolManager.sendServerPacket(p, metaDataPacket);
        }
        return entityId;
    }

    public static void removeEntity(List<Player> player, int entityId) {
        PacketContainer entityDestroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);

        entityDestroyPacket.getIntLists().write(0, Collections.singletonList(entityId));

        for (Player p : player) {
            CustomArcheology.protocolManager.sendServerPacket(p, entityDestroyPacket);
        }

    }
}

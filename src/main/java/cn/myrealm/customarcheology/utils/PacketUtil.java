package cn.myrealm.customarcheology.utils;


import cn.myrealm.customarcheology.enums.Permissions;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Quaternion4f;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;


/**
 * @author rzt1020
 */
public class PacketUtil {
    private PacketUtil() {
    }
    public static void swingItem(Player player) {

        WrapperPlayServerEntityAnimation packet = new WrapperPlayServerEntityAnimation(player.getEntityId(),
                WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
    }
    public static void spawnItemDisplay(List<Player> player,
                                        org.bukkit.Location location,
                                        org.bukkit.inventory.ItemStack displayItem,
                                        int entityId,
                                        @Nullable Vector3f scale,
                                        @Nullable Quaternion4f rotation) {
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity(
                entityId,
                UUID.randomUUID(),
                EntityTypes.ITEM_DISPLAY,
                new Location(location.getX() + 0.5, location.getY() + 0.5, location.getZ() + 0.5, location.getYaw(), location.getPitch()),
                location.getYaw(),
                0,
                null);

        for (Player p : player) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(p, packet);
        }
        updateItemDisplay(player, displayItem, entityId, scale, rotation);
    }
    public static void updateItemDisplay(List<Player> player,
                                         org.bukkit.inventory.ItemStack displayItem,
                                         int entityId,
                                         @Nullable Vector3f scale,
                                         @Nullable Quaternion4f rotation) {
        List<EntityData> data = new ArrayList<>();
        if (!CommonUtil.getMinorVersion(20, 2)) {
            if (Objects.nonNull(scale)) {
                data.add(new EntityData(11, EntityDataTypes.VECTOR3F, scale));
            }
            if (Objects.nonNull(rotation)) {
                data.add(new EntityData(12, EntityDataTypes.QUATERNION, rotation));
            }
            data.add(new EntityData(22, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(displayItem)));
        } else {
            if (Objects.nonNull(scale)) {
                data.add(new EntityData(12, EntityDataTypes.VECTOR3F, scale));
            }
            if (Objects.nonNull(rotation)) {
                data.add(new EntityData(13, EntityDataTypes.QUATERNION, rotation));
            }
            data.add(new EntityData(23, EntityDataTypes.ITEMSTACK, SpigotConversionUtil.fromBukkitItemStack(displayItem)));
        }

        WrapperPlayServerEntityMetadata metaDataPacket = new WrapperPlayServerEntityMetadata(
                entityId,
                data
        );

        List<EntityData> otherData = new ArrayList<>(data);
        byte tempByte = 0x40;
        otherData.add(new EntityData(0, EntityDataTypes.BYTE, tempByte));

        WrapperPlayServerEntityMetadata glowMetaDataPacket = new WrapperPlayServerEntityMetadata(
                entityId,
                otherData
        );

        for (Player p : player) {
            if (!Permissions.PLAY_HIGHLIGHT.hasPermission(p)) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(p, metaDataPacket);
            } else {
                PacketEvents.getAPI().getPlayerManager().sendPacket(p, glowMetaDataPacket);
            }
        }


    }

    public static void removeEntity(List<Player> player, int entityId) {
        WrapperPlayServerDestroyEntities packet = new WrapperPlayServerDestroyEntities(entityId);

        for (Player p : player) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(p, packet);
        }
    }

    public static void teleportEntity(List<Player> players, int entityId, org.bukkit.Location location) {
        try {
            WrapperPlayServerEntityTeleport packet = new WrapperPlayServerEntityTeleport(entityId,
                    new Location(location.getX() + 0.5, location.getY() + 0.5, location.getZ() + 0.5, location.getYaw(), location.getPitch()), true);

            for (Player player : players) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}

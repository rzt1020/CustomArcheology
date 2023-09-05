package cn.myrealm.customarcheology.utils;


import cn.myrealm.customarcheology.CustomArcheology;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

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
    public static void changeBlock(Player player, Location blockLocation, Material material) {
        PacketContainer blockChangePacket = CustomArcheology.protocolManager.createPacket(PacketType.Play.Server.BLOCK_CHANGE);

        blockChangePacket.getBlockPositionModifier().write(0, new BlockPosition(blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ()));
        blockChangePacket.getBlockData().write(0, WrappedBlockData.createData(material));

        CustomArcheology.protocolManager.sendServerPacket(player, blockChangePacket);
        System.out.println(1);
    }
}

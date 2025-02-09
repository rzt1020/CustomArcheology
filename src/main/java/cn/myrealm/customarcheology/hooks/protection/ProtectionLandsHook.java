package cn.myrealm.customarcheology.hooks.protection;

import cn.myrealm.customarcheology.CustomArcheology;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.land.LandWorld;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtectionLandsHook extends AbstractProtectionHook {

    public LandsIntegration api = LandsIntegration.of(CustomArcheology.plugin);

    public ProtectionLandsHook() {
        super("Lands");
    }

    @Override
    public boolean canBreak(Player player, Location location) {
        LandWorld world = api.getWorld(location.getWorld());
        if (world != null) {
            return world.hasRoleFlag(api.getLandPlayer(player.getUniqueId()),
                    location,
                    Flags.BLOCK_BREAK, location.getBlock().getType(),
                    false);
        }
        return true;
    }

    @Override
    public boolean canPlace(Player player, Location location) {
        LandWorld world = api.getWorld(location.getWorld());
        if (world != null) {
            return world.hasRoleFlag(api.getLandPlayer(player.getUniqueId()),
                    location,
                    Flags.BLOCK_PLACE, location.getBlock().getType(),
                    false);
        }
        return true;
    }
}

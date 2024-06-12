package cn.myrealm.customarcheology.hooks;

import cn.myrealm.customarcheology.listeners.BaseListener;
import cn.myrealm.customarcheology.mechanics.ArcheologyBoundingBoxSpawner;
import com.magmaguy.betterstructures.api.BuildPlaceEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class BetterStructuresHook extends BaseListener {

    public BetterStructuresHook(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onBuildPlace(BuildPlaceEvent event) {
        Location lowestLocation = event.getFitAnything().getLocation().clone().add(event.getFitAnything().getSchematicOffset());
        BoundingBox boundingBox = BoundingBox.of(lowestLocation, lowestLocation.clone().add(
                new Vector(event.getFitAnything().getSchematicClipboard().getRegion().getWidth() - 1,
                        event.getFitAnything().getSchematicClipboard().getRegion().getHeight(),
                        event.getFitAnything().getSchematicClipboard().getRegion().getLength() - 1)
        ));
        new ArcheologyBoundingBoxSpawner(event.getFitAnything().getLocation().getWorld(), boundingBox,
                event.getFitAnything().getSchematicContainer().getConfigFilename().replace(".yml", ""));
    }
}

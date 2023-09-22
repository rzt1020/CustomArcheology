package cn.myrealm.customarcheology.mechanics.persistent_data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


/**
 * @author rzt1020
 */
public class LocationTagType implements PersistentDataType<byte[], Location> {

    private final Charset charset = StandardCharsets.UTF_8;

    @Override
    public @NonNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NonNull Class<Location> getComplexType() {
        return Location.class;
    }

    @Override
    public byte @NonNull[] toPrimitive(Location location, @NonNull PersistentDataAdapterContext context) {
        String locString = Objects.requireNonNull(location.getWorld()).getName() + "," +
                location.getX() + "," +
                location.getY() + "," +
                location.getZ() + "," +
                location.getYaw() + "," +
                location.getPitch();
        return locString.getBytes(charset);
    }

    @Override
    public @NonNull Location fromPrimitive(byte @NonNull[] bytes, @NonNull PersistentDataAdapterContext context) {
        String locString = new String(bytes, charset);
        String[] parts = locString.split(",");

        return new Location(
                Bukkit.getWorld(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]),
                Float.parseFloat(parts[4]),
                Float.parseFloat(parts[5])
        );
    }
}


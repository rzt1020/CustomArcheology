package cn.myrealm.customarcheology.mechanics.persistent_data;


import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author rzt10
 */
public class ItemStackTagType implements PersistentDataType<byte[], ItemStack> {

    @Override
    public @NonNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NonNull Class<ItemStack> getComplexType() {
        return ItemStack.class;
    }

    @Override
    public byte @NonNull [] toPrimitive(@NonNull ItemStack itemStack, @NonNull PersistentDataAdapterContext context) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeObject(itemStack);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new IllegalStateException("Could not convert ItemStack to byte[]", e);
        }
    }

    @Override
    public @NonNull ItemStack fromPrimitive(byte @NonNull [] bytes, @NonNull PersistentDataAdapterContext context) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            return (ItemStack) dataInput.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Could not convert byte[] to ItemStack", e);
        }
    }
}


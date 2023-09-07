package cn.myrealm.customarcheology.mechanics.persistent_data;


import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * import from <a href="https://pastebin.com/JJnLDU2V">...</a>
 * @author LYNXPLAY
 */
public class StringArrayTagType implements PersistentDataType<byte[], String[]> {

    private final Charset charset;

    public StringArrayTagType(Charset charset) {
        this.charset = charset;
    }

    @Override
    public @NonNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NonNull Class<String[]> getComplexType() {
        return String[].class;
    }

    @Override
    public byte @NonNull[] toPrimitive(String[] strings, @NonNull PersistentDataAdapterContext itemTagAdapterContext) {
        byte[][] allStringBytes = new byte[strings.length][];
        int total = 0;
        for (int i = 0; i < allStringBytes.length; i++) {
            byte[] bytes = strings[i].getBytes(charset);
            allStringBytes[i] = bytes;
            total += bytes.length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(total + allStringBytes.length * 4);
        for (byte[] bytes : allStringBytes) {
            buffer.putInt(bytes.length);
            buffer.put(bytes);
        }

        return buffer.array();
    }

    @Override
    public String@NonNull[] fromPrimitive(byte @NonNull[] bytes, @NonNull PersistentDataAdapterContext itemTagAdapterContext) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        ArrayList<String> list = new ArrayList<>();

        while (buffer.remaining() > 0) {
            if (buffer.remaining() < 4) {
                break;
            }
            int stringLength = buffer.getInt();
            if (buffer.remaining() < stringLength) {
                break;
            }

            byte[] stringBytes = new byte[stringLength];
            buffer.get(stringBytes);

            list.add(new String(stringBytes, charset));
        }
        String [] array = new String[list.size()];
        list.toArray(array);
        return array;
    }
}

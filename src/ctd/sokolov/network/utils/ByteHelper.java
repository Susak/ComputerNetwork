package ctd.sokolov.network.utils;

/**
 * author: Ruslan Sokolov
 * date: 10/3/14
 */
public class ByteHelper {
    public static byte[] concatenateByteArrays(byte[]... arrays) {
        int size = 0;
        for (byte[] array : arrays) {
            size += array.length;
        }
        byte[] c = new byte[size];
        int cursor = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, c, cursor, array.length);
            cursor += array.length;
        }
        return c;
    }
}

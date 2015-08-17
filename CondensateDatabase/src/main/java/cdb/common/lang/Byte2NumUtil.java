package cdb.common.lang;

/**
 * Convert binary stream to numbers
 * 
 * @author Chao Chen
 * @version $Id: Byte2NumUtil.java, v 0.1 Jul 22, 2015 4:26:08 PM chench Exp $
 */
public final class Byte2NumUtil {

    /**
     * forbidden construction
     */
    private Byte2NumUtil() {

    }

    /**
     * convert bytes to non-signal integer
     * 
     * @param bytes     the bytes to convert
     * @return          the integer corresponding to the given bytes
     */
    public static int byte2int(byte[] bytes) {
        return byte2int(bytes, 0, bytes.length);
    }

    /**
     * convert bytes to non-signal integer    
     * 
     * @param bytes     the bytes to convert
     * @param offset    the index of the first byte to convert
     * @param len       the total length of bytes to convert
     * @return          the integer corresponding to the given bytes
     */
    public static int byte2int(byte[] bytes, int offset, int len) {
        int result = 0;
        for (int i = 0; i < len; i++) {
            result = result | ((bytes[offset + i] & 0xff) << (i * 8));
        }
        return result;
    }

}

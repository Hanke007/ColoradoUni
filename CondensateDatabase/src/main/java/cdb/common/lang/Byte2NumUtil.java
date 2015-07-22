package cdb.common.lang;

/**
 * Convert binary stream to numbers
 * 
 * @author chench
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
        int result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result = result | ((bytes[i] & 0xff) << i * 8);
        }
        return result;
    }
}

package com.sjl.scanner;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ByteUtils
 * @time 2020/8/29 19:32
 * @copyright(C) 2020 song
 */
public class ByteUtils {
    /**
     * 16进制字符串转字节数组
     *
     * @param hex
     * @return
     */
    public static byte[] hexStringToByteArr(String hex) {
        int l = hex.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer.valueOf(hex.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }
}

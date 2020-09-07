package com.sjl.scanner;


/**
 * 扫码指令
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ScanCmd
 * @time 2020/7/2 10:55
 * @copyright(C) 2020 song
 */
public interface ScanCmd {
    /**
     * 扫码触发指令，打开
     */
     byte[] SCAN_OPEN = ByteUtils.hexStringToByteArr("FF540D");
    /**
     * 扫码触发指令，关闭
     */
     byte[] SCAN_CLOSE = ByteUtils.hexStringToByteArr("FF550D");


}

package com.sjl.scanner.listener;

/**
 * USB设备权限申请回调
 *
 * @author Kelly
 * @version 1.0.0
 * @filename UsbPer
 * @time 2022/6/7 14:31
 * @copyright(C) 2022 song
 */
public interface UsbPermissionListener {
    void onGranted();

    void onDenied();
}


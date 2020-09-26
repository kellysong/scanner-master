package com.sjl.scanner;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename UsbErrorCode
 * @time 2020/7/1 13:43
 * @copyright(C) 2020 song
 */
public interface UsbErrorCode {
    /**
     * usb正常状态
     */
    int USB_OK = 0;
    /**
     * USB授权失败
     */
    int USB_PERMISSION_FAIL = -1;
    /**
     * 没有找到指定设备
     */
    int USB_FIND_THIS_FAIL = -2;
    /**
     * 没有找到任何设备
     */
    int USB_FIND_ALL_FAIL = -3;
    /**
     * USB设备打开失败
     */
    int USB_OPEN_FAIL = -4;
    /**
     * USB通道打开失败
     */
    int USB_CHANNEL_FAIL = -5;
    /**
     * USB发送数据失败
     */
    int USB_SEND_DATA_FAIL = -6;
    /**
     * USB读取数据失败
     */
    int USB_READ_DATA_FAIL = -7;

    /**
     * USB未知错误
     */
    int USB_UNKNOWN_FAIL = -99;//USB未知错误
}

package com.sjl.scanner;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ScanMode
 * @time 2020/8/30 13:31
 * @copyright(C) 2020 song
 */
public enum ScanMode {
    //0 usb键盘，自感模式，1 通过usb连接扫码, 发送命令扫码，2 通过usb转串口，自感模式
    USB_KEYBOARD(0), USB_COMMAND(1), USB_AUTO(2);

    private final int mode;

    ScanMode(int mode) {
        this.mode = mode;
    }

}

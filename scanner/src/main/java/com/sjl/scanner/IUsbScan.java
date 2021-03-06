package com.sjl.scanner;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename IUsbScan
 * @time 2020/7/3 18:01
 * @copyright(C) 2020 song
 */
public interface IUsbScan {
    /**
     * 打开指定usb扫码
     *
     * @param usbConfig
     * @return
     */
     int openScan(UsbConfig usbConfig);


    /**
     * 发送数据
     *
     * @param buffer 字节数组
     * @return
     */
     int sendData(byte[] buffer);


    /**
     * 开线程读取数据,必须在正常打开usb的情况下读取数据
     */
     void startReading();


    /**
     * 停止读扫码数据
     */
     void stopReading();

    /**
     * 重连
     */
    void reconnect();

    /**
     * 关闭扫描
     */
    void closeScan();


}

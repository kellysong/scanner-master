package com.sjl.scanner;

import android.content.Context;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ScanHelper
 * @time 2020/7/1 14:28
 * @copyright(C) 2020 song
 */
public class Scanner {

    private Context context;
    private AbstractScan scanInstance;
    private ScanMode scanMode;

    public Scanner(Context context) {
        this.context = context.getApplicationContext();
    }


    /**
     * 初始化扫码模式
     *
     * @param scanMode 扫码模式
     * @return
     */
    public Scanner init(ScanMode scanMode) {
        this.scanMode = scanMode;
        if (scanMode == ScanMode.USB_KEYBOARD) {
            scanInstance = new AutoScan();
        } else if (scanMode == ScanMode.USB_COMMAND) {
            scanInstance = new UsbScan(context);
        } else if (scanMode == ScanMode.USB_AUTO) {
            scanInstance = new AutoUsbScan(context);
        } else {
            throw new IllegalArgumentException("不支持扫码模式：" + scanMode);
        }
        return this;
    }


    public Scanner scanListener(OnScanListener onScanListener) {
        scanInstance.setOnScanListener(onScanListener);
        return this;
    }

    public Scanner run() {
        if (scanMode ==ScanMode.USB_KEYBOARD) {
            AutoScan autoScan = (AutoScan) scanInstance;
//            autoScan.analysisKeyEvent();//TODO
        } else if (scanMode == ScanMode.USB_COMMAND || scanMode ==  ScanMode.USB_AUTO) {
            BaseUsbScan baseUsbScan = (BaseUsbScan) scanInstance;
            baseUsbScan.startReading();
        }
        return this;
    }

    public void stop() {
        if (scanMode == ScanMode.USB_KEYBOARD) {
            AutoScan autoScan = (AutoScan) scanInstance;
        } else if (scanMode == ScanMode.USB_COMMAND || scanMode ==  ScanMode.USB_AUTO) {
            BaseUsbScan baseUsbScan = (BaseUsbScan) scanInstance;
            baseUsbScan.stopReading();
        }
    }

    public void cancel() {
        if (scanMode == ScanMode.USB_KEYBOARD) {
            AutoScan autoScan = (AutoScan) scanInstance;
            autoScan.cancel();
        } else if (scanMode == ScanMode.USB_COMMAND || scanMode ==  ScanMode.USB_AUTO) {
            BaseUsbScan baseUsbScan = (BaseUsbScan) scanInstance;
            baseUsbScan.cancel();
        }
    }
}

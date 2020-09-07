package com.sjl.scanner;

import android.content.Context;
import android.view.KeyEvent;

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
    private BaseUsbScan baseUsbScan;
    private ScanMode scanMode;
    private  AutoScan autoScan;

    /**
     * 初始化扫码器
     * @param context
     * @param scanMode
     */
    public Scanner(Context context,ScanMode scanMode) {
        this.context = context.getApplicationContext();
        this.scanMode = scanMode;
        init(scanMode);
    }

    /**
     * 初始化扫码模式
     *
     * @param scanMode 扫码模式
     * @return
     */
    private void init(ScanMode scanMode) {
        if (scanMode == ScanMode.USB_KEYBOARD) {
            autoScan = new AutoScan();
        } else if (scanMode == ScanMode.USB_COMMAND) {
            baseUsbScan = new UsbScan(context);
        } else if (scanMode == ScanMode.USB_AUTO) {
            baseUsbScan = new AutoUsbScan(context);
        } else {
            throw new IllegalArgumentException("不支持扫码模式：" + scanMode);
        }
    }

    /**
     * 扫码回调监听
     * @param onScanListener
     * @return
     */
    public Scanner scanListener(OnScanListener onScanListener) {
        baseUsbScan.setOnScanListener(onScanListener);
        return this;
    }

    /**
     * 启动扫码，usb
     * @return
     */
    public Scanner start() {
       if (scanMode == ScanMode.USB_COMMAND || scanMode ==  ScanMode.USB_AUTO) {
            baseUsbScan.startReading();
        }
        return this;
    }

    /**
     * 停止扫码，usb
     */
    public void stop() {
        if (scanMode == ScanMode.USB_KEYBOARD && autoScan != null) {
             autoScan.cancel();
        } else if (scanMode == ScanMode.USB_COMMAND || scanMode ==  ScanMode.USB_AUTO) {
            baseUsbScan.stopReading();
        }
    }

    /**
     * 取消扫码
     */
    public void cancel() {
        if (scanMode == ScanMode.USB_KEYBOARD  && autoScan != null) {
            autoScan.cancel();
        } else if (scanMode == ScanMode.USB_COMMAND || scanMode ==  ScanMode.USB_AUTO) {
            baseUsbScan.cancel();
        }
    }

    /**
     * usb 键盘模式，需要注册
     * <p>  @Override
     *     public boolean dispatchKeyEvent(KeyEvent event) {
     *         scanner.dispatchKeyEvent(event);
     *         return super.dispatchKeyEvent(event);
     *     }</p>
     * @param event
     */
    public void dispatchKeyEvent(KeyEvent event) {
        if (scanMode == ScanMode.USB_KEYBOARD  && autoScan != null) {
            autoScan.analysisKeyEvent(event);
        }
    }
}

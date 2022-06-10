package com.sjl.scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.SystemClock;

import com.sjl.scanner.listener.OnScanListener;
import com.sjl.scanner.util.LogUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BaseUsbScan
 * @time 2020/7/3 18:40
 * @copyright(C) 2020 song
 */
public abstract class BaseUsbScan implements IUsbScan {
    protected final UsbManager mUsbManager;
    protected BroadcastReceiver broadcastReceiver;

    protected static final int MAX_RECONNECT_COUNT = 5;
    protected int reconnectCount = 0;
    protected boolean connected = false;

    protected boolean readFlag = false;
    protected Context mContext;
    protected static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    protected UsbConfig usbConfig;
    protected OnScanListener mOnScanListener;


    public BaseUsbScan(Context context) {
        this.mContext = context.getApplicationContext();
        //1.创建usbManager
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
    }


    /**
     * 查找UsbDevice,一般来说每个usb孔的vendor-id（厂商 id） 和 product-id （产品 id)不一样
     *
     * @param usbConfig
     * @return
     */
    protected UsbDevice findUsbDevice(UsbConfig usbConfig) {
        this.usbConfig = usbConfig;
        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        //2.获取到所有设备 选择出满足的设备
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        UsbDevice usbDevice = null;
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            LogUtils.i("id:" + device.getDeviceId() + ",mName:" + device.getDeviceName() + "，vendorID:" + device.getVendorId() + ",ProductId:" + device.getProductId());
            if (device.getVendorId() == usbConfig.getVendorId() && device.getProductId() == usbConfig.getProductId()) {
                usbDevice = device; // 获取USBDevice
                break;
            }
        }
        return usbDevice;
    }



    /**
     * 分发扫码数据
     * @param barcode
     */
    protected void disposeScanData(final String barcode) {
        if (mOnScanListener != null) {
            MainThreadExecutor.runMainThread(new Runnable() {
                @Override
                public void run() {
                    mOnScanListener.onScanSuccess(barcode);
                }
            });
        }
    }


    /**
     * 设置扫码监听
     *
     * @param onScanListener
     * @return
     */
    public void setOnScanListener(OnScanListener onScanListener) {
        mOnScanListener = onScanListener;
    }

    @Override
    public void closeScan() {
        removeScanListener();
    }

    /**
     * 移除扫码监听
     */
    public void removeScanListener() {
        if (mOnScanListener != null) {
            mOnScanListener = null;
        }
    }
}

package com.sjl.scanner;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.sjl.scanner.listener.UsbPermissionListener;
import com.sjl.scanner.listener.UsbPlugListener;
import com.sjl.scanner.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename UsbScanHelper
 * @time 2022/6/9 11:37
 * @copyright(C) 2022 song
 */
public class UsbScanHelper {

    static volatile UsbScanHelper usbScanHelper;
    private static UsbPermission usbPermission = UsbPermission.Unknown;
    private Context mContext;

    public enum UsbPermission {Unknown, Requested, Granted, Denied}

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private UsbPermissionListener usbPermissionListener;
    private UsbPlugListener usbPlugListener;

    public static UsbScanHelper getInstance() {
        if (usbScanHelper == null) {
            synchronized (UsbScanHelper.class) {
                if (usbScanHelper == null) {
                    usbScanHelper = new UsbScanHelper();
                }
            }
        }
        return usbScanHelper;
    }

    /**
     * 初始化上下文
     * @param context
     */
    public void init(Context context) {
        mContext = context;
    }


    /**
     * 申请usb设备权限
     *
     * @param usbDevice
     */
    public void requestPermission(UsbDevice usbDevice) {
        requestPermission(usbDevice, null);
    }

    /**
     * 申请usb设备权限
     *
     * @param usbDevice
     * @param usbPermissionListener
     */
    public void requestPermission(UsbDevice usbDevice, UsbPermissionListener usbPermissionListener) {
        this.usbPermissionListener = usbPermissionListener;
        checkContext(mContext);
        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        usbManager.requestPermission(usbDevice, usbPermissionIntent);
    }

    private void checkContext(Context context) {
        if (context == null){
            throw new RuntimeException("context未初始化");
        }
    }

    /**
     * 是否有usb设备权限
     *
     * @param usbDevice
     * @return
     */
    public boolean hasPermission(UsbDevice usbDevice) {
        checkContext(mContext);
        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        return usbManager.hasPermission(usbDevice);
    }

    /**
     * 返回usb设备列表
     *
     * @return
     */
    public List<UsbDevice> getDeviceList() {
        checkContext(mContext);
        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        for (UsbDevice usbDevice : deviceList.values()) {
            LogUtils.i("id:" + usbDevice.getDeviceId() + ",mName:" + usbDevice.getDeviceName() + "，vendorID:" + usbDevice.getVendorId() + ",ProductId:" + usbDevice.getProductId());
        }
        return new ArrayList<>(deviceList.values());
    }


    /**
     * 注册usb广播
     */
    public void registerUsbReceiver() {
        checkContext(mContext);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        mContext.registerReceiver(mBroadcastReceiver, filter);
    }

    /**
     * 反注册usb广播
     */
    public void unregisterUsbReceiver() {
        if (mBroadcastReceiver != null) {
            mContext.unregisterReceiver(mBroadcastReceiver);
        }
        usbPermissionListener = null;
        usbPlugListener = null;
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    usbPermission = UsbPermission.Granted;
                    if (usbPermissionListener != null) {
                        usbPermissionListener.onGranted();
                    }
                } else {
                    LogUtils.w("usb 授权拒绝： " + device.getDeviceName());
                    usbPermission = UsbPermission.Denied;
                    if (usbPermissionListener != null) {
                        usbPermissionListener.onDenied();
                    }
                }

            } else if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                LogUtils.w("USB插入:" + device.toString());
                if (usbPlugListener != null) {
                    usbPlugListener.onAttached(device);
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                LogUtils.w("USB拔出:" + device.toString());
                if (usbPlugListener != null) {
                    usbPlugListener.onDetached(device);
                }
            }
        }
    };

    /**
     * usb插拔监听
     *
     * @param usbPlugListener
     */
    public void setUsbPlugListener(UsbPlugListener usbPlugListener) {
        this.usbPlugListener = usbPlugListener;
    }
}

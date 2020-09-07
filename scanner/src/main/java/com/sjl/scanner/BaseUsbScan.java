package com.sjl.scanner;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BaseUsbScan
 * @time 2020/7/3 18:40
 * @copyright(C) 2020 song
 */
public abstract class BaseUsbScan extends AbstractScan implements IUsbScan {
    protected final UsbManager mUsbManager;
    protected BroadcastReceiver broadcastReceiver;

    protected static final int MAX_RECONNECT_COUNT = 5;
    protected int reconnectCount = 0;
    protected int vendorId, productId;
    protected boolean connected = false;

    protected boolean readFlag = false;

    /**
     * 意图
     */
    private PendingIntent intent;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";


    public BaseUsbScan(Context context) {
        this.mContext = context;
        //1.创建usbManager
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
    }


    public static class UsbPermissionReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int status;
            if (ACTION_USB_PERMISSION.equals(action)) {
                status = UsbErrorCode.USB_OK;
                LogUtils.w("usb授权成功");
            } else {
                status = UsbErrorCode.USB_PERMISSION_FAIL;
                LogUtils.e("usb授权失败");
            }
        }
    }


    /**
     * 查找UsbDevice
     *
     * @param vendorId
     * @param productId
     * @return
     */
    protected UsbDevice findUsbDevice(int vendorId, int productId) {
        this.vendorId = vendorId;
        this.productId = productId;
        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        //2.获取到所有设备 选择出满足的设备
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        UsbDevice usbDevice = null;
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            LogUtils.i("id:" + device.getDeviceId() + ",mName:" + device.getDeviceName() + "，vendorID:" + device.getVendorId() + ",ProductId:" + device.getProductId());

            //dev/bus/usb/002/015，vendorID:1550,ProductId:2400 usb键盘自感模式
            //dev/bus/usb/002/022，vendorID:6790,ProductId:29987
            //每个 usb 设备通过 vendor-id（厂商 id） 和 product-id （产品 id)

            if (device.getVendorId() == vendorId && device.getProductId() == productId) {
                usbDevice = device; // 获取USBDevice
                break;
            }
        }
        return usbDevice;
    }

    /**
     * 注册申请usb权限广播
     * @param usbDevice
     */
    protected void registerPermissionReceiver(UsbDevice usbDevice) {
        broadcastReceiver = new UsbPermissionReceiver();
        intent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        mContext.registerReceiver(broadcastReceiver, new IntentFilter(ACTION_USB_PERMISSION));
        mUsbManager.requestPermission(usbDevice, intent);
    }


    /**
     * 查找本机所有的USB设备
     *
     * @param context
     */
    public static List<UsbDevice> getUsbDevices(Context context) {
        //1)创建usbManager
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        //2)获取到所有设备 选择出满足的设备
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        //创建返回数据
        List<UsbDevice> lists = new ArrayList<>();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            LogUtils.i("id:" + device.getDeviceId() + ",mName:" + device.getDeviceName() + "，vendorID:" + device.getVendorId() + ",ProductId:" + device.getProductId());
            lists.add(device);
        }
        return lists;
    }
}

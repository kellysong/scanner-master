package com.sjl.scanner;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.os.SystemClock;


import com.sjl.scanner.util.LogUtils;

import java.util.Arrays;

/**
 * usb连接扫码封装,自己控制命令
 *
 * @author Kelly
 * @version 1.0.0
 * @filename UsbCmdScan
 * @time 2020/7/1 11:01
 * @copyright(C) 2020 song
 */
public class UsbCmdScan extends BaseUsbScan {

    /**
     * 块输出端点
     */
    private UsbEndpoint epBulkOut;
    private UsbEndpoint epBulkIn;
    /**
     * 控制端点
     */
    private UsbEndpoint epControl;
    /**
     * 中断端点
     */
    private UsbEndpoint epIntEndpointOut;
    private UsbEndpoint epIntEndpointIn;

    private UsbDeviceConnection conn = null;





    public UsbCmdScan(Context context) {
        super(context);
    }


    @Override
    public int openScan(UsbConfig usbConfig) {
        if (connected) {
            LogUtils.i("已经打开");
            return 0;
        }
        UsbDevice usbDevice = findUsbDevice(usbConfig);
        if (usbDevice == null) {
            return UsbErrorCode.USB_FIND_THIS_FAIL;
        }
        //3.获取usb接口
        UsbInterface usbInterface = null;
        for (int i = 0; i < usbDevice.getInterfaceCount(); i++) {
            //一个设备上面一般只有一个接口，有两个端点，分别接受和发送数据
            usbInterface = usbDevice.getInterface(i);
            break;
        }

        //4.获取usb设备的通信通道endpoint
        int endpointCount = usbInterface.getEndpointCount();
        LogUtils.i("usb设备的通信通道数:" + endpointCount);
        for (int i = 0; i < endpointCount; i++) {
            UsbEndpoint ep = usbInterface.getEndpoint(i);
            switch (ep.getType()) {
                case UsbConstants.USB_ENDPOINT_XFER_BULK://USB端口传输
                    if (UsbConstants.USB_DIR_OUT == ep.getDirection()) {//输出
                        epBulkOut = ep;
                    } else {
                        epBulkIn = ep;
                    }
                    break;
                case UsbConstants.USB_ENDPOINT_XFER_CONTROL://控制
                    epControl = ep;
                    break;
                case UsbConstants.USB_ENDPOINT_XFER_INT://中断
                    if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {//输出
                        epIntEndpointOut = ep;
                    }
                    if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
                        epIntEndpointIn = ep;
                    }
                    break;
                case UsbConstants.USB_ENDPOINT_XFER_ISOC://中断
                    break;
                default:
                    break;
            }
        }
        boolean b = checkUsbEndpoint();
        if (!b) {
            return UsbErrorCode.USB_CHANNEL_FAIL;
        }


        //5.打开conn连接通道
        if (UsbScanHelper.getInstance().hasPermission(usbDevice)) {
            //有权限，那么打开
            conn = mUsbManager.openDevice(usbDevice);
        } else {
            //一般需要外部申请授权
            UsbScanHelper.getInstance().requestPermission(usbDevice);
            if (!UsbScanHelper.getInstance().hasPermission(usbDevice)){
                LogUtils.e("申请usb权限失败");
                return UsbErrorCode.USB_PERMISSION_FAIL;
            }else {
                conn = mUsbManager.openDevice(usbDevice);
            }
        }
        if (conn == null) {
            LogUtils.w("不能连接到设备");
            return UsbErrorCode.USB_OPEN_FAIL;
        }
        if (conn.claimInterface(usbInterface, true)) {
            LogUtils.w("usb扫码连接成功");
            connected = true;
            return UsbErrorCode.USB_OK;
        } else {
            LogUtils.w("无法打开连接通道。");
            conn.close();
        }
        return UsbErrorCode.USB_UNKNOWN_FAIL;
    }



    private boolean checkUsbEndpoint() {
        boolean ret = false;
        if (epBulkOut != null) {
            ret = true;
        }
        if (epBulkIn != null) {
            ret = true;
        }
        if (epControl != null) {
            ret = true;
        }
        if (epIntEndpointOut != null) {
            ret = true;
        }
        if (epIntEndpointIn != null) {
            ret = true;
        }
        return ret;
    }

    @Override
    public int sendData(byte[] buffer) {
        if (conn == null || epBulkOut == null) return UsbErrorCode.USB_UNKNOWN_FAIL;
        if (conn.bulkTransfer(epBulkOut, buffer, buffer.length, usbConfig.getWriteTimeout()) >= 0) {
            //0 或者正数表示成功
            return UsbErrorCode.USB_OK;
        } else {
            return UsbErrorCode.USB_SEND_DATA_FAIL;
        }
    }



    @Override
    public void startReading() {
        if (!connected) {
            LogUtils.e("not connected");
            return;
        }
        readFlag = true;
       executorService.execute(new Runnable() {
            @Override
            public void run() {
                //防止读取多次
                try {
                    byte[] buffer = new byte[512];
                    Arrays.fill(buffer, 0, buffer.length, (byte) 0);
                    UsbConfig.ScanCmd scanCmd = usbConfig.getScanCmd();
                    while (readFlag) {
                        synchronized (this) {
                            int i = sendData(scanCmd.getScanOpen());//写
                            if (i == UsbErrorCode.USB_SEND_DATA_FAIL) {
                                continue;
                            }
                            if (conn == null) {
                                break;
                            }
                            int ret = conn.bulkTransfer(epBulkIn, buffer, buffer.length, usbConfig.getReadTimeout());//获取数据流
                            if (ret > 0) {
                                sendData(scanCmd.getScanClose());
                                try {
                                    byte[] bs = new byte[ret];
                                    System.arraycopy(buffer, 0, bs, 0, ret);
                                    String barcode = new String(bs).trim();
                                    disposeScanData(barcode);
                                } catch (Exception e) {
                                    LogUtils.e("扫码拷贝异常", e);
                                }
                                SystemClock.sleep(2000);
                                resetBuffer(buffer, ret);
                            } else {
                                SystemClock.sleep(200);
                            }
                            sendData(scanCmd.getScanClose());
                        }
                    }
                } catch (Exception e) {
                    LogUtils.e("扫码异常", e);
                    if (conn != null) {
                        reconnect();
                    }

                }
                LogUtils.i("退出扫码读取线程");
            }

            /**
             * 折中处理缓存数据，无法解决扫码连续两次声音
             * @param buffer
             * @param toIndex
             */
            private void resetBuffer(byte[] buffer, int toIndex) {
                Arrays.fill(buffer, 0, toIndex, (byte) 0);
                int i = conn.bulkTransfer(epBulkIn, buffer, buffer.length, usbConfig.getReadTimeout());
                if (i > 0) {
                    LogUtils.w("=========释放重复读数据,length:" + i);
                    Arrays.fill(buffer, 0, i, (byte) 0);
                }
            }
        });
    }



    @Override
    public void stopReading() {
        readFlag = false;
        if (usbConfig != null){
            UsbConfig.ScanCmd scanCmd = usbConfig.getScanCmd();
            sendData(scanCmd.getScanClose());
        }

    }

    @Override
    public void reconnect() {
        if (reconnectCount > MAX_RECONNECT_COUNT) {
            return;
        }
        reconnectCount++;
        LogUtils.i("开始第" + reconnectCount + "重连");
        closeScan();
        int i = openScan(usbConfig);
        if (i == UsbErrorCode.USB_OK) {
            LogUtils.i("=====重连成功=====");
            startReading();
            reconnectCount = 0;
        } else {
            reconnect();
        }
    }

    @Override
    public void closeScan() {
        super.closeScan();
        connected = false;
        stopReading();
        if (mContext != null && broadcastReceiver != null) {
            mContext.unregisterReceiver(broadcastReceiver);
        }
        if (conn != null) {
            conn.close();
            conn = null;
        }
    }

}

package com.sjl.scanner;


import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;

import com.hoho.android.usbserial.driver.CommonUsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.sjl.scanner.util.LogUtils;

/**
 * usb传虚拟串口通讯,自感模式
 *
 * @author Kelly
 * @version 1.0.0
 * @filename UsbComAutoScan
 * @time 2020/7/3 18:01
 * @copyright(C) 2020 song
 */
public class UsbComAutoScan extends BaseUsbScan {
    private SerialInputOutputManager usbIoManager;
    private CommonUsbSerialPort usbSerialPort;

    public UsbComAutoScan(Context context) {
        super(context);
    }

    @Override
    public int openScan(UsbConfig usbConfig) {
        if (connected) {
            LogUtils.i("扫码器已经打开");
            return 0;
        }
        UsbConfig.SerialPortConfig serialPortConfig = usbConfig.getSerialPortConfig();
        if (serialPortConfig == null){
            throw new NullPointerException("serialPortConfig is null.");
        }
        UsbDevice usbDevice = findUsbDevice(usbConfig);
        if (usbDevice == null) {
            return UsbErrorCode.USB_FIND_THIS_FAIL;
        }

        try {
            UsbSerialDriver usbSerialDriver = UsbSerialProber.getDefaultProber().probeDevice(usbDevice);
            if (usbSerialDriver == null) {
                LogUtils.e("connection failed: no driver for device");
                return UsbErrorCode.USB_FIND_THIS_FAIL;
            }
            //获取已插入的串口驱动
            UsbDeviceConnection connection;
            if (mUsbManager.hasPermission(usbDevice)) {
                //有权限，那么打开
                connection = mUsbManager.openDevice(usbSerialDriver.getDevice());
            } else {
                //一般需要外部申请授权
                UsbScanHelper.getInstance().requestPermission(usbDevice);
                if (!UsbScanHelper.getInstance().hasPermission(usbDevice)){
                    LogUtils.e("申请usb权限失败");
                    return UsbErrorCode.USB_PERMISSION_FAIL;
                }else {
                    connection = mUsbManager.openDevice(usbDevice);
                }
            }
            if (connection == null) {
                LogUtils.w("不能连接到设备");
                return UsbErrorCode.USB_OPEN_FAIL;
            }

            usbSerialPort = (CommonUsbSerialPort) usbSerialDriver.getPorts().get(0);
            usbSerialPort.open(connection);
            //此条码默认配置为：设置串口的波特率、数据位，停止位，校验位
            //115200 波特率，8 位数据位，无校验位，1 位停止位）
            usbSerialPort.setParameters(serialPortConfig.getBaudRate(), serialPortConfig.getDataBits(), serialPortConfig.getStopBits(),serialPortConfig.getParity());

            //添加监听
            usbIoManager = new SerialInputOutputManager(usbSerialPort, new SerialInputOutputManager.Listener() {

                /*
                 * Serial
                 */
                @Override
                public void onNewData(byte[] data) {
                    long nowTime = System.currentTimeMillis();
                    if (nowTime - mLastSendTime > TIME_INTERVAL) {
                        // do something
                        mLastSendTime = nowTime;
                    } else {
                        LogUtils.i("过滤重复数据");
                        return;
                    }

                    LogUtils.i("connected:" + connected + ",readFlag:" + readFlag);
                    if (!connected) {
                        LogUtils.e("not connected");
                        reconnect();
                        return;
                    } else {
                        if (!readFlag) {
                            LogUtils.w("Have stopped reading.");
                            return;
                        }
                        try {
                            String barcode = new String(data).trim();
                            disposeScanData(barcode);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onRunError(Exception e) {
                    if (!connected) {
                        LogUtils.e("not connected", e);
                        return;
                    }
                    if (!readFlag) {
                        LogUtils.w("Have stopped reading.");
                        return;
                    }
                    LogUtils.e("扫码运行错误", e);
                    reconnect();
                }
            });
            usbIoManager.setReadTimeout(usbConfig.getReadTimeout());
            usbIoManager.setWriteTimeout(usbConfig.getWriteTimeout());
            //在新的线程中监听串口的数据变化
            executorService.submit(usbIoManager);
            connected = true;
            return UsbErrorCode.USB_OK;
        } catch (Exception e) {
            LogUtils.e("connection failed: ", e);
            return UsbErrorCode.USB_UNKNOWN_FAIL;
        }
    }


    @Override
    public int sendData(byte[] buffer) {
        try {
            if (usbConfig != null){
                usbSerialPort.write(buffer, usbConfig.getWriteTimeout());
                return UsbErrorCode.USB_OK;
            }

        } catch (Exception e) {
            LogUtils.e("发送数据异常", e);
        }
        return UsbErrorCode.USB_SEND_DATA_FAIL;
    }

    private long mLastSendTime = 0;
    public static final long TIME_INTERVAL = 1200L;

    @Override
    public void startReading() {
        if (!connected) {
            LogUtils.e("not connected");
            return;
        }
        readFlag = true;
        //不能关闭读的通道，不然下次打开会有历史数据
    }


    @Override
    public void stopReading() {
        readFlag = false;
    }

    @Override
    public void reconnect() {
        if (reconnectCount > MAX_RECONNECT_COUNT) {
            return;
        }
        reconnectCount++;
        try {
            LogUtils.i("扫码重连开始第" + reconnectCount + "重连");
            closeScan();
            int i = openScan(usbConfig);
            if (i == UsbErrorCode.USB_OK) {
                LogUtils.i("=====扫码重连成功=====");
                startReading();
                reconnectCount = 0;
            } else {
                reconnect();
            }
        } catch (Exception e) {
            LogUtils.e("扫码重连异常",e);
        }
    }


    @Override
    public void closeScan() {
        super.closeScan();
        connected = false;
        try {
            if (usbIoManager != null) {
                usbIoManager.stop();
            }
            usbIoManager = null;
            if (usbSerialPort != null) {
                usbSerialPort.close();
            }
        } catch (Exception ignored) {

        }
        usbSerialPort = null;

    }

}

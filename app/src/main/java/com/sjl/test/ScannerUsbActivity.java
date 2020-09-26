package com.sjl.test;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.sjl.scanner.BaseUsbScan;
import com.sjl.scanner.UsbComAutoScan;
import com.sjl.scanner.UsbConfig;
import com.sjl.scanner.listener.OnScanListener;

import java.util.List;

/**
 * 基于usb扫码
 *
 * @author song
 */
public class ScannerUsbActivity extends AppCompatActivity {
    BaseUsbScan usbScan;
    EditText et_barcode;
    UsbConfig usbConfig = new UsbConfig();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_usb_activity);
        et_barcode = findViewById(R.id.et_barcode);
        usbConfig.setProductId(1233);
        usbConfig.setVendorId(1234);
//        usbScan = new UsbCmdScan(this); //通过usb连接扫码, 发送命令扫码
        usbScan = new UsbComAutoScan(this);//通过usb转串口，自感模式,推荐
        usbScan.setOnScanListener(new OnScanListener() {
            @Override
            public void onScanSuccess(String barcode) {

            }
        });
    }


    public void listUsbDevice(View view) {
        //找到设备插入的usb孔对应UsbDevice
        List<UsbDevice> usbDevices = usbScan.getUsbDevices(this);
        for (UsbDevice device : usbDevices) {
            et_barcode.setText(et_barcode.getText() + "\n" + device.getDeviceName() + "，vendorID:" + device.getVendorId() + ",ProductId:" + device.getProductId());
        }
    }

    public void openUsbScan(View view) {
        int ret = usbScan.openScan(usbConfig);
        if (ret != 0) {
            showMsg("打开usb扫码失败,ret" + ret);
        } else {
            showMsg("打开usb扫码成功");
        }
    }

    private void showMsg(String s) {
        et_barcode.setText(s);
    }

    public void closeUsbScan(View view) {
        usbScan.closeScan();
    }

    public void loopScan(View view) {
        usbScan.startReading();
    }

    public void stopLoopScan(View view) {
        usbScan.stopReading();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        usbScan.closeScan();
    }
}

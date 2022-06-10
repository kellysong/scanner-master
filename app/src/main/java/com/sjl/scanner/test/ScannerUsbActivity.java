package com.sjl.scanner.test;

import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sjl.scanner.BaseUsbScan;
import com.sjl.scanner.UsbCmdScan;
import com.sjl.scanner.UsbComAutoScan;
import com.sjl.scanner.UsbConfig;
import com.sjl.scanner.UsbScanHelper;
import com.sjl.scanner.listener.OnScanListener;
import com.sjl.scanner.listener.UsbPermissionListener;
import com.sjl.scanner.listener.UsbPlugListener;
import com.sjl.scanner.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 基于usb扫码
 *
 * @author song
 */
public class ScannerUsbActivity extends AppCompatActivity {
    BaseUsbScan usbScan;
    EditText et_barcode;
    TextView tv_msg;
    Button btn_connect_setting;
    LinearLayout ll_operation;
    public static UsbConfig usbConfig = new UsbConfig();
    private Spinner spinner_connect_way, spinner_device;
    private int connectWay;
    private String deviceName;
    private static final String DEVICE_SEPARATOR = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_usb_activity);
        et_barcode = findViewById(R.id.et_barcode);
        tv_msg = findViewById(R.id.tv_msg);
        spinner_connect_way = findViewById(R.id.spinner_connect_way);
        spinner_device = findViewById(R.id.spinner_device);
        btn_connect_setting = findViewById(R.id.btn_connect_setting);
        ll_operation= findViewById(R.id.ll_operation);
        btn_connect_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScannerUsbActivity.this, SettingActivity.class));
            }
        });
        initSpinner();
        UsbScanHelper.getInstance().registerUsbReceiver();
        UsbScanHelper.getInstance().setUsbPlugListener(new UsbPlugListener() {
            @Override
            public void onAttached(UsbDevice usbDevice) {
                initDeviceList();
            }

            @Override
            public void onDetached(UsbDevice usbDevice) {
                initDeviceList();
            }
        });
    }

    private void initSpinner() {
        final List<String> connectWayStr = Arrays.asList("UsbCmdScan", "UsbComAutoScan");
        spinner_connect_way.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, connectWayStr));
        spinner_connect_way.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                LogUtils.i("connectWayStr:" + connectWayStr.get(position));
                connectWay = position;
                if (usbScan != null) {
                    usbScan.closeScan();
                }
                if (connectWay == 0) {
                    usbScan = new UsbCmdScan(ScannerUsbActivity.this); //通过usb连接扫码, 发送命令扫码
                } else {
                    usbScan = new UsbComAutoScan(ScannerUsbActivity.this);//通过usb转串口，自感模式,推荐
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        initDeviceList();


    }

    private void initDeviceList() {
        final List<String> deviceList = new ArrayList<>();

        List<UsbDevice> tempDevice = UsbScanHelper.getInstance().getDeviceList();
        for (UsbDevice device : tempDevice) {
            deviceList.add(device.getDeviceName() + DEVICE_SEPARATOR + "vid:" + device.getVendorId() + ",pid:" + device.getProductId());
        }

        if (deviceList.size() == 0) {
            ll_operation.setVisibility(View.GONE);
            showMsg("没有可用设备");
            return;
        }else {
            ll_operation.setVisibility(View.VISIBLE);
        }

        spinner_device.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList));
        spinner_device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                LogUtils.i("deviceName:" + deviceList.get(position));
                deviceName = deviceList.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    public void openUsbScan(View view) {
        UsbScanHelper usbScanHelper = UsbScanHelper.getInstance();
        List<UsbDevice> deviceList = usbScanHelper.getDeviceList();
        if (deviceList.size() == 0) {
            Toast.makeText(ScannerUsbActivity.this, "没有可用设备", Toast.LENGTH_LONG).show();
            return;
        }
        for (final UsbDevice device : deviceList) {
            String[] s = deviceName.split(DEVICE_SEPARATOR);
            if (device.getDeviceName().equals(s[0])) {
                // Request permission
                if (usbScanHelper.hasPermission(device)) {
                    performOpenUsbScan(device);
                } else {
                    usbScanHelper.requestPermission(device, new UsbPermissionListener() {
                        @Override
                        public void onGranted() {
                            performOpenUsbScan(device);
                        }

                        @Override
                        public void onDenied() {
                            showMsg("无usb权限");
                        }
                    });
                }
                break;
            }
        }


    }

    private void performOpenUsbScan(UsbDevice device) {
        if (usbScan == null) {
            return;
        }
        usbConfig.setProductId(device.getProductId());
        usbConfig.setVendorId(device.getVendorId());
        usbScan.setOnScanListener(new OnScanListener() {
            @Override
            public void onScanSuccess(String barcode) {
                et_barcode.setText(barcode);
            }
        });
        int ret = usbScan.openScan(usbConfig);
        if (ret != 0) {
            showMsg("打开usb扫码失败,ret" + ret);
        } else {
            showMsg("打开usb扫码成功");
        }
    }


    public void closeUsbScan(View view) {
        usbScan.closeScan();
        showMsg("关闭usb扫码成功");
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
        UsbScanHelper.getInstance().unregisterUsbReceiver();
    }


    private void showMsg(String s) {

        final String str = s;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Date curDate = new Date(System.currentTimeMillis());
                String strDate = new SimpleDateFormat("HH:mm:ss.SSS").format(curDate);
                //已有的log
                String log = tv_msg.getText().toString().trim();
                if (TextUtils.isEmpty(log)) {
                    log = strDate + ":  " + str;
                } else {
                    log += "\r\n" + strDate + ":  " + str;
                }
                tv_msg.setText(log);
                //自动滚动
                tv_msg.post(() -> {
                    int scrollAmount = tv_msg.getLayout().getLineTop(tv_msg.getLineCount()) - tv_msg.getHeight();
                    tv_msg.scrollTo(0, scrollAmount > 0 ? scrollAmount : 0);
                });
            }
        });

    }
}

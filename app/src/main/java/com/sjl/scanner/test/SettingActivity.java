package com.sjl.scanner.test;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sjl.scanner.UsbConfig;
import com.sjl.scanner.util.ByteUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * 扫码设置，保存到内存中
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SettingActivity
 * @time 2022/6/9 18:00
 * @copyright(C) 2022 song
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    TextView tvBaudRate;
    TextView tvDataBits;
    TextView tvStopBits;
    TextView tvParity;
    EditText et_open_cmd;
    EditText et_close_cmd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        RelativeLayout itemBaudRate = findViewById(R.id.itemBaudRate);
        RelativeLayout itemDataBits = findViewById(R.id.itemDataBits);
        RelativeLayout itemStopBits = findViewById(R.id.itemStopBits);
        RelativeLayout itemParity = findViewById(R.id.itemParity);
        tvBaudRate = findViewById(R.id.tvBaudRate);
        tvDataBits = findViewById(R.id.tvDataBits);
        tvStopBits = findViewById(R.id.tvStopBits);
        tvParity = findViewById(R.id.tvParity);
        itemBaudRate.setOnClickListener(this);
        itemDataBits.setOnClickListener(this);
        itemStopBits.setOnClickListener(this);
        itemParity.setOnClickListener(this);


        et_open_cmd = findViewById(R.id.et_open_cmd);
        et_close_cmd = findViewById(R.id.et_close_cmd);
        et_open_cmd.addTextChangedListener(this);
        et_close_cmd.addTextChangedListener(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initDefaultValue();
    }

    private void initDefaultValue() {
        final UsbConfig usbConfig = ScannerUsbActivity.usbConfig;
        final UsbConfig.SerialPortConfig serialPortConfig = usbConfig.getSerialPortConfig();
        final UsbConfig.ScanCmd scanCmd = usbConfig.getScanCmd();
        tvBaudRate.setText(String.valueOf(serialPortConfig.getBaudRate()));
        tvDataBits.setText(String.valueOf(serialPortConfig.getDataBits()));
        if (serialPortConfig.getStopBits() == 3) {
            tvStopBits.setText("1.5");
        } else {
            tvStopBits.setText(String.valueOf(serialPortConfig.getStopBits()));
        }
        int parity = serialPortConfig.getParity();
        final String[] values = getResources().getStringArray(R.array.parity);
        tvParity.setText(values[parity]);
        String openCmd = ByteUtils.byteArrToHexString(scanCmd.getScanOpen());
        et_open_cmd.setText(openCmd);
        et_open_cmd.setSelection(openCmd.length());
        String closeCmd = ByteUtils.byteArrToHexString(scanCmd.getScanClose());
        et_close_cmd.setText(closeCmd);
        et_close_cmd.setSelection(closeCmd.length());

    }

    private int baudRate = 115200;
    private int dataBits = 8;
    private int stopBits = 1;
    private int parity = 0;

    @Override
    public void onClick(View v) {
        final UsbConfig usbConfig = ScannerUsbActivity.usbConfig;
        final UsbConfig.SerialPortConfig serialPortConfig = usbConfig.getSerialPortConfig();

        switch (v.getId()) {
            case R.id.itemBaudRate: {
                final String[] values = getResources().getStringArray(R.array.baudRates);
                int pos = java.util.Arrays.asList(values).indexOf(String.valueOf(baudRate));
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("波特率");
                builder.setSingleChoiceItems(values, pos, (dialog, which) -> {
                    dialog.dismiss();
                    baudRate = Integer.parseInt(values[which]);
                    tvBaudRate.setText(values[which]);
                    serialPortConfig.setBaudRate(baudRate);
                });
                builder.create().show();
                break;
            }
            case R.id.itemDataBits: {
                final String[] values = getResources().getStringArray(R.array.dataBits);
                int pos = java.util.Arrays.asList(values).indexOf(String.valueOf(dataBits));
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("数据位");
                builder.setSingleChoiceItems(values, pos, (dialog, which) -> {
                    dialog.dismiss();
                    dataBits = Integer.parseInt(values[which]);
                    tvDataBits.setText(values[which]);
                    serialPortConfig.setDataBits(dataBits);
                });
                builder.create().show();
                break;
            }
            case R.id.itemStopBits: {
                final String[] values = getResources().getStringArray(R.array.stopBits);
                int pos = 0;
                if (stopBits == 1) {
                    pos = 0;
                } else if (stopBits == 3) {
                    pos = 1;
                } else if (stopBits == 2) {
                    pos = 2;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("停止位");
                builder.setSingleChoiceItems(values, pos, (dialog, which) -> {
                    dialog.dismiss();
                    if (values[which].equals("1.5")) {
                        stopBits = 3;
                    } else {
                        stopBits = Integer.parseInt(values[which]);
                    }
                    tvStopBits.setText(values[which]);

                    serialPortConfig.setStopBits(stopBits);
                });
                builder.create().show();
                break;
            }
            case R.id.itemParity: {
                final String[] values = getResources().getStringArray(R.array.parity);

                int pos = parity;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("校验位");
                builder.setSingleChoiceItems(values, pos, (dialog, which) -> {
                    dialog.dismiss();
                    parity = which;
                    tvParity.setText(values[which]);
                    serialPortConfig.setParity(which);
                });
                builder.create().show();
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        final UsbConfig usbConfig = ScannerUsbActivity.usbConfig;
        String openCmd = et_open_cmd.getText().toString().trim().replace(" ", "");
        String closeCmd = et_close_cmd.getText().toString().trim().replace(" ", "");
        if (!TextUtils.isEmpty(openCmd) && openCmd.length() % 2 == 0) {
            UsbConfig.ScanCmd scanCmd = usbConfig.getScanCmd();
            scanCmd.setScanOpen(ByteUtils.hexStringToByteArr(openCmd));
        }
        if (!TextUtils.isEmpty(closeCmd) && closeCmd.length() % 2 == 0) {
            UsbConfig.ScanCmd scanCmd = usbConfig.getScanCmd();
            scanCmd.setScanClose(ByteUtils.hexStringToByteArr(closeCmd));
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}

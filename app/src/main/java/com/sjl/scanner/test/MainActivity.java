package com.sjl.scanner.test;

import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.sjl.scanner.UsbScanHelper;
import com.sjl.scanner.listener.UsbPlugListener;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MainActivity
 * @time 2020/9/26 13:30
 * @copyright(C) 2020 song
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

    }


    public void testUsbKeyboard(View view) {
        startActivity(new Intent(this,ScannerUsbKeyboardActivity.class));
    }


    public void testUsbConnect(View view) {
        startActivity(new Intent(this,ScannerUsbActivity.class));
    }


}


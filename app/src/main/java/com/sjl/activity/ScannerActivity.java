package com.sjl.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.sjl.scanner.OnScanListener;
import com.sjl.scanner.ScanMode;
import com.sjl.scanner.Scanner;
import com.sjl.scanner.test.R;


public class ScannerActivity extends AppCompatActivity {
    Scanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_activity);
        scanner = new Scanner(this,ScanMode.USB_KEYBOARD).scanListener(new OnScanListener() {
            @Override
            public void onScanSuccess(String barcode) {

            }

            @Override
            public void onScanFail(String barcode, Exception e) {

            }
        }).start();
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        scanner.dispatchKeyEvent(event);
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanner.cancel();
    }
}

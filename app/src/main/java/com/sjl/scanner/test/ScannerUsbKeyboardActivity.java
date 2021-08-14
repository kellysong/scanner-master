package com.sjl.scanner.test;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.sjl.scanner.UsbKeyboardAutoScan;
import com.sjl.scanner.listener.OnScanListener;
import com.sjl.scanner.test.fragment.MyDialogFragment;

/**
 * usb键盘模式扫码示例，无需EditText接收,推荐
 *
 * @author song
 */
public class ScannerUsbKeyboardActivity extends AppCompatActivity {
    UsbKeyboardAutoScan usbKeyboardAutoScan;
    EditText et_barcode;
    MyDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_usb_keyboard_activity);
        et_barcode = findViewById(R.id.et_barcode);
        usbKeyboardAutoScan = new UsbKeyboardAutoScan();
        initScanListener();

    }

    private void initScanListener() {
        usbKeyboardAutoScan.setOnScanListener(new OnScanListener() {
            @Override
            public void onScanSuccess(String barcode) {
                et_barcode.setText(barcode);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (usbKeyboardAutoScan.isIntercept()
                && event.getKeyCode() != KeyEvent.KEYCODE_BACK) {//不处理返回键
            usbKeyboardAutoScan.analysisKeyEvent(event);
            return true;//防止输入框接收事件
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        usbKeyboardAutoScan.cancel();
    }

    public void btnDialogFragment(View view) {
        dialogFragment = new MyDialogFragment();
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        dialogFragment.show(supportFragmentManager, dialogFragment.getClass().getSimpleName());
        dialogFragment.setMyDialogFragmentListener(new MyDialogFragment.MyDialogFragmentListener() {
            @Override
            public void onDismiss() {
                initScanListener();
            }
        });
    }

    public UsbKeyboardAutoScan getUsbKeyboardAutoScan() {
        return usbKeyboardAutoScan;
    }
}

package com.sjl.scanner;

import android.view.KeyEvent;

import com.sjl.scanner.listener.OnScanListener;
import com.sjl.scanner.util.LogUtils;

/**
 * usb键盘，自动感应扫码封装
 *
 * @author song
 */
public class UsbKeyboardAutoScan {
    protected OnScanListener mOnScanListener;

    //延迟500ms，判断扫码是否完成。
    private final static long MESSAGE_DELAY = 500;
    private boolean intercept;
    //扫码内容
    private StringBuffer mStringBufferResult = new StringBuffer();
    private final Runnable mScanningFishedRunnable = new Runnable() {
        @Override
        public void run() {
            performScanSuccess();
        }
    };

    //返回扫描结果
    private void performScanSuccess() {
        String barcode = mStringBufferResult.toString().trim();
        disposeScanData(barcode);
        mStringBufferResult.setLength(0);
    }

    /**
     * 处理扫码数据并验证,运行在主线程
     *
     * @param barcode
     */
    protected void disposeScanData(String barcode) {
        LogUtils.i("扫码结果：" + barcode);
        if (mOnScanListener != null) {
            mOnScanListener.onScanSuccess(barcode);
        }
    }


    /**
     * key事件处理
     *
     * @param event
     */
    public void analysisKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            char pressedKey = (char) event.getUnicodeChar();
            if (pressedKey != 0) {
                mStringBufferResult.append(pressedKey);
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                //若为回车键，直接返回
                MainThreadExecutor.getHandler().removeCallbacks(mScanningFishedRunnable);
                MainThreadExecutor.getHandler().post(mScanningFishedRunnable);
            } else {
                //延迟post，若500ms内，有其他事件
                MainThreadExecutor.getHandler().removeCallbacks(mScanningFishedRunnable);
                MainThreadExecutor.getHandler().postDelayed(mScanningFishedRunnable, MESSAGE_DELAY);
            }

        }

    }


    /**
     * 设置扫码监听
     *
     * @param onScanListener
     * @return
     */
    public void setOnScanListener(OnScanListener onScanListener) {
        intercept = true;
        mOnScanListener = onScanListener;
    }

    /**
     * 移除扫码监听
     */
    public void removeScanListener() {
        intercept = false;
        if (mOnScanListener != null) {
            mOnScanListener = null;
        }
    }

    public boolean isIntercept() {
        return intercept;
    }

    /**
     * 取消扫码回调
     */
    public void cancel() {
        removeScanListener();
        MainThreadExecutor.getHandler().removeCallbacks(mScanningFishedRunnable);
    }


}

package com.sjl.scanner;

import android.view.KeyEvent;

/**
 * 自动感应扫码封装
 */
public class AutoScan extends AbstractScan{

    //延迟500ms，判断扫码是否完成。
    private final static long MESSAGE_DELAY = 500;
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
     * key事件处理
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
                mHandler.removeCallbacks(mScanningFishedRunnable);
                mHandler.post(mScanningFishedRunnable);
            } else {
                //延迟post，若500ms内，有其他事件
                mHandler.removeCallbacks(mScanningFishedRunnable);
                mHandler.postDelayed(mScanningFishedRunnable, MESSAGE_DELAY);
            }

        }

    }


    @Override
    public void cancel() {
        super.cancel();
        mHandler.removeCallbacks(mScanningFishedRunnable);
    }
}

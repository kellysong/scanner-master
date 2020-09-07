package com.sjl.scanner;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AbstractScan
 * @time 2020/7/1 14:23
 * @copyright(C) 2020 song
 */
public abstract class AbstractScan {
    protected OnScanListener mOnScanListener;
    protected Handler mHandler = new Handler(Looper.getMainLooper());
    protected  static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    protected Context mContext;
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
     * 设置扫码监听
     *
     * @param onScanListener
     * @return
     */
    public AbstractScan setOnScanListener(OnScanListener onScanListener) {
        mOnScanListener = onScanListener;
        return this;
    }

    /**
     * 移除扫码监听
     */
    public void removeScanListener() {
        if (mOnScanListener != null) {
            mOnScanListener = null;
        }
    }

    /**
     * 取消扫码
     */
    public void cancel() {
        mOnScanListener = null;
    }
}

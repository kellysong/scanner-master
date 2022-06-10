package com.sjl.scanner.test;

import android.app.Application;

import com.sjl.scanner.UsbScanHelper;
import com.sjl.scanner.util.LogUtils;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyApplication
 * @time 2022/6/2 15:53
 * @copyright(C) 2022 song
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.init(true);
        UsbScanHelper.getInstance().init(this);
    }


}

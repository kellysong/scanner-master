package com.sjl.scanner;

import android.util.Log;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename LogUtils
 * @time 2020/8/29 18:56
 * @copyright(C) 2020 song
 */
public class LogUtils {
    private LogUtils() {

    }

    private static final String TAG = "SIMPLE_LOGGER";

    public static void i(String str) {
        Log.i(TAG, str);
    }

    public static void w(String str) {
        Log.w(TAG, str);
    }

    public static void e(String str) {
        Log.e(TAG, str);
    }

    public static void e(String str, Exception e) {
        Log.e(TAG, str, e);
    }


}

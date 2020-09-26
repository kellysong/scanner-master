package com.sjl.scanner;

import android.os.Handler;
import android.os.Looper;

/**
 * MainThreadExecutor
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MainThreadExecutor.java
 * @time 2019/5/22 10:10
 * @copyright(C) 2019 song
 */
public final class MainThreadExecutor {
    private MainThreadExecutor() {
    }

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    public static void runMainThread(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            HANDLER.post(runnable);
        }
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static Handler getHandler() {
        return HANDLER;
    }
}

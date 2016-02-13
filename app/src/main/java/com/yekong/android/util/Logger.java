package com.yekong.android.util;

import android.util.Log;

/**
 * Created by baoxiehao on 16/2/4.
 */
public class Logger {
    private static final String TAG = "[bao] ";

    public static void d(String tag, String msg) {
        Log.d(TAG + tag, msg);
    }

    public static void i(String tag, String msg) {
        Log.i(TAG + tag, msg);
    }

    public static void w(String tag, String msg) {
        Log.w(TAG + tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.e(TAG + tag, msg);
    }

    public static void e(String tag, String msg, Throwable t) {
        Log.e(TAG + tag, msg, t);
    }
}

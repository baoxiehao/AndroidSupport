package com.yekong.android.ui;

import android.app.Application;

import com.yekong.android.util.Logger;

/**
 * Created by baoxiehao on 16/2/5.
 */
public class RssApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d("app", "oncreate");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}

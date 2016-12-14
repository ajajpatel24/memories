package com.azzie.memories;

/**
 * Created by BrillBrains-4 on 03-10-2016.
 */

import android.app.Application;

/**
 * Created by Ravi on 13/08/15.
 */
public class MyApplication extends Application {
    public static final String TAG = MyApplication.class
            .getSimpleName();

    private static MyApplication mInstance;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
    }
}
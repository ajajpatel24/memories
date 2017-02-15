package com.messagitory;

/**
 * Created by BrillBrains-4 on 03-10-2016.
 */

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by Ravi on 13/08/15.
 */
public class MyApplication extends Application {
    public static final String TAG = MyApplication.class
            .getSimpleName();
    public static SharedPreferences preferences;
    private static MyApplication mInstance;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        preferences = getSharedPreferences("MessagiTory", MODE_PRIVATE);
        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
    }
}
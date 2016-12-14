package com.azzie.memories;

import android.app.Application;

/**
 * Created by BrillBrains-4 on 16-08-2016.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        if (Build.VERSION.SDK_INT >= 23) {
//
//            // Show alert dialog to the user saying a separate permission is needed
//            // Launch the settings activity if the user prefers
//            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
//            startActivity(myIntent);
//        } else {
//            startService(new Intent(this, ChatHeadService.class));
//        }
    }
}

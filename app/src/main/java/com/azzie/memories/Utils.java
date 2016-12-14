package com.azzie.memories;


import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.view.Display;

public class Utils {
    public static String LogTag = "henrytest";
    public static String EXTRA_MSG = "extra_msg";

    public static int DeviceWidth = 0;


    public static int getScreenWidth(Activity activity) {

        Display display = activity.getWindowManager().getDefaultDisplay();

        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size);

            return size.x;
        } else {
            return display.getWidth();
        }
    }

    public static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else {
            return Settings.canDrawOverlays(context);
        }


    }


}

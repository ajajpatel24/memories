package com.messagitory;


import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.Display;

public class Utils {
    public static String LogTag = "messagitory";
    public static String EXTRA_MSG = "extra_msg";
    public static String URL;
    public static int DeviceWidth = 0;
    public static String dbName = "MessagiToryDB";
    public static int dbVersion = 1;
    public static String BaseURL = "http://www.messagitory.com/admin/index.php/";

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

    public static String getDeviceID(Activity activity) {
        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }
}

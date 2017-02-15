package com.messagitory;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by admin on 12/23/2016.
 */

public class ServiceModel {
    public static String dbName = "MessagiToryDB";
    public static int dbVersion = 1;
    private static ServiceModel mInstance = new ServiceModel();

    public static ServiceModel getInstance() {
        if (mInstance == null) {
            mInstance = new ServiceModel();
        }
        return mInstance;
    }

    public void insert(Context context) {
        String onoroff = "ON";
        DatabaseHelperClass helper = new DatabaseHelperClass(
                context, Utils.dbName, null,
                Utils.dbVersion);
        SQLiteDatabase db1 = helper.getWritableDatabase();
        db1.execSQL("INSERT INTO service (onoroff) VALUES ('" + onoroff + "');");
        db1.close();
        helper.close();
    }

    public void update(Context context, String onoroff) {
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        db1.execSQL("UPDATE service SET onoroff = '" + onoroff + "';");
        db1.close();
        helper.close();
    }

    public String get(Context context) {
        String onoroff = "";
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM service;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                do {
                    onoroff = cr1.getString(0);
                } while (cr1.moveToNext());
            }
            cr1.close();
            db1.close();
            helper.close();
        } else {
            cr1.close();
            db1.close();
            helper.close();
        }
        return onoroff;
    }
}

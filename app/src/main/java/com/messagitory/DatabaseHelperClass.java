package com.messagitory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper class
 */
public class DatabaseHelperClass extends SQLiteOpenHelper {
    public DatabaseHelperClass(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db1) {
        db1.execSQL("CREATE TABLE categories (categoryid INTEGER PRIMARY KEY AUTOINCREMENT,category text);");
        db1.execSQL("CREATE TABLE messages (messageid INTEGER PRIMARY KEY AUTOINCREMENT,message text);");
        db1.execSQL("CREATE TABLE service (onoroff text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db1, int arg1, int arg2) {
        onCreate(db1);
    }
}

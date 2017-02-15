package com.messagitory;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Ajaj on 11/22/2016.
 */

public class CategoryModel {
    public static String dbName = "MessagiToryDB";
    public static int dbVersion = 1;
    private static CategoryModel mInstance = new CategoryModel();
    public ArrayList<Category> categoryArrayList;

    public static CategoryModel getInstance() {
        if (mInstance == null) {
            mInstance = new CategoryModel();
        }
        return mInstance;
    }

    public void insert(Category category, Context context) {
        Gson gson = new Gson();
        DatabaseHelperClass helper = new DatabaseHelperClass(
                context, Utils.dbName, null,
                Utils.dbVersion);
        SQLiteDatabase db1 = helper.getWritableDatabase();
        db1.execSQL("INSERT INTO categories (category) VALUES ('" + gson.toJson(category) + "');");
        db1.close();
        helper.close();
    }

    public void deleteAll(Context context) {
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        db1.execSQL("DELETE FROM categories;");
        db1.close();
        helper.close();
    }

    public void update(Context context, String name, boolean value) {
        int id = -1;
        Category categoryToUpdate = new Category();
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM categories;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                do {
                    Type type = new TypeToken<Category>() {
                    }.getType();
                    Gson gson = new Gson();
                    Category category = gson.fromJson(cr1.getString(1), type);
                    if (category.getName().equals(name)) {
                        id = cr1.getInt(0);
                        categoryToUpdate = category;
                        categoryToUpdate.setFavorite(value);
                        break;
                    }
                } while (cr1.moveToNext());
            }
            if (id != -1) {
                db1.execSQL("UPDATE categories SET category = '" + new Gson().toJson(categoryToUpdate) + "' WHERE categoryid='" + id + "';");
            } else {
                Toast.makeText(context, "Cannot update category.", Toast.LENGTH_LONG).show();
            }
            cr1.close();
            db1.close();
            helper.close();
        } else {
            cr1.close();
            db1.close();
            helper.close();
        }
    }

    public ArrayList<Category> getCategories(Context context) {
        categoryArrayList = new ArrayList<>();
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM categories;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                do {
                    Type type = new TypeToken<Category>() {
                    }.getType();
                    Gson gson = new Gson();
                    Category category = gson.fromJson(cr1.getString(1), type);
                    categoryArrayList.add(category);
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
        return categoryArrayList;
    }

    public boolean checkCategory(Context context, String categoryToInsert) {
        boolean value = false;
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM categories;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                do {
                    Type type = new TypeToken<Category>() {
                    }.getType();
                    Gson gson = new Gson();
                    Category category = gson.fromJson(cr1.getString(1), type);
                    if (category.getName().equals(categoryToInsert)) {
                        value = true;
                        break;
                    }
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
        return value;
    }

    public void delete(Context context, String categoryToDelete) {
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM categories;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                do {
                    Type type = new TypeToken<Category>() {
                    }.getType();
                    Gson gson = new Gson();
                    Category category = gson.fromJson(cr1.getString(1), type);
                    if (category.getName().equals(categoryToDelete)) {
                        db1.execSQL("DELETE FROM categories WHERE categoryid='" + cr1.getInt(0) + "';");
                        break;
                    }
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
    }

    public void updateName(Context context, String oldvalue, String newvalue) {
        int id = -1;
        Category categoryToUpdate = new Category();
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM categories;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                do {
                    Type type = new TypeToken<Category>() {
                    }.getType();
                    Gson gson = new Gson();
                    Category category = gson.fromJson(cr1.getString(1), type);
                    if (category.getName().equals(oldvalue)) {
                        id = cr1.getInt(0);
                        categoryToUpdate = category;
                        categoryToUpdate.setName(newvalue);
                        break;
                    }
                } while (cr1.moveToNext());
            }
            if (id != -1) {
                db1.execSQL("UPDATE categories SET category = '" + new Gson().toJson(categoryToUpdate) + "' WHERE categoryid='" + id + "';");
            } else {
                Toast.makeText(context, "Cannot update category.", Toast.LENGTH_LONG).show();
            }
            MessagesModel.getInstance().updateMessagesCategoryWise(context,oldvalue,newvalue);
            cr1.close();
            db1.close();
            helper.close();
        } else {
            cr1.close();
            db1.close();
            helper.close();
        }
    }
}

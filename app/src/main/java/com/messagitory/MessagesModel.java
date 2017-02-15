package com.messagitory;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ajaj on 11/22/2016.
 */

public class MessagesModel {
    public static String dbName = "MessagiToryDB";
    public static int dbVersion = 1;
    private static MessagesModel mInstance = new MessagesModel();
    public ArrayList<Message> messageArrayList;

    public static MessagesModel getInstance() {
        if (mInstance == null) {
            mInstance = new MessagesModel();
        }
        return mInstance;
    }

    public void insert(Message Message, Context context) {
        Gson gson = new Gson();
        DatabaseHelperClass helper = new DatabaseHelperClass(
                context, Utils.dbName, null,
                Utils.dbVersion);
        SQLiteDatabase db1 = helper.getWritableDatabase();
        db1.execSQL("INSERT INTO messages (message) VALUES ('" + gson.toJson(Message) + "');");
        db1.close();
        helper.close();
    }

    public void delete(Context context, String messageToDelete) {
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM messages;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                do {
                    Type type = new TypeToken<Message>() {
                    }.getType();
                    Gson gson = new Gson();
                    Message message1 = gson.fromJson(cr1.getString(1), type);
                    if (message1.getMessage().equals(messageToDelete)) {
                        db1.execSQL("DELETE FROM messages WHERE messageid='" + cr1.getInt(0) + "';");
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

    public void update(Context context, String messageFrom, boolean value) {
        int id = -1;
        Message messageToUpdate = new Message();
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM messages;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                do {
                    Type type = new TypeToken<Message>() {
                    }.getType();
                    Gson gson = new Gson();
                    Message message = gson.fromJson(cr1.getString(1), type);
                    if (message.getMessage().equals(messageFrom)) {
                        id = cr1.getInt(0);
                        messageToUpdate = message;
                        messageToUpdate.setFavorite(value);
                        break;
                    }
                } while (cr1.moveToNext());
            }
            if (id != -1) {
                db1.execSQL("UPDATE messages SET message = '" + new Gson().toJson(messageToUpdate) + "' WHERE messageid='" + id + "';");
            } else {
                Toast.makeText(context, "Cannot update message.", Toast.LENGTH_LONG).show();
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

    public ArrayList<Message> getMessages(Context context) {
        messageArrayList = new ArrayList<>();
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM messages;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                do {
                    Type type = new TypeToken<Message>() {
                    }.getType();
                    Gson gson = new Gson();
                    Message Message = gson.fromJson(cr1.getString(1), type);
                    messageArrayList.add(Message);
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
        return messageArrayList;
    }

    public ArrayList<Message> getMessagesByCategory(Context context, String categoryname) {
        messageArrayList = new ArrayList<>();
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM messages;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                do {
                    Type type = new TypeToken<Message>() {
                    }.getType();
                    Gson gson = new Gson();
                    Message message = gson.fromJson(cr1.getString(1), type);
                    if (message.getCategoryname().equals(categoryname))
                        messageArrayList.add(message);
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
        return messageArrayList;
    }

    public ArrayList<Message> getFavoriteMessages(Context context) {
        messageArrayList = new ArrayList<>();
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM messages;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                do {
                    Type type = new TypeToken<Message>() {
                    }.getType();
                    Gson gson = new Gson();
                    Message message = gson.fromJson(cr1.getString(1), type);
                    if (message.isFavorite())
                        messageArrayList.add(message);
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
        return messageArrayList;
    }

    public boolean checkMessage(Context context, String message) {
        boolean value = false;
        messageArrayList = new ArrayList<>();
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM messages;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                do {
                    Type type = new TypeToken<Message>() {
                    }.getType();
                    Gson gson = new Gson();
                    Message message1 = gson.fromJson(cr1.getString(1), type);
                    if (message1.getMessage().equals(message)) {
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

    public ArrayList<Message> filterByDate(Context context, String startDate, String endDate) {
        messageArrayList = new ArrayList<>();
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM messages;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                do {
                    Type type = new TypeToken<Message>() {
                    }.getType();
                    Gson gson = new Gson();
                    Message message = gson.fromJson(cr1.getString(1), type);
                    Log.e("message", "" + message.getDate());
                    if ((!TextUtils.isEmpty(startDate) && Long.parseLong(message.getDate()) >= Long.parseLong(startDate)) && (!TextUtils.isEmpty(endDate) && Long.parseLong(message.getDate()) <= Long.parseLong(endDate)))
                        messageArrayList.add(message);
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
        return messageArrayList;
    }

    public ArrayList<Message> filterFavoriteByDate(Context context, String startDate, String endDate) {
        messageArrayList = new ArrayList<>();
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM messages;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                do {
                    Type type = new TypeToken<Message>() {
                    }.getType();
                    Gson gson = new Gson();
                    Message message = gson.fromJson(cr1.getString(1), type);
                    Log.e("message", "" + message.getDate());
                    if ((!TextUtils.isEmpty(startDate) && Long.parseLong(message.getDate()) >= Long.parseLong(startDate)) && (!TextUtils.isEmpty(endDate) && Long.parseLong(message.getDate()) <= Long.parseLong(endDate))) {
                        if (message.isFavorite())
                            messageArrayList.add(message);
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
        return messageArrayList;
    }

    public HashMap<String, Integer> countMessagesByCategory(Context context) {
        HashMap<String, Integer> messageCount = new HashMap<>();
        ArrayList<Category> categoryArrayList = CategoryModel.getInstance().getCategories(context);
        int count = 0;
        messageArrayList = new ArrayList<>();
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM messages;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                for (int i = 0; i < categoryArrayList.size(); i++) {
                    cr1.moveToFirst();
                    count = 0;
                    do {
                        Type type = new TypeToken<Message>() {
                        }.getType();
                        Gson gson = new Gson();
                        Message message = gson.fromJson(cr1.getString(1), type);
                        if (message.getCategoryname().equals(categoryArrayList.get(i).getName())) {
                            count++;
                        }
                    } while (cr1.moveToNext());
                    messageCount.put(categoryArrayList.get(i).getName(), count);
                }
            } else {
                for (int i = 0; i < categoryArrayList.size(); i++) {
                    messageCount.put(categoryArrayList.get(i).getName(), 0);
                }
            }
            cr1.close();
            db1.close();
            helper.close();
        } else {
            for (int i = 0; i < categoryArrayList.size(); i++) {
                messageCount.put(categoryArrayList.get(i).getName(), 0);
            }
            cr1.close();
            db1.close();
            helper.close();
        }
        return messageCount;
    }

    public void updateMessage(Context context, String oldvalue, String newvalue) {
        int id = -1;
        Message messageToUpdate = new Message();
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM messages;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                do {
                    Type type = new TypeToken<Message>() {
                    }.getType();
                    Gson gson = new Gson();
                    Message message = gson.fromJson(cr1.getString(1), type);
                    if (message.getMessage().equals(oldvalue)) {
                        id = cr1.getInt(0);
                        messageToUpdate = message;
                        messageToUpdate.setMessage(newvalue);
                        break;
                    }
                } while (cr1.moveToNext());
            }
            if (id != -1) {
                db1.execSQL("UPDATE messages SET message = '" + new Gson().toJson(messageToUpdate) + "' WHERE messageid='" + id + "';");
            } else {
                Toast.makeText(context, "Cannot update message.", Toast.LENGTH_LONG).show();
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

    public void messageBackup(Context context) {
        String backupString = "";
        HashMap<String, Integer> messageCounts = countMessagesByCategory(context);
        ArrayList<Category> categoryArrayList = CategoryModel.getInstance().getCategories(context);
        ArrayList<Message> messageArrayList = MessagesModel.getInstance().getMessages(context);
        for (int i = 0; i < messageCounts.size(); i++) {
            if (messageCounts.get(categoryArrayList.get(i).getName()) > 0) {
                backupString = backupString + "\n\n\n--------------------------------------------------------------------------------------";
                backupString = backupString + "\nCATEGORY : " + categoryArrayList.get(i).getName().toUpperCase();
                backupString = backupString + "\n--------------------------------------------------------------------------------------";
                for (int j = 0; j < messageArrayList.size(); j++) {
                    if (messageArrayList.get(j).getCategoryname().equals(categoryArrayList.get(i).getName())) {
                        backupString = backupString + "\n\n" + messageArrayList.get(j).getMessage();
                        backupString = backupString + "\n--------------------------------------------------------------------------------------";
                    }
                }
            }
        }
        try {
            if (!backupString.equals("")) {
                File root = new File(Environment.getExternalStorageDirectory(), "MessagiTory/DatabaseBackup");
                if (!root.exists()) {
                    root.mkdirs();
                }
                File gpxfile = new File(root, "Backup.txt");
                FileWriter writer = new FileWriter(gpxfile);
                writer.append(backupString);
                writer.flush();
                writer.close();
//                Toast.makeText(context,"Your messages saved successfully in MessagiTory Folder..",Toast.LENGTH_LONG).show();
            } else {
                ;
//                Toast.makeText(context,"There is no content to save.",Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Some error occurred, Try again later.", Toast.LENGTH_LONG).show();
        }
    }
    public void updateMessagesCategoryWise(Context context,String oldCategoryName,String newCategoryName){
        int id=-1;
        messageArrayList = new ArrayList<>();
        DatabaseHelperClass helper = new DatabaseHelperClass(context, Utils.dbName,
                null, Utils.dbVersion);
        SQLiteDatabase db1 = helper.getReadableDatabase();
        Cursor cr1 = db1.rawQuery("SELECT * FROM messages;", null);
        if (cr1 != null && cr1.moveToFirst()) {
            if (cr1.moveToFirst()) {
                do {
                    Type type = new TypeToken<Message>() {
                    }.getType();
                    Gson gson = new Gson();
                    id = cr1.getInt(0);
                    Message message = gson.fromJson(cr1.getString(1), type);
                    if (message.getCategoryname().equals(oldCategoryName)){
                        message.setCategoryname(newCategoryName);
                        db1.execSQL("UPDATE messages SET message = '" + new Gson().toJson(message) + "' WHERE messageid='" + id + "';");
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
}

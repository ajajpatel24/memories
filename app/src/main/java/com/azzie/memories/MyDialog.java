package com.azzie.memories;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;


public class MyDialog extends Activity {
    public static boolean active = false;
    public static Activity myDialog;
    ListView category;
    ArrayList<String> categories;
    ArrayList<Category> categoryArrayList;
    TextView title;
    ImageButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);
        title = (TextView) findViewById(R.id.title);
        add = (ImageButton) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
        title.setText("Select Category");
        myDialog = MyDialog.this;
        categoryArrayList = new ArrayList<>();
        categoryArrayList = CategoryModel.getInstance().getCategories(this);
        Collections.sort(categoryArrayList, new Comparator<Category>() {
            @Override
            public int compare(Category abc1, Category abc2) {
                int b1 = abc1.isCustom() ? 1 : 0;
                int b2 = abc2.isCustom() ? 1 : 0;
                return b2 - b1;
            }
        });
        category = (ListView) findViewById(R.id.category);
        categories = new ArrayList<>();
        for (int i = 0; i < categoryArrayList.size(); i++) {
            categories.add(categoryArrayList.get(i).getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.layout_dialog_item, categories);
        category.setAdapter(adapter);
        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard.getPrimaryClip() != null) {
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    if (!MessagesModel.getInstance().checkMessage(MyDialog.this, item.getText().toString())) {
                        Message message = new Message();
                        message.setMessage(item.getText().toString());
                        message.setCategoryname(categories.get(position));
                        message.setCategoryid(null);
                        message.setFavorite(false);
                        message.setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                        MessagesModel.getInstance().insert(message, MyDialog.this);
                    } else {
                        Toast.makeText(MyDialog.this, "Message already exists.", Toast.LENGTH_LONG).show();
                    }
                }
                myDialog.finish();
            }
        });
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        active = true;
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        active = false;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        active = false;
    }

    public void showDialog() {
        final Dialog add;
        final EditText name;
        final TextView title;
        final Button save, cancel;
        add = new Dialog(new ContextThemeWrapper(this, R.style.Theme_MyApp));
        add.requestWindowFeature(Window.FEATURE_NO_TITLE);
        add.setContentView(R.layout.layout_add_category_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(add.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        add.getWindow().setAttributes(lp);
        name = (EditText) add.findViewById(R.id.name);
        save = (Button) add.findViewById(R.id.add);
        cancel = (Button) add.findViewById(R.id.cancel);
        title = (TextView) add.findViewById(R.id.title);
        title.setText("Add Category");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("")) {
                    Toast.makeText(MyDialog.this, "Name should be there.", Toast.LENGTH_LONG).show();
                } else {
                    if (CategoryModel.getInstance().checkCategory(MyDialog.this, name.getText().toString())) {
                        Toast.makeText(MyDialog.this, "Category already exists.", Toast.LENGTH_LONG).show();
                    } else {
                        Category category = new Category();
                        category.setCustom(true);
                        category.setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                        category.setFavorite(false);
                        category.setImage(0);
                        category.setName(name.getText().toString());
                        CategoryModel.getInstance().insert(category, MyDialog.this);
                        categoryArrayList = new ArrayList<>();
                        categoryArrayList = CategoryModel.getInstance().getCategories(MyDialog.this);
                        Collections.sort(categoryArrayList, new Comparator<Category>() {
                            @Override
                            public int compare(Category abc1, Category abc2) {
                                int b1 = abc1.isCustom() ? 1 : 0;
                                int b2 = abc2.isCustom() ? 1 : 0;
                                return b2 - b1;
                            }
                        });
                        categories = new ArrayList<>();
                        for (int i = 0; i < categoryArrayList.size(); i++) {
                            categories.add(categoryArrayList.get(i).getName());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyDialog.this, R.layout.layout_dialog_item, categories);
                        MyDialog.this.category.setAdapter(adapter);
                        MyDialog.this.category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                if (clipboard.getPrimaryClip() != null) {
                                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                                    if (!MessagesModel.getInstance().checkMessage(MyDialog.this, item.getText().toString())) {
                                        Message message = new Message();
                                        message.setMessage(item.getText().toString());
                                        message.setCategoryname(categories.get(position));
                                        message.setCategoryid(null);
                                        message.setFavorite(false);
                                        message.setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                                        MessagesModel.getInstance().insert(message, MyDialog.this);
                                    } else {
                                        Toast.makeText(MyDialog.this, "Message already exists.", Toast.LENGTH_LONG).show();
                                    }
                                    Tracker t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                                    t.send(new HitBuilders.EventBuilder()
                                            .setCategory("Messages")
                                            .setAction("Paste contents")
                                            .setLabel("Paste")
                                            .build());
                                }
                                myDialog.finish();
                            }
                        });
                    }
                    Tracker t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Categories")
                            .setAction("Add category from Bubble")
                            .setLabel("AddCategory")
                            .build());
                }
                add.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add.dismiss();
            }
        });
        add.show();
    }
}

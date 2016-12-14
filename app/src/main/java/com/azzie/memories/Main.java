package com.azzie.memories;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.wooplr.spotlight.SpotlightView;

import java.util.ArrayList;
import java.util.Calendar;

public class Main extends AppCompatActivity {
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1234;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 5678;
    public static Button btnStartService, btnShowMsg;
    public static String dbName = "mydb";
    public static int dbVersion = 1;
    TextView memories;
    CategoriesAdapter adapter;
    GridView gridView;
    ArrayList<Category> categoryArrayList;
    int images[] = {R.drawable.anniversary,
            R.drawable.bestwishes,
            R.drawable.birthday,
            R.drawable.boygirl,
            R.drawable.brokenheart,
            R.drawable.commercial,
            R.drawable.community,
            R.drawable.cricket,
            R.drawable.eid,
            R.drawable.exam,
            R.drawable.friendship,
            R.drawable.jokes,
            R.drawable.life,
            R.drawable.love,
            R.drawable.moral,
            R.drawable.movies,
            R.drawable.others,
            R.drawable.parentandchild,
            R.drawable.poetry,
            R.drawable.politics,
            R.drawable.quotes,
            R.drawable.marriage,
            R.drawable.teacherstudent,
            R.drawable.valentines};
    String categories[] = {"Anniversary SMS",
            "Best Wishes / Dua SMS",
            "Birthday SMS",
            "Boy & Girl SMS",
            "Broken Heart SMS",
            "Commercial SMS",
            "Community SMS",
            "Cricket SMS",
            "Eid SMS",
            "Exam SMS",
            "Friendship SMS",
            "Funny / Jokes SMS",
            "Life SMS",
            "Love SMS",
            "Moral SMS",
            "Movies SMS",
            "Other SMS",
            "Parent & Child SMS",
            "Poetry SMS",
            "Politics SMS",
            "Quotes SMS",
            "Marriage SMS",
            "Teacher & Student SMS",
            "Valentines Day SMS"};
    boolean handled = false;
    android.support.v4.app.FragmentManager fragmentManager;
    TextView title;
    SharedPreferences prefs;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    String categoryToSave;
    HomeFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        categoryArrayList = new ArrayList<>();
        prefs = getSharedPreferences("Memories", MODE_PRIVATE);
        boolean firstTime = prefs.getBoolean("firstTime", true);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_18dp);
        if (firstTime) {
            SharedPreferences.Editor editor = getSharedPreferences("Memories", MODE_PRIVATE).edit();
            editor.putBoolean("firstTime", false);
            editor.commit();
            for (int i = 0; i < categories.length; i++) {
                Category categoryToInsert = new Category();
                categoryToInsert.setCustom(false);
                categoryToInsert.setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                categoryToInsert.setFavorite(false);
                categoryToInsert.setImage(images[i]);
                categoryToInsert.setName(categories[i]);
                CategoryModel.getInstance().insert(categoryToInsert, this);
            }
        } else {
            categoryArrayList = CategoryModel.getInstance().getCategories(this);
            Log.d("categories", "" + new Gson().toJson(categoryArrayList));
        }

        fragmentManager = getSupportFragmentManager();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        fragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Tracker t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                if (menuItem.isChecked())
                    menuItem.setChecked(false);
                else
                    menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment;
                Intent i;
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        //menu.findItem(R.id.search).setVisible(false);
                        //menu.findItem(R.id.filter).setVisible(false);
                        //Toast.makeText(getApplicationContext(), "Home Selected", Toast.LENGTH_SHORT).show();
                        fragment = new HomeFragment();
                        fragmentTransaction.replace(R.id.frame, fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commitAllowingStateLoss();
                        return true;
                    case R.id.favorite:
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory("Messages")
                                .setAction("Favorite Messages")
                                .setLabel("Favorite")
                                .build());
                        i = new Intent(Main.this, MessagesActivity.class);
                        i.putExtra("categoryName", "Favorite Messages");
                        i.putExtra("favorite", true);
                        startActivity(i);
                        return true;
                    case R.id.info:
                        i = new Intent(Main.this, InstructionsActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.rate:
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory("Rating")
                                .setAction("Rate app")
                                .setLabel("RateApp")
                                .build());
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        Uri uri = Uri.parse("market://details?id=" + "com.jetpurdictionary");
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.jetpurdictionary")));
                        }
                        return true;
                    case R.id.share:
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory("ShareApp")
                                .setAction("Share app")
                                .setLabel("ShareApp")
                                .build());
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                        Intent sharingIntent = new Intent(
                                android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        String shareBody = "https://play.google.com/store/apps/details?id=com.jetpurdictionary";
                        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Very Usefull App , Download right now");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        return true;

                    case R.id.exit:
                        final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(Main.this, R.style.Theme_MyApp)).create();
                        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        alertDialog.setMessage("Are you sure ?");
                        alertDialog.setCancelable(false);
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                        alertDialog.show();
                        return true;
                    case R.id.setting:
//                        //Toast.makeText(getApplicationContext(), "Settings Selected", Toast.LENGTH_SHORT).show();
                        i = new Intent(Main.this, com.azzie.memories.Settings.class);
                        startActivity(i);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Went Wrong", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new SpotlightView.Builder(Main.this)
                        .introAnimationDuration(400)
                        .performClick(true)
                        .fadeinTextDuration(400)
                        .headingTvColor(Color.parseColor("#ffffff"))
                        .headingTvSize(32)
                        .headingTvText("Add Category")
                        .subHeadingTvColor(Color.parseColor("#ffffff"))
                        .subHeadingTvSize(16)
                        .subHeadingTvText("Click on button to add new category.")
                        .maskColor(Color.parseColor("#80000000"))
                        .target(findViewById(R.id.add))
                        .lineAnimDuration(400)
                        .lineAndArcColor(Color.parseColor("#2196F3"))
                        .dismissOnTouch(true)
                        .dismissOnBackPress(true)
                        .enableDismissAfterShown(true)
                        .usageId("Add Category") //UNIQUE ID
                        .show();
            }
        }, 1500);

    }

    void handleSendText(Intent intent) {
        Tracker t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        t.send(new HitBuilders.EventBuilder()
                .setCategory("Messages")
                .setAction("Direct insert messages.")
                .setLabel("ShareMessages")
                .build());
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            showDialogMessage(sharedText);
        }
    }

    public void showDialogMessage(final String messageToSave) {
        final Dialog add;
        final EditText messageEdit;
        final Spinner categorySpinner;
        final TextView title;
        final Button save, cancel;
        add = new Dialog(new ContextThemeWrapper(this, R.style.Theme_MyApp));
        add.requestWindowFeature(Window.FEATURE_NO_TITLE);
        add.setContentView(R.layout.message_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(add.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        add.getWindow().setAttributes(lp);
        categorySpinner = (Spinner) add.findViewById(R.id.category);
        messageEdit = (EditText) add.findViewById(R.id.message);
        save = (Button) add.findViewById(R.id.add);
        cancel = (Button) add.findViewById(R.id.cancel);
        title = (TextView) add.findViewById(R.id.title);
        title.setText("Add Message");
        messageEdit.setText(messageToSave);
        categorySpinner.setVisibility(View.VISIBLE);
        final ArrayList<Category> categoryArrayList = CategoryModel.getInstance().getCategories(this);
        ArrayList<String> list;
        list = new ArrayList<String>();
        list.add("Select Category");
        for (int i = 0; i < categoryArrayList.size(); i++) {
            list.add(categoryArrayList.get(i).getName());
        }
        CustomSpinnerAdapter spinAdapter1 = new CustomSpinnerAdapter(
                this, list, 1);
        categorySpinner.setAdapter(spinAdapter1);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                if (position == 0) {
                } else {
                    categoryToSave = categoryArrayList.get(position - 1).getName();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (messageEdit.getText().toString().equals("")) {
                    Toast.makeText(Main.this, "Message cannot be empty.", Toast.LENGTH_LONG).show();
                } else if (categorySpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(Main.this, "Select any category for message to save.", Toast.LENGTH_LONG).show();
                } else {
                    if (MessagesModel.getInstance().checkMessage(Main.this, messageEdit.getText().toString())) {
                        Toast.makeText(Main.this, "Message already exists.", Toast.LENGTH_LONG).show();
                    } else {
                        Message message = new Message();
                        message.setCustome(true);
                        message.setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                        message.setFavorite(false);
                        message.setMessage(messageEdit.getText().toString());
                        message.setCategoryname(categoryToSave);
                        message.setCategoryid(null);
                        MessagesModel.getInstance().insert(message, Main.this);
                        add.dismiss();
                    }
                }
                if(fragment instanceof HomeFragment){
                    fragment.init();
                }
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

    @Override
    public void onBackPressed() {
        final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(Main.this, R.style.Theme_MyApp)).create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setMessage("Are you sure ?");
        alertDialog.setCancelable(false);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterfpace, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        alertDialog.show();
    }


    private void startChatHead() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                requestPermission(OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG);
            } else {
                showChatHeadMsg();

            }
        } else {
            startService(new Intent(Main.this, ChatHeadService.class));
        }
    }

    private void showChatHeadMsg() {
        java.util.Date now = new java.util.Date();
        String str = "test by henry  " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);

        Intent it = new Intent(Main.this, ChatHeadService.class);
        it.putExtra(Utils.EXTRA_MSG, str);
        startService(it);
    }

    private void needPermissionDialog(final int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
        builder.setMessage("You need to allow permission");
        builder.setPositiveButton("OK",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        requestPermission(requestCode);
                    }
                });
        builder.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        boolean on = prefs.getBoolean("service", true);
        if (on) {
            startChatHead();
        }
        if(fragment instanceof HomeFragment){
            fragment.init();
        }
        if (!handled) {
            handled = true;
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    handleSendText(intent); // Handle text being sent
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.add:
                showDialog(true, "", "");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDialog(final boolean action, final String oldvalue, final String newvalue) {
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
        if (action)
            title.setText("Add Category");
        else
            title.setText("Update Category");
        if (action){
            name.setText("");
        }else{
            name.setText(oldvalue);
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("")) {
                    Toast.makeText(Main.this, "Name should be there.", Toast.LENGTH_LONG).show();
                } else {
                    if (CategoryModel.getInstance().checkCategory(Main.this, name.getText().toString())) {
                        Toast.makeText(Main.this, "Category already exists.", Toast.LENGTH_LONG).show();
                    } else {
                        Category category = new Category();
                        category.setCustom(true);
                        category.setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                        category.setFavorite(false);
                        category.setImage(0);
                        category.setName(name.getText().toString());
                        if (action) {
                            CategoryModel.getInstance().insert(category, Main.this);
                            Toast.makeText(Main.this, "Category added successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            CategoryModel.getInstance().updateName(Main.this, oldvalue, name.getText().toString());
                            Toast.makeText(Main.this, "Category updated successfully.", Toast.LENGTH_LONG).show();
                        }

                    }
                    if(fragment instanceof HomeFragment){
                        fragment.init();
                    }
                }
                Tracker t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("Categories")
                        .setAction("Add new category")
                        .setLabel("AddCategory")
                        .build());
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void requestPermission(int requestCode) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION_REQ_CODE_CHATHEAD) {
            if (!Utils.canDrawOverlays(Main.this)) {
                needPermissionDialog(requestCode);
            } else {
                startChatHead();
            }
        }
    }

}

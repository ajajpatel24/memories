package com.messagitory;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Main extends AppCompatActivity {
    Call<String> addMessageCall;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1234;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 5678;
    TextView messagitory;
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
    boolean goPublics = false;
    android.support.v4.app.FragmentManager fragmentManager;
    SharedPreferences prefs;
    String categoryToSave, categoryName = "";
    HomeFragment fragment;
    private String TAG = "Main Activity";
    private Uri imageUri;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private LinearLayout goPublic;
    private TextView noofmessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        goPublic = (LinearLayout) findViewById(R.id.publics);
        noofmessages = (TextView) findViewById(R.id.noofmessages);
        goPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Main.this, MainActivity.class);
                startActivity(i);
            }
        });
        categoryArrayList = new ArrayList<>();
        prefs = MyApplication.preferences;
        boolean firstTime = prefs.getBoolean("firstTime", true);
        Log.e("FirstTime", "first" + firstTime);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_18dp);
        if (firstTime) {
            SharedPreferences.Editor editor = MyApplication.preferences.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();
            ServiceModel.getInstance().insert(this);
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
        //isReadStoragePermissionGranted();
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
                        i = new Intent(Main.this, FavoriteMessages.class);
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
                        Uri uri = Uri.parse("market://details?id=" + "com.messagitory");
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        try {
                            startActivity(goToMarket);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=com.messagitory")));
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
                        String shareBody = "Save time and don't push your mind too much to remember the messages you like. Download and Share https://play.google.com/store/apps/details?id=com.messagitory";
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
                        i = new Intent(Main.this, com.messagitory.Settings.class);
                        startActivity(i);
                        return true;
                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Went Wrong", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });
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
        list.add("Go Public");
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
                if (position > 1) {
                    categoryToSave = categoryArrayList.get(position - 2).getName();
                } else if (position == 1) {
                    goPublics = true;
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
                    if (goPublics) {
                        if (messageEdit.getText().toString().length() > 240) {
                            Toast.makeText(Main.this, "Message should be less than 240 characters to share in public.", Toast.LENGTH_LONG).show();
                        } else {
                            goPublics = false;
                            addMessage(messageEdit.getText().toString());
                            add.dismiss();
                        }
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
                            Toast.makeText(Main.this, "Message added successfully.", Toast.LENGTH_LONG).show();
                            add.dismiss();
                        }
                    }
                }
                if (fragment instanceof HomeFragment) {
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
        if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
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
        } else {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        }
    }


    private void startChatHead() {
        Log.e("startChatHead", "startChatHead");
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
        Intent it = new Intent(Main.this, ChatHeadService.class);
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
        noofMessages();
        Log.e("OnResume", "OnResume");
        navigationView.getMenu().findItem(R.id.home).setChecked(true);
        isReadStoragePermissionGranted();
        if (fragment instanceof HomeFragment) {
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

    public void showChooseImageDialog() {
        final Dialog dialog;
        ArrayList<String> listOfMenuOptions = new ArrayList<>();
        dialog = new Dialog(Main.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        final ListView menuoptions = (ListView) dialog.findViewById(R.id.sortmenu);
        Toolbar toolbar = (Toolbar) dialog.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        listOfMenuOptions.add("Gallery");
        listOfMenuOptions.add("Camera");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Main.this, R.layout.menu_item, listOfMenuOptions);
        menuoptions.setAdapter(adapter);
        menuoptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFromList = (String) (menuoptions.getItemAtPosition(position));
                if (selectedFromList.equals("Gallery")) {
                    gallery();
                } else if (selectedFromList.equals("Camera")) {
                    takePhoto();
                } else {
                    Toast.makeText(getApplicationContext(), "Other Selected", Toast.LENGTH_LONG).show();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void showDialog(final boolean action, final String oldvalue, final String newvalue) {
        final Dialog add;
        final EditText name;
        final TextView title;
        final Button save, cancel, upload, capture;
        final ImageButton chooseImage;
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
        upload = (Button) add.findViewById(R.id.upload);
        chooseImage = (ImageButton) add.findViewById(R.id.chooseImage);
        capture = (Button) add.findViewById(R.id.capture);
        save = (Button) add.findViewById(R.id.add);
        cancel = (Button) add.findViewById(R.id.cancel);
        title = (TextView) add.findViewById(R.id.title);
        if (action)
            title.setText("Add Category");
        else
            title.setText("Update Category");
        if (action) {
            name.setText("");
        } else {
            name.setText(oldvalue);
        }
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("")) {
                    Toast.makeText(Main.this, "Name should be there.", Toast.LENGTH_LONG).show();
                } else {
                    categoryName = name.getText().toString();
                    showChooseImageDialog();
                }
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("")) {
                    Toast.makeText(Main.this, "Name should be there.", Toast.LENGTH_LONG).show();
                } else {
                    categoryName = name.getText().toString();
                    gallery();
                }
            }
        });
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("")) {
                    Toast.makeText(Main.this, "Name should be there.", Toast.LENGTH_LONG).show();
                } else {
                    categoryName = name.getText().toString();
                    takePhoto();
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("")) {
                    Toast.makeText(Main.this, "Name should be there.", Toast.LENGTH_LONG).show();
                } else {
                    if (action) {
                        if (CategoryModel.getInstance().checkCategory(Main.this, name.getText().toString())) {
                            Toast.makeText(Main.this, "Category already exists.", Toast.LENGTH_LONG).show();
                        } else {
                            Category category = new Category();
                            category.setCustom(true);
                            category.setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                            category.setFavorite(false);
                            category.setImage(0);
                            category.setName(name.getText().toString());
                            CategoryModel.getInstance().insert(category, Main.this);
                            uploadPic();
                            Toast.makeText(Main.this, "Category added successfully.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (oldvalue.equals(name.getText().toString())) {
                            uploadPic();
                            Toast.makeText(Main.this, "Category updated successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            CategoryModel.getInstance().updateName(Main.this, oldvalue, name.getText().toString());
                            uploadPic();
                            Toast.makeText(Main.this, "Category updated successfully.", Toast.LENGTH_LONG).show();
                        }
                    }
                    if (fragment instanceof HomeFragment) {
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

    public void gallery() {
        if (isReadStoragePermissionGranted()) {
            Utils.URL = "";
            Intent i = new Intent(this, GalleryActivity.class);
            startActivityForResult(i, 2);
        }
    }

    public void takePhoto() {
        if (isReadStoragePermissionGranted()) {
            Utils.URL = "";
            String fileName = UUID.randomUUID().toString() + ".jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
            imageUri = Main.this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, 1);
        }
    }

    public boolean isReadStoragePermissionGranted() {
        Log.e("permission", "permission");
        String[] perms = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_PHONE_STATE"};
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                if (ServiceModel.getInstance().get(this).equals("ON")) {
                    startChatHead();
                }
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, perms, 200);
                return false;
            }
        } else {
            if (ServiceModel.getInstance().get(this).equals("ON")) {
                startChatHead();
            }
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permission : " + permissions[i] + "was granted");
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri = null;
        String filePath = null;
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    selectedImageUri = imageUri;
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(Main.this, "Picture was not taken", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Main.this, "Picture was not taken", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1234:
                if (!Utils.canDrawOverlays(Main.this)) {
                    needPermissionDialog(requestCode);
                } else {
                    Log.e("1234", "1234");
                    startChatHead();
                }
                break;
            case 2:
                break;
        }
        if (selectedImageUri != null) {
            try {
                String filemanagerstring = selectedImageUri.getPath();
                String selectedImagePath = getPath(selectedImageUri);
                if (selectedImagePath != null) {
                    filePath = selectedImagePath;
                } else if (filemanagerstring != null) {
                    filePath = filemanagerstring;
                } else {
                    Toast.makeText(Main.this, "Unknown path",
                            Toast.LENGTH_LONG).show();
                    Log.e("Bitmap", "Unknown path");
                }
                Log.d("PATH", "" + filePath);
                if (filePath != null) {
                    if (filePath == null) {
                        Toast.makeText(Main.this, "Could not find the filepath of the selected file",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    Utils.URL = filePath;
                }
            } catch (Exception e) {
                Toast.makeText(Main.this, "Internal error",
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }
    }

    private void uploadPic() {
        try {
            if (!Utils.URL.equals("")) {
                Log.d("URL", "" + Utils.URL);
                File file = new File(Utils.URL);
                String extStorageDirectory = Environment.getExternalStorageDirectory() + File.separator;
                File folder = new File(extStorageDirectory, "MessagiTory");
                if (folder.exists()) {
                } else {
                    folder.mkdir();
                }
                File pdfFile = new File(folder, categoryName + ".jpg");
                copyDirectory(file, pdfFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void copyDirectory(File sourceLocation, File targetLocation)
            throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs()) {
                throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
            }
            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            File directory = targetLocation.getParentFile();
            if (directory != null && !directory.exists() && !directory.mkdirs()) {
                throw new IOException("Cannot create dir " + directory.getAbsolutePath());
            }
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void noofMessages() {
        Call<String> noofMessageCall;
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.BaseURL).addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient)
                .build();
        RestInterface restInterface = retrofit.create(RestInterface.class);
        noofMessageCall = restInterface.noofMessages("");
        noofMessageCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (!response.body().equals("")) {
                        noofmessages.setText("GO PUBLIC (" + response.body() + " Messages)");
                    } else {
                        noofmessages.setText("GO PUBLIC (" + 0 + " Messages)");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(Main.this, "No internet connection.", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    public void addMessage(String message) {
        final Dialog mDialog = new Dialog(Main.this);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_progressbar);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface mDialog) {
                addMessageCall.cancel();
            }
        });
        mDialog.show();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.BaseURL).addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient)
                .build();
        RestInterface restInterface = retrofit.create(RestInterface.class);
        addMessageCall = restInterface.addMessage(Utils.getDeviceID(Main.this), message);
        addMessageCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mDialog.dismiss();
                try {
                    if (!response.body().equals("")) {
                        Toast.makeText(Main.this, "Message posted successfully.", Toast.LENGTH_LONG).show();
                        goPublics = false;
                    } else {
                        Toast.makeText(Main.this, "Error posting message.", Toast.LENGTH_LONG).show();
                    }
                    noofMessages();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(Main.this, "No internet connection.", Toast.LENGTH_LONG).show();
                mDialog.dismiss();
                t.printStackTrace();
            }
        });
    }
}

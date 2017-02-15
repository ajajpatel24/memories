package com.messagitory;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MyDialog extends Activity {
    public static boolean active = false;
    public static Activity myDialog;
    ListView category;
    ArrayList<String> categories;
    ArrayList<Category> categoryArrayList;
    TextView title;
    ImageButton add, addMessage;
    String categoryName = "", categoryToSave;
    String TAG = "MyDialog";
    private Uri imageUri;
    boolean goPublic = false;
    Call<String> addMessageCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog);
        title = (TextView) findViewById(R.id.title);
        add = (ImageButton) findViewById(R.id.add);
        addMessage = (ImageButton) findViewById(R.id.addMessage);
        addMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogMessage();
            }
        });
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
        categories.add("Go Public");
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
                    if (position == 0) {
                        if (item.getText().toString().length() > 240) {
                            Toast.makeText(MyDialog.this, "Message should be less than 240 characters to share in public.", Toast.LENGTH_LONG).show();
                        } else {
                            goPublic = false;
                            addMessage(item.getText().toString());
                        }
                    } else {
                        if (!MessagesModel.getInstance().checkMessage(MyDialog.this, item.getText().toString())) {
                            Message message = new Message();
                            message.setMessage(item.getText().toString());
                            message.setCategoryname(categories.get(position));
                            message.setCategoryid(null);
                            message.setFavorite(false);
                            message.setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                            MessagesModel.getInstance().insert(message, MyDialog.this);
                            Toast.makeText(MyDialog.this, "Message saved successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MyDialog.this, "Message already exists.", Toast.LENGTH_LONG).show();
                        }
                        myDialog.finish();
                    }
                }

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

    public boolean isReadStoragePermissionGranted() {
        Log.e("permission", "permission");
        String[] perms = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, perms, 200);
                return false;
            }
        } else {
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

    public void showChooseImageDialog() {
        final Dialog dialog;
        ArrayList<String> listOfMenuOptions = new ArrayList<>();
        dialog = new Dialog(MyDialog.this);
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyDialog.this, R.layout.menu_item, listOfMenuOptions);
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

    public void showDialog() {
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
        chooseImage = (ImageButton) add.findViewById(R.id.chooseImage);
        upload = (Button) add.findViewById(R.id.upload);
        capture = (Button) add.findViewById(R.id.capture);
        save = (Button) add.findViewById(R.id.add);
        cancel = (Button) add.findViewById(R.id.cancel);
        title = (TextView) add.findViewById(R.id.title);
        title.setText("Add Category");
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().toString().equals("")) {
                    Toast.makeText(MyDialog.this, "Name should be there.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(MyDialog.this, "Name should be there.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(MyDialog.this, "Name should be there.", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(MyDialog.this, "Category added successfully.", Toast.LENGTH_LONG).show();
                        uploadPic();
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
                                        Toast.makeText(MyDialog.this, "Message saved successfully.", Toast.LENGTH_LONG).show();
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
            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, 1);
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
                    Toast.makeText(MyDialog.this, "Picture was not taken", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyDialog.this, "Picture was not taken", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(MyDialog.this, "Unknown path",
                            Toast.LENGTH_LONG).show();
                    Log.e("Bitmap", "Unknown path");
                }
                Log.d("PATH", "" + filePath);
                if (filePath != null) {
                    if (filePath == null) {
                        Toast.makeText(MyDialog.this, "Could not find the filepath of the selected file",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    Utils.URL = filePath;
                }
            } catch (Exception e) {
                Toast.makeText(MyDialog.this, "Internal error",
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

    public void showDialogMessage() {
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
                    categoryToSave = categoryArrayList.get(position - 1).getName();
                } else if (position == 1) {
                    goPublic = true;
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
                    Toast.makeText(MyDialog.this, "Message cannot be empty.", Toast.LENGTH_LONG).show();
                } else if (categorySpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(MyDialog.this, "Select any category for message to save.", Toast.LENGTH_LONG).show();
                } else {
                    if (goPublic) {
                        if (messageEdit.getText().toString().length() > 240) {
                            Toast.makeText(MyDialog.this, "Message should be less than 240 characters to share in public.", Toast.LENGTH_LONG).show();
                        } else {
                            goPublic = false;
                            addMessage(messageEdit.getText().toString());
                            add.dismiss();
                        }
                    } else {
                        if (MessagesModel.getInstance().checkMessage(MyDialog.this, messageEdit.getText().toString())) {
                            Toast.makeText(MyDialog.this, "Message already exists.", Toast.LENGTH_LONG).show();
                        } else {
                            Message message = new Message();
                            message.setCustome(true);
                            message.setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                            message.setFavorite(false);
                            message.setMessage(messageEdit.getText().toString());
                            message.setCategoryname(categoryToSave);
                            message.setCategoryid(null);
                            MessagesModel.getInstance().insert(message, MyDialog.this);
                            Toast.makeText(MyDialog.this, "Message added successfully.", Toast.LENGTH_LONG).show();
                            add.dismiss();
                        }
                    }
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

    public void addMessage(String message) {
        final Dialog mDialog = new Dialog(MyDialog.this);
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
        addMessageCall = restInterface.addMessage(Utils.getDeviceID(MyDialog.this), message);
        addMessageCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mDialog.dismiss();
                try {
                    if (!response.body().equals("")) {
                        Toast.makeText(MyDialog.this, "Message posted successfully.", Toast.LENGTH_LONG).show();
                        goPublic = false;
                        myDialog.finish();
                    } else {
                        Toast.makeText(MyDialog.this, "Error posting message.", Toast.LENGTH_LONG).show();
                        myDialog.finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MyDialog.this, "No internet connection.", Toast.LENGTH_LONG).show();
                mDialog.dismiss();
                t.printStackTrace();
                myDialog.finish();
            }
        });
    }
}

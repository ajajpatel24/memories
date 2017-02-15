package com.messagitory;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Settings extends AppCompatActivity {
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1234;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 5678;
    public static String dbName = "MessagiToryDB";
    public static int dbVersion = 1;
    public String filePath = "";
    Toolbar toolbar;
    Switch aSwitch;
    TextView title, lastBackup;
    Button backup, restore;
    String TAG = "Settings";
    ProgressDialog pDialog;

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        title = (TextView) findViewById(R.id.title);
        title.setText("Settings");
        SharedPreferences prefs = MyApplication.preferences;
        String lastBackupDate = prefs.getString("lastBackupDate", "No Backup Found");
        lastBackup = (TextView) findViewById(R.id.backupLastDate);
        if (lastBackupDate.contains("No")) {
            lastBackup.setText(lastBackupDate);
        } else {
            lastBackup.setText("Last Backup : " + lastBackupDate);
        }
        aSwitch = (Switch) findViewById(R.id.switch1);
        backup = (Button) findViewById(R.id.backup);
        restore = (Button) findViewById(R.id.restore);
        if (ServiceModel.getInstance().get(this).equals("ON")) {
            aSwitch.setChecked(true);
        } else {
            aSwitch.setChecked(false);
        }
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Tracker t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                if (b) {
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Service")
                            .setAction("Start service")
                            .setLabel("StartService")
                            .build());
                    ServiceModel.getInstance().update(Settings.this, "ON");
                    startChatHead();
                } else {
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Service")
                            .setAction("Stop service")
                            .setLabel("StopService")
                            .build());
                    ServiceModel.getInstance().update(Settings.this, "OFF");
                    Intent it = new Intent(Settings.this, ChatHeadService.class);
                    it.putExtra(Utils.EXTRA_MSG, "test");
                    stopService(it);
                }
            }
        });
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(0);
            }
        });
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(1);
            }
        });
        pDialog = new ProgressDialog(Settings.this);
        pDialog.setMessage("Checking for backup...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        pDialog.show();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isReadStoragePermissionGranted()) {
                    walkdir(Environment.getExternalStorageDirectory());
                }
            }
        }, 400);

        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                pDialog.dismiss();
            }
        }, 2500);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startChatHead() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!android.provider.Settings.canDrawOverlays(this)) {
                requestPermission(OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG);
            } else {
                showChatHeadMsg();

            }
        } else {
            startService(new Intent(Settings.this, ChatHeadService.class));
        }
    }

    private void showChatHeadMsg() {
        Intent it = new Intent(Settings.this, ChatHeadService.class);
        startService(it);
    }

    private void needPermissionDialog(final int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
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

    private void requestPermission(int requestCode) {
        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION_REQ_CODE_CHATHEAD) {
            if (!Utils.canDrawOverlays(Settings.this)) {
                needPermissionDialog(requestCode);
            } else {
                startChatHead();
            }
        }
    }

    private void exportDB() {
        if (isReadStoragePermissionGranted()) {
            File direct = new File(Environment.getExternalStorageDirectory() + "/MessagiTory/DatabaseBackup");
            if (!direct.exists()) {
                if (direct.mkdir()) {

                }
            } else {
                try {
                    FileUtils.cleanDirectory(direct);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                MessagesModel.getInstance().messageBackup(Settings.this);
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();
                if (sd.canWrite()) {
                    String currentDBPath = "//data//" + "com.messagitory"
                            + "//databases//" + "MessagiToryDB";
                    long date = Calendar.getInstance().getTimeInMillis();
                    String backupDBPath = "/MessagiTory/DatabaseBackup/MessagiToryDB" + date;
                    File currentDB = new File(data, currentDBPath);
                    File backupDB = new File(sd, backupDBPath);

                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    SharedPreferences.Editor editor = MyApplication.preferences.edit();
                    editor.putString("lastBackupDate", getDate(date));
                    editor.commit();
                    lastBackup.setText("Last Backup : " + getDate(date));
                    Toast.makeText(getBaseContext(), "Backup completed successfully.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "Check Storage Permission.",
                            Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private void importDB() {
        if (isReadStoragePermissionGranted()) {
            try {
                DatabaseHelperClass helper = new DatabaseHelperClass(this, Utils.dbName,
                        null, Utils.dbVersion);
                helper.getWritableDatabase();
                helper.getReadableDatabase();
                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();
                if (!data.exists()) {
                    data.mkdir();
                }
                if (sd.canWrite()) {
                    String files[] = filePath.split("/");
                    String currentDBPath = "//data//" + "com.messagitory"
                            + "//databases//" + "MessagiToryDB";
                    String backupDBPath = "/" + files[files.length - 3] + "/" + files[files.length - 2] + "/" + files[files.length - 1];
                    File backupDB = new File(data, currentDBPath);
                    File currentDB = new File(sd, backupDBPath);
                    if (!backupDB.exists()) {
                        backupDB.mkdir();
                    }
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getBaseContext(), "Messages restored successfully.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "Check Storage Permission.",
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void walkdir(File dir) {
        String filename = "";
        File listFile[] = dir.listFiles();
        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    walkdir(listFile[i]);
                } else {
                    if (listFile[i].getName().contains("MessagiToryDB")) {
                        filename = listFile[i].getName();
                        filename = filename.substring(filename.indexOf("B") + 1, filename.length());
                        lastBackup.setText("Last Backup : " + getDate(Long.parseLong(filename)));
                        SharedPreferences.Editor editor = MyApplication.preferences.edit();
                        editor.putString("lastBackupDate", getDate(Long.parseLong(filename)));
                        editor.commit();
                        filePath = listFile[i].getAbsolutePath();
                    }
                }
            }
        }
    }

    public void showAlertDialog(final int flag) {
        final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_MyApp)).create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setMessage("Are you sure ?");
        alertDialog.setCancelable(false);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterfpace, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (flag == 0) {
                    exportDB();
                } else {
                    importDB();
                }
            }
        });
        alertDialog.show();
    }

    public boolean isReadStoragePermissionGranted() {
        Log.e("permission", "permission");
        String[] perms = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
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
}

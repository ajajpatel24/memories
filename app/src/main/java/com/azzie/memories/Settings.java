package com.azzie.memories;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class Settings extends AppCompatActivity {
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1234;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 5678;
    Toolbar toolbar;
    Switch aSwitch;
    TextView title;

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
        title = (TextView) findViewById(R.id.title);
        title.setText("Settings");
        aSwitch = (Switch) findViewById(R.id.switch1);
        SharedPreferences prefs = getSharedPreferences("Memories", MODE_PRIVATE);
        boolean on = prefs.getBoolean("service", true);
        if (on) {
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
                    SharedPreferences.Editor editor = getSharedPreferences("Memories", MODE_PRIVATE).edit();
                    editor.putBoolean("service", true);
                    editor.commit();
                    startChatHead();
                } else {
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Service")
                            .setAction("Stop service")
                            .setLabel("StopService")
                            .build());
                    SharedPreferences.Editor editor = getSharedPreferences("Memories", MODE_PRIVATE).edit();
                    editor.putBoolean("service", false);
                    editor.commit();
                    Intent it = new Intent(Settings.this, ChatHeadService.class);
                    it.putExtra(Utils.EXTRA_MSG, "test");
                    stopService(it);
                }
            }
        });
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
        java.util.Date now = new java.util.Date();
        String str = "test by henry  " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);

        Intent it = new Intent(Settings.this, ChatHeadService.class);
        it.putExtra(Utils.EXTRA_MSG, str);
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
}

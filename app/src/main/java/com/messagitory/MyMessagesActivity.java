package com.messagitory;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyMessagesActivity extends AppCompatActivity implements ClickCallback {
    ListView messages;
    TimeLineAdapter adapter;
    ArrayList<MessageReponse> messagesList;
    LinearLayout noMessages;
    TextView title;
    Toolbar toolbar;
    Menu menu;
    SwipeRefreshLayout swipeRefreshLayout;
    Call<ArrayList<MessageReponse>> messageCall;
    Call<String> addMessageCall;
    String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
//        goPublic = (LinearLayout) findViewById(R.id.publics);
//        noofmessages = (TextView) findViewById(R.id.noofmessages);
//        goPublic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent i = new Intent(Main.this, MyMessagesActivity.class);
////                startActivity(i);
//            }
//        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.title);
        Log.e("Messages", "Public Messages");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        messages = (ListView) findViewById(R.id.messages);
        noMessages = (LinearLayout) findViewById(R.id.noMessages);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        messagesList = new ArrayList<>();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMessages();
            }
        });
        deviceID = Utils.getDeviceID(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.add:
                showDialogMessage();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDialogMessage() {
        final Dialog add;
        final EditText messageEdit;
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
        messageEdit = (EditText) add.findViewById(R.id.message);
        save = (Button) add.findViewById(R.id.add);
        cancel = (Button) add.findViewById(R.id.cancel);
        title = (TextView) add.findViewById(R.id.title);
        title.setText("Add Message");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (messageEdit.getText().toString().equals("")) {
                    Toast.makeText(MyMessagesActivity.this, "Message cannot be empty.", Toast.LENGTH_LONG).show();
                } else if (messageEdit.getText().toString().length() > 240) {
                    Toast.makeText(MyMessagesActivity.this, "Message should be less than 240 characters.", Toast.LENGTH_LONG).show();
                } else {
                    Tracker t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Messages")
                            .setAction("Add new Public message")
                            .setLabel("AddMessage")
                            .build());
                    add.dismiss();
                    addMessage(messageEdit.getText().toString());
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
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        noofMessages();
        getMessages();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.people, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void getMessages() {
        final Dialog mDialog = new Dialog(MyMessagesActivity.this);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.layout_progressbar);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface mDialog) {
                messageCall.cancel();
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
        messageCall = restInterface.getMessageListForUser(deviceID);
        messageCall.enqueue(new Callback<ArrayList<MessageReponse>>() {
            @Override
            public void onResponse(Call<ArrayList<MessageReponse>> call, Response<ArrayList<MessageReponse>> response) {
                mDialog.dismiss();
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                try {
                    messagesList = response.body();
                    if (messagesList.size() == 0) {
                        noMessages.setVisibility(View.VISIBLE);
                        messages.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.GONE);
                    } else {
                        noMessages.setVisibility(View.GONE);
                        messages.setVisibility(View.VISIBLE);
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        adapter = new TimeLineAdapter(MyMessagesActivity.this, messagesList);
                        messages.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<MessageReponse>> call, Throwable t) {
                mDialog.dismiss();
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                noMessages.setVisibility(View.VISIBLE);
                messages.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
                Toast.makeText(MyMessagesActivity.this, "No internet connection.", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    public void addMessage(String message) {
        final Dialog mDialog = new Dialog(MyMessagesActivity.this);
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
        addMessageCall = restInterface.addMessage(deviceID, message);
        addMessageCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mDialog.dismiss();
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                try {
                    if (!response.body().equals("")) {
                        Toast.makeText(MyMessagesActivity.this, "Message posted successfully.", Toast.LENGTH_LONG).show();
                        getMessages();
                    } else {
                        Toast.makeText(MyMessagesActivity.this, "Error posting message.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MyMessagesActivity.this, "No internet connection.", Toast.LENGTH_LONG).show();
                mDialog.dismiss();
                t.printStackTrace();
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void like(String messageId) {
        final Dialog mDialog = new Dialog(MyMessagesActivity.this);
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
                .baseUrl(Utils.BaseURL + "Service/Messagitory/Like/" + messageId + "/").addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient)
                .build();
        RestInterface restInterface = retrofit.create(RestInterface.class);
        addMessageCall = restInterface.like(deviceID, "");
        addMessageCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mDialog.dismiss();
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                try {
                    if (!response.body().equals("")) {
                        adapter.notifyDataSetChanged();
                        ;
//                        Toast.makeText(MyMessagesActivity.this, "Message posted successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MyMessagesActivity.this, "Please try again.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MyMessagesActivity.this, "No internet connection.", Toast.LENGTH_LONG).show();
                mDialog.dismiss();
                t.printStackTrace();
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void report(String messageId) {
        final Dialog mDialog = new Dialog(MyMessagesActivity.this);
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
                .baseUrl(Utils.BaseURL + "Service/Messagitory/Report/" + messageId + "/").addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient)
                .build();
        RestInterface restInterface = retrofit.create(RestInterface.class);
        addMessageCall = restInterface.report(deviceID, "");
        addMessageCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mDialog.dismiss();
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                try {
                    if (!response.body().equals("")) {
//                        Toast.makeText(MyMessagesActivity.this, "Message posted successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MyMessagesActivity.this, "Please try again.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MyMessagesActivity.this, "No internet connection.", Toast.LENGTH_LONG).show();
                mDialog.dismiss();
                t.printStackTrace();
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
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
        noofMessageCall = restInterface.noofMessages(deviceID);
        noofMessageCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
//                    if (!response.body().equals("")) {
//                        noofmessages.setText("Your Messages (" + response.body() + ")");
//                    } else {
//                        noofmessages.setText("Your Messages (" + 0 + ")");
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MyMessagesActivity.this, "No internet connection.", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void doLike(String id) {
        like(id);
    }

    @Override
    public void doReport(String id) {

    }
}

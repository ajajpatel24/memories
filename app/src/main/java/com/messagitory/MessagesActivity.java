package com.messagitory;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.utils.SpotlightListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessagesActivity extends AppCompatActivity {
    ListView messages;
    MessagesAdapter adapter;
    ArrayList<Message> messagesList;
    LinearLayout noMessages;
    TextView title;
    Toolbar toolbar, searchToolbar;
    boolean multiSelection = false, search = false;
    Menu menu;
    ImageButton close;
    EditText edtSearch;
    boolean favorite = false;
    private int mDay, mMonth, mYear, mEndDay, mEndMonth, mEndYear;
    private String mStartDate, mEndDate;
    private int no = 10;
    private Date mDate, mCheckStartDate, mCheckEndDate;
    Call<String> addMessageCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchToolbar = (Toolbar) findViewById(R.id.toolbarSearch);
        Log.e("Messages", "Messages");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        favorite = getIntent().getBooleanExtra("favorite", false);
        messagesList = new ArrayList<>();
        if (favorite) {
            messagesList = MessagesModel.getInstance().getFavoriteMessages(this);
        } else {
            messagesList = MessagesModel.getInstance().getMessagesByCategory(this, getIntent().getStringExtra("categoryName"));
        }
        adapter = new MessagesAdapter(this, messagesList);
        messages = (ListView) findViewById(R.id.messages);
        noMessages = (LinearLayout) findViewById(R.id.noMessages);
        title = (TextView) findViewById(R.id.title);
        edtSearch = (EditText) findViewById(R.id.searchView);
        close = (ImageButton) findViewById(R.id.close);
        title.setText(getIntent().getStringExtra("categoryName"));
        if (messagesList.size() > 0) {
            messages.setVisibility(View.VISIBLE);
            noMessages.setVisibility(View.GONE);
            messages.setAdapter(adapter);
        } else {
            messages.setVisibility(View.GONE);
            noMessages.setVisibility(View.VISIBLE);
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.filter("");
                edtSearch.setText("");
                searchToolbar.setVisibility(View.GONE);
                toolbar.setVisibility(View.VISIBLE);
                search = false;
                View view1 = MessagesActivity.this.getCurrentFocus();
                if (view1 != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filter(edtSearch.getText().toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handleInstruction();
            }
        }, 1500);
    }

    public void handleInstruction() {
        new SpotlightView.Builder(this)
                .introAnimationDuration(400)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(Color.parseColor("#ffffff"))
                .headingTvSize(32)
                .headingTvText("Add Message")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText("Click on button to add new message.")
                .maskColor(Color.parseColor("#80000000"))
                .target(findViewById(R.id.add))
                .lineAnimDuration(400)
                .lineAndArcColor(Color.parseColor("#2196F3"))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId("Add Message").setListener(new SpotlightListener() {
            @Override
            public void onUserClicked(String s) {
                new SpotlightView.Builder(MessagesActivity.this)
                        .introAnimationDuration(400)
                        .performClick(true)
                        .fadeinTextDuration(400)
                        .headingTvColor(Color.parseColor("#ffffff"))
                        .headingTvSize(32)
                        .headingTvText("Search Message")
                        .subHeadingTvColor(Color.parseColor("#ffffff"))
                        .subHeadingTvSize(16)
                        .subHeadingTvText("Click on button to search message you are looking for.")
                        .maskColor(Color.parseColor("#80000000"))
                        .target(findViewById(R.id.search))
                        .lineAnimDuration(400)
                        .lineAndArcColor(Color.parseColor("#2196F3"))
                        .dismissOnTouch(true)
                        .dismissOnBackPress(true)
                        .enableDismissAfterShown(true)
                        .usageId("Search Message") //UNIQUE ID
                        .setListener(new SpotlightListener() {
                            @Override
                            public void onUserClicked(String s) {
                                new SpotlightView.Builder(MessagesActivity.this)
                                        .introAnimationDuration(400)
                                        .performClick(true)
                                        .fadeinTextDuration(400)
                                        .headingTvColor(Color.parseColor("#ffffff"))
                                        .headingTvSize(32)
                                        .headingTvText("Message Filtering")
                                        .subHeadingTvColor(Color.parseColor("#ffffff"))
                                        .subHeadingTvSize(16)
                                        .subHeadingTvText("Click on button to filter messages Date Wise.")
                                        .maskColor(Color.parseColor("#80000000"))
                                        .target(findViewById(R.id.filter))
                                        .lineAnimDuration(400)
                                        .lineAndArcColor(Color.parseColor("#2196F3"))
                                        .dismissOnTouch(true)
                                        .dismissOnBackPress(true)
                                        .enableDismissAfterShown(true)
                                        .usageId("Filter").setListener(new SpotlightListener() {
                                    @Override
                                    public void onUserClicked(String s) {
                                        new SpotlightView.Builder(MessagesActivity.this)
                                                .introAnimationDuration(400)
                                                .performClick(true)
                                                .fadeinTextDuration(400)
                                                .headingTvColor(Color.parseColor("#ffffff"))
                                                .headingTvSize(32)
                                                .headingTvText("Paste Message")
                                                .subHeadingTvColor(Color.parseColor("#ffffff"))
                                                .subHeadingTvSize(16)
                                                .subHeadingTvText("Click on button to paste the message you have copied.")
                                                .maskColor(Color.parseColor("#80000000"))
                                                .target(findViewById(R.id.paste))
                                                .lineAnimDuration(400)
                                                .lineAndArcColor(Color.parseColor("#2196F3"))
                                                .dismissOnTouch(true)
                                                .dismissOnBackPress(true)
                                                .enableDismissAfterShown(true)
                                                .usageId("Paste").show();
                                    }
                                }).show();
                            }
                        }).show();
            }
        }).show();
    }

    public boolean isMultiSelection() {
        return multiSelection;
    }

    public void setMultiSelection(boolean multiSelection) {
        this.multiSelection = multiSelection;
    }

    public void refresh() {
        messagesList = new ArrayList<>();
        if (favorite) {
            messagesList = MessagesModel.getInstance().getFavoriteMessages(this);
        } else {
            messagesList = MessagesModel.getInstance().getMessagesByCategory(this, getIntent().getStringExtra("categoryName"));
        }
        adapter = new MessagesAdapter(this, messagesList);
        messages = (ListView) findViewById(R.id.messages);
        if (messagesList.size() > 0) {
            messages.setVisibility(View.VISIBLE);
            noMessages.setVisibility(View.GONE);
            messages.setAdapter(adapter);
        } else {
            messages.setVisibility(View.GONE);
            noMessages.setVisibility(View.VISIBLE);
        }
    }

    public void makeDates() {
        Log.e("startdate", "Date " + mStartDate);
        Log.e("enddate", "Date " + mEndDate);
        int day, month, year, endday = 0, endmonth = 0, endyear = 0;
        String startSplit[] = mStartDate.split("/");
        day = Integer.parseInt(startSplit[0]);
        month = Integer.parseInt(startSplit[1]) - 1;
        year = Integer.parseInt(startSplit[2]);
        if (!TextUtils.isEmpty(mEndDate)) {
            String endSplit[] = mEndDate.split("/");
            endday = Integer.parseInt(endSplit[0]);
            endmonth = Integer.parseInt(endSplit[1]) - 1;
            endyear = Integer.parseInt(endSplit[2]);
        }
        try {
            Calendar temp = Calendar.getInstance();
            if (no == 0 || no == 5) {
                if (no == 5) {
                    temp.set(Calendar.MONTH, month);
                    temp.set(Calendar.DAY_OF_MONTH, day);
                    temp.set(Calendar.YEAR, year);
                    temp.set(Calendar.HOUR_OF_DAY, 00);
                    temp.set(Calendar.MINUTE, 00);
                    temp.set(Calendar.SECOND, 01);
                    mStartDate = String.valueOf(temp.getTimeInMillis());
                    Log.e("Start", "Date " + mStartDate);
                    temp.set(Calendar.MONTH, month);
                    temp.set(Calendar.DAY_OF_MONTH, day);
                    temp.set(Calendar.YEAR, year);
                    temp.set(Calendar.HOUR_OF_DAY, 23);
                    temp.set(Calendar.MINUTE, 59);
                    temp.set(Calendar.SECOND, 59);
                    mEndDate = String.valueOf(temp.getTimeInMillis());
                    Log.e("End", "Date " + mEndDate);
                } else {
                    temp.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
                    temp.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                    temp.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                    temp.set(Calendar.HOUR_OF_DAY, 00);
                    temp.set(Calendar.MINUTE, 00);
                    temp.set(Calendar.SECOND, 01);
                    mStartDate = String.valueOf(temp.getTimeInMillis());
                    Log.e("Start", "Date " + temp.getTimeInMillis());
                    temp = Calendar.getInstance();
                    mEndDate = String.valueOf(temp.getTimeInMillis());
                    Log.e("End", "Date " + temp.getTimeInMillis());
                }
            } else {
                temp.set(Calendar.MONTH, month);
                temp.set(Calendar.DAY_OF_MONTH, day);
                temp.set(Calendar.YEAR, year);
                temp.set(Calendar.HOUR_OF_DAY, 00);
                temp.set(Calendar.MINUTE, 00);
                temp.set(Calendar.SECOND, 01);
                mStartDate = String.valueOf(temp.getTimeInMillis());
                Log.e("Start", "Date " + mStartDate);
                temp.set(Calendar.MONTH, endmonth);
                temp.set(Calendar.DAY_OF_MONTH, endday);
                temp.set(Calendar.YEAR, endyear);
                temp.set(Calendar.HOUR_OF_DAY, 23);
                temp.set(Calendar.MINUTE, 59);
                temp.set(Calendar.SECOND, 59);
                mEndDate = String.valueOf(temp.getTimeInMillis());
                Log.e("End", "Date " + mEndDate);
            }
            filter(mStartDate, mEndDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Tracker t;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.copy:
                multiSelection = false;
                adapter.copySelected();
                break;
            case R.id.selectall:
                adapter.selectAll();
                break;
            case R.id.search:
                t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                t.send(new HitBuilders.EventBuilder()
                        .setCategory("Messages")
                        .setAction("Search Message")
                        .setLabel("Search")
                        .build());
                Log.e("Search", "Called");
                searchToolbar.setVisibility(View.VISIBLE);
                edtSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edtSearch, InputMethodManager.SHOW_IMPLICIT);
                toolbar.setVisibility(View.GONE);
                search = true;
                break;
            case R.id.delete:
                final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(MessagesActivity.this, R.style.Theme_MyApp)).create();
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setMessage("Are you sure you want to delete ?");
                alertDialog.setCancelable(false);
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();

                    }
                });
                alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        adapter.deleteSelected();
                        refresh();
                        multiSelection = false;
                        adapter.unselectAll();
                        menu.findItem(R.id.copy).setVisible(false);
                        menu.findItem(R.id.selectall).setVisible(false);
                        menu.findItem(R.id.delete).setVisible(false);
                        menu.findItem(R.id.filter).setVisible(true);
                        menu.findItem(R.id.paste).setVisible(true);
                        menu.findItem(R.id.search).setVisible(true);
                        menu.findItem(R.id.add).setVisible(true);
                        title.setText(getIntent().getStringExtra("categoryName"));
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                break;
            case R.id.filter:
                showDatePickerDialog();
                break;
            case R.id.add:
                showDialogMessage(true, "", "");
                break;
            case R.id.paste:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard.getPrimaryClip() != null) {
                    ClipData.Item item1 = clipboard.getPrimaryClip().getItemAt(0);
                    if (MessagesModel.getInstance().checkMessage(MessagesActivity.this, item1.getText().toString())) {
                        Toast.makeText(MessagesActivity.this, "Message already exists.", Toast.LENGTH_LONG).show();
                    } else {
                        Message message = new Message();
                        message.setCustome(true);
                        message.setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                        message.setFavorite(false);
                        message.setMessage(item1.getText().toString());
                        message.setCategoryname(MessagesActivity.this.getIntent().getStringExtra("categoryName"));
                        message.setCategoryid(null);
                        MessagesModel.getInstance().insert(message, MessagesActivity.this);
                        Toast.makeText(MessagesActivity.this, "Message added successfully.", Toast.LENGTH_LONG).show();
                        refresh();
                    }
                    t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Messages")
                            .setAction("Add new message")
                            .setLabel("AddMessage")
                            .build());
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDialogMessage(final boolean action, final String oldvalue, String newvalue) {
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
        messageEdit = (EditText) add.findViewById(R.id.message);
        save = (Button) add.findViewById(R.id.add);
        cancel = (Button) add.findViewById(R.id.cancel);
        title = (TextView) add.findViewById(R.id.title);
        if (action)
            title.setText("Add Message");
        else
            title.setText("Update Message");
        if (action)
            messageEdit.setText("");
        else
            messageEdit.setText(oldvalue);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (messageEdit.getText().toString().equals("")) {
                    Toast.makeText(MessagesActivity.this, "Message cannot be empty.", Toast.LENGTH_LONG).show();
                } else {
                    if (MessagesModel.getInstance().checkMessage(MessagesActivity.this, messageEdit.getText().toString())) {
                        Toast.makeText(MessagesActivity.this, "Message already exists.", Toast.LENGTH_LONG).show();
                    } else {
                        Message message = new Message();
                        message.setCustome(true);
                        message.setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                        message.setFavorite(false);
                        message.setMessage(messageEdit.getText().toString());
                        message.setCategoryname(MessagesActivity.this.getIntent().getStringExtra("categoryName"));
                        message.setCategoryid(null);
                        if (action) {
                            MessagesModel.getInstance().insert(message, MessagesActivity.this);
                            Toast.makeText(MessagesActivity.this, "Message added successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            MessagesModel.getInstance().updateMessage(MessagesActivity.this, oldvalue, messageEdit.getText().toString());
                            Toast.makeText(MessagesActivity.this, "Message updated successfully.", Toast.LENGTH_LONG).show();
                        }
                        refresh();
                        add.dismiss();
                    }
                    Tracker t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                    t.send(new HitBuilders.EventBuilder()
                            .setCategory("Messages")
                            .setAction("Add new message")
                            .setLabel("AddMessage")
                            .build());
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
        if (search) {
            edtSearch.setText("");
            adapter.filter("");
            searchToolbar.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            search = false;
        } else if (multiSelection) {
            multiSelection = false;
            menu.findItem(R.id.copy).setVisible(false);
            menu.findItem(R.id.selectall).setVisible(false);
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.filter).setVisible(true);
            menu.findItem(R.id.paste).setVisible(true);
            menu.findItem(R.id.add).setVisible(true);
            menu.findItem(R.id.search).setVisible(true);
            title.setText(getIntent().getStringExtra("categoryName"));
            adapter.unselectAll();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        menu.findItem(R.id.copy).setVisible(false);
        menu.findItem(R.id.selectall).setVisible(false);
        menu.findItem(R.id.delete).setVisible(false);
        menu.findItem(R.id.filter).setVisible(true);
        menu.findItem(R.id.paste).setVisible(true);
        menu.findItem(R.id.search).setVisible(true);
        menu.findItem(R.id.add).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.messages, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void changeMenu(int count, boolean value) {
        if (value) {
            title.setText(count + " Selected");
            menu.findItem(R.id.copy).setVisible(true);
            menu.findItem(R.id.selectall).setVisible(true);
            menu.findItem(R.id.delete).setVisible(true);
            menu.findItem(R.id.filter).setVisible(false);
            menu.findItem(R.id.paste).setVisible(false);
            menu.findItem(R.id.search).setVisible(false);
            menu.findItem(R.id.add).setVisible(false);
        } else {
            menu.findItem(R.id.copy).setVisible(false);
            menu.findItem(R.id.selectall).setVisible(false);
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.filter).setVisible(true);
            menu.findItem(R.id.paste).setVisible(true);
            menu.findItem(R.id.search).setVisible(true);
            menu.findItem(R.id.add).setVisible(true);
            title.setText(getIntent().getStringExtra("categoryName"));
        }
    }

    public void showDatePickerDialog() {
        final Dialog dialog;
        ArrayList<String> listOfMenuOptions = new ArrayList<>();
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog);
        //header = (TextView) mDialog.findViewById(R.id.header);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        Toolbar toolbar = (Toolbar) dialog.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        ListView menuoptions = (ListView) dialog.findViewById(R.id.sortmenu);
        listOfMenuOptions.add("Today");
        listOfMenuOptions.add("Last 7 Days");
        listOfMenuOptions.add("Month To Date");
        listOfMenuOptions.add("Year To Date");
        listOfMenuOptions.add("The Previous Month");
        listOfMenuOptions.add("Specific Date");
        listOfMenuOptions.add("All Dates Before");
        listOfMenuOptions.add("All Dates After");
        listOfMenuOptions.add("Date Range");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.menu_item, listOfMenuOptions);
        menuoptions.setAdapter(adapter);
        menuoptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Calendar myCalendar = Calendar.getInstance();

            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    String myFormat = "dd/MM/yyyy"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                    if (no == 6) {
                        mStartDate = "01/01/2001";
                        mEndDate = sdf.format(myCalendar.getTime());
                        //mOrderDate.setText(mStartDate + " - " + mEndDate);
                    } else if (no == 7) {
                        mStartDate = sdf.format(myCalendar.getTime());
                        mEndDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "/" + Calendar.getInstance().get(Calendar.YEAR);
                        //mOrderDate.setText(mStartDate + " - " + mEndDate);
                    } else {
                        mStartDate = sdf.format(myCalendar.getTime());
                        mEndDate = "";
                        //mOrderDate.setText(mStartDate);
                    }
                    makeDates();
                }

            };

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myCalendar.set(Calendar.YEAR, myCalendar.get(Calendar.YEAR));
                myCalendar.set(Calendar.MONTH, myCalendar.get(Calendar.MONTH));
                myCalendar.set(Calendar.DAY_OF_MONTH, myCalendar.get(Calendar.DAY_OF_MONTH));
                switch (position) {
                    case 0:
                        String myFormat = "dd/MM/yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                        mStartDate = sdf.format(myCalendar.getTime());
                        mEndDate = "";
                        //mOrderDate.setText(sdf.format(myCalendar.getTime()));
                        no = 0;
                        makeDates();
                        break;
                    case 1:
                        mEndMonth = myCalendar.get(Calendar.MONTH) + 1;
                        mEndYear = myCalendar.get(Calendar.YEAR);
                        mEndDay = myCalendar.get(Calendar.DAY_OF_MONTH);
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DATE, -6);
                        mDay = cal.get(Calendar.DAY_OF_MONTH);
                        mMonth = cal.get(Calendar.MONTH) + 1;
                        mYear = cal.get(Calendar.YEAR);
                        mStartDate = mDay + "/" + mMonth + "/" + mYear;
                        mEndDate = mEndDay + "/" + mEndMonth + "/" + mEndYear;
                        //mOrderDate.setText(mDay + "/" + mMonth + "/" + mYear + " - " + mEndDay + "/" + mEndMonth + "/" + mEndYear);
                        no = 1;
                        makeDates();
                        break;
                    case 2:
                        mEndMonth = myCalendar.get(Calendar.MONTH) + 1;
                        mEndYear = myCalendar.get(Calendar.YEAR);
                        mEndDay = myCalendar.get(Calendar.DAY_OF_MONTH);
                        mDay = 1;
                        mMonth = myCalendar.get(Calendar.MONTH) + 1;
                        mYear = myCalendar.get(Calendar.YEAR);
                        mStartDate = mDay + "/" + mMonth + "/" + mYear;
                        mEndDate = mEndDay + "/" + mEndMonth + "/" + mEndYear;
                        //mOrderDate.setText(mDay + "/" + mMonth + "/" + mYear + " - " + mEndDay + "/" + mEndMonth + "/" + mEndYear);
                        no = 2;
                        makeDates();
                        break;
                    case 3:
                        mEndMonth = myCalendar.get(Calendar.MONTH) + 1;
                        mEndYear = myCalendar.get(Calendar.YEAR);
                        mEndDay = myCalendar.get(Calendar.DAY_OF_MONTH);
                        mDay = 1;
                        mMonth = 1;
                        mYear = myCalendar.get(Calendar.YEAR);
                        mStartDate = mDay + "/" + mMonth + "/" + mYear;
                        mEndDate = mEndDay + "/" + mEndMonth + "/" + mEndYear;
                        //mOrderDate.setText(mDay + "/" + mMonth + "/" + mYear + " - " + mEndDay + "/" + mEndMonth + "/" + mEndYear);
                        no = 3;
                        makeDates();
                        break;
                    case 4:
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) - 1, 1);
                        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
                        Date date1 = calendar.getTime();
                        DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
                        mEndMonth = calendar.get(Calendar.MONTH) + 1;
                        mEndYear = calendar.get(Calendar.YEAR);
                        mEndDay = calendar.get(Calendar.DAY_OF_MONTH);
                        mDay = 1;
                        mMonth = calendar.get(Calendar.MONTH) + 1;
                        mYear = calendar.get(Calendar.YEAR);
                        mStartDate = mDay + "/" + mMonth + "/" + mYear;
                        mEndDate = mEndDay + "/" + mEndMonth + "/" + mEndYear;
                        //mOrderDate.setText(mDay + "/" + mMonth + "/" + mYear + " - " + mEndDay + "/" + mEndMonth + "/" + mEndYear);
                        no = 4;
                        makeDates();
                        break;
                    case 5:
                        new DatePickerDialog(MessagesActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        no = 5;
                        break;
                    case 6:
                        no = 6;
                        new DatePickerDialog(MessagesActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        no = 6;
                        break;
                    case 7:
                        no = 7;
                        new DatePickerDialog(MessagesActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        no = 7;
                        break;
                    case 8:
                        no = 8;
                        Calendar now = Calendar.getInstance();
                        com.borax12.materialdaterangepicker.date.DatePickerDialog dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                                new com.borax12.materialdaterangepicker.date.DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(com.borax12.materialdaterangepicker.date.DatePickerDialog view, int year1, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
                                        mEndMonth = monthOfYearEnd + 1;
                                        mEndYear = yearEnd;
                                        mEndDay = dayOfMonthEnd;
                                        mDay = dayOfMonth;
                                        mMonth = monthOfYear + 1;
                                        mYear = year1;
                                        mStartDate = mDay + "/" + mMonth + "/" + mYear;
                                        mEndDate = mEndDay + "/" + mEndMonth + "/" + mEndYear;
                                        makeDates();
                                        //mOrderDate.setText(mDay + "/" + mMonth + "/" + mYear + " - " + mEndDay + "/" + "/" + mEndMonth + "/" + mEndYear);
                                    }
                                },
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.show(getFragmentManager(), "Datepickerdialog");
                        break;
                    default:
                        break;
                }
                //Toast.makeText(MessagesActivity.this, "kai pan", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void filter(String startDate, String endDate) {
        Tracker t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        t.send(new HitBuilders.EventBuilder()
                .setCategory("Messages")
                .setAction("Filter messages")
                .setLabel("FilterMessage")
                .build());
        messagesList = new ArrayList<>();
        if (favorite) {
            messagesList = MessagesModel.getInstance().filterFavoriteByDate(this, startDate, endDate);
        } else {
            messagesList = MessagesModel.getInstance().filterByDate(this, startDate, endDate);
        }
        adapter = new MessagesAdapter(this, messagesList);
        messages = (ListView) findViewById(R.id.messages);
        if (messagesList.size() > 0) {
            messages.setVisibility(View.VISIBLE);
            noMessages.setVisibility(View.GONE);
            messages.setAdapter(adapter);
        } else {
            messages.setVisibility(View.GONE);
            noMessages.setVisibility(View.VISIBLE);
        }
    }

    public void addMessage(String message) {
        final Dialog mDialog = new Dialog(MessagesActivity.this);
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
        addMessageCall = restInterface.addMessage(Utils.getDeviceID(MessagesActivity.this), message);
        addMessageCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mDialog.dismiss();
                try {
                    if (!response.body().equals("")) {
                        Toast.makeText(MessagesActivity.this, "Message posted successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MessagesActivity.this, "Error posting message.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(MessagesActivity.this, "No internet connection.", Toast.LENGTH_LONG).show();
                mDialog.dismiss();
                t.printStackTrace();
            }
        });
    }
}

package com.azzie.memories;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FavoriteMessages extends AppCompatActivity {
    ListView messages;
    FavoriteMessagesAdapter adapter;
    ArrayList<Message> messagesList;
    LinearLayout noMessages;
    TextView title;
    Toolbar toolbar;
    boolean multiSelection = false;
    Menu menu;
    private int mDay, mMonth, mYear, mEndDay, mEndMonth, mEndYear;
    private String mStartDate, mEndDate;
    private int no = 10;
    private Date mDate, mCheckStartDate, mCheckEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        messagesList = new ArrayList<>();
        messagesList = MessagesModel.getInstance().getFavoriteMessages(this);
        adapter = new FavoriteMessagesAdapter(this, messagesList);
        messages = (ListView) findViewById(R.id.messages);
        noMessages = (LinearLayout) findViewById(R.id.noMessages);
        title = (TextView) findViewById(R.id.title);
        title.setText("Favorite Messages");
        if (messagesList.size() > 0) {
            messages.setVisibility(View.VISIBLE);
            noMessages.setVisibility(View.GONE);
            messages.setAdapter(adapter);
        } else {
            messages.setVisibility(View.GONE);
            noMessages.setVisibility(View.VISIBLE);
        }

    }

    public boolean isMultiSelection() {
        return multiSelection;
    }

    public void setMultiSelection(boolean multiSelection) {
        this.multiSelection = multiSelection;
    }

    public void refresh() {
        messagesList = new ArrayList<>();
        messagesList = MessagesModel.getInstance().getFavoriteMessages(this);
        adapter = new FavoriteMessagesAdapter(this, messagesList);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            case R.id.delete:
                final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(FavoriteMessages.this, R.style.Theme_MyApp)).create();
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
                        menu.findItem(R.id.add).setVisible(false);
                        title.setText("Favorite Messages");
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
                break;
            case R.id.filter:
                showDatePickerDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (multiSelection) {
            multiSelection = false;
            menu.findItem(R.id.copy).setVisible(false);
            menu.findItem(R.id.selectall).setVisible(false);
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.filter).setVisible(true);
            menu.findItem(R.id.add).setVisible(false);
            title.setText("Favorite Messages");
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
        menu.findItem(R.id.add).setVisible(false);
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
            menu.findItem(R.id.add).setVisible(false);
        } else {
            menu.findItem(R.id.copy).setVisible(false);
            menu.findItem(R.id.selectall).setVisible(false);
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.filter).setVisible(true);
            menu.findItem(R.id.add).setVisible(false);
            title.setText("Favorite Messages");
        }
    }
    public void makeDates() {
        Log.e("startdate", "Date " + mStartDate);
        Log.e("enddate", "Date " + mEndDate);
        int day, month, year, endday = 0, endmonth = 0, endyear = 0;
        String startSplit[] = mStartDate.split("/");
        day = Integer.parseInt(startSplit[0]);
        month = Integer.parseInt(startSplit[1])-1;
        year = Integer.parseInt(startSplit[2]);
        if (!TextUtils.isEmpty(mEndDate)) {
            String endSplit[] = mEndDate.split("/");
            endday = Integer.parseInt(endSplit[0]);
            endmonth = Integer.parseInt(endSplit[1])-1;
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
            filter();
        } catch (Exception e) {
            e.printStackTrace();
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
                        new DatePickerDialog(FavoriteMessages.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        no = 5;
                        break;
                    case 6:
                        no = 6;
                        new DatePickerDialog(FavoriteMessages.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                        no = 6;
                        break;
                    case 7:
                        no = 7;
                        new DatePickerDialog(FavoriteMessages.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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
                //Toast.makeText(FavoriteMessages.this, "kai pan", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void filter() {
        messagesList = new ArrayList<>();
        messagesList = MessagesModel.getInstance().filterFavoriteByDate(this, mStartDate, mEndDate);
        adapter = new FavoriteMessagesAdapter(this, messagesList);
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
}

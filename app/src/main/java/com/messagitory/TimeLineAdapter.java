package com.messagitory;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Ajaj Patel on  .
 */
public class TimeLineAdapter extends BaseAdapter {

    private LayoutInflater inflater = null;
    private Activity mActivity;
    private ArrayList<MessageReponse> messages;
    private ArrayList<MessageReponse> messagesCopy;
    private ClickCallback mClickCallback;

    public TimeLineAdapter(Activity activity, ArrayList<MessageReponse> messages) {
        mActivity = activity;
        mClickCallback = (ClickCallback) mActivity;
        this.messages = messages;
        this.messagesCopy = new ArrayList<>();
        this.messagesCopy.addAll(messages);
        Collections.reverse(this.messages);
        Collections.reverse(this.messagesCopy);
        inflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static String getDate(String dateString) {
        String formattedDate = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = df.parse(dateString);
            df.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
            formattedDate = df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        if (convertView == null) {

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.layout_public_message_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/
            holder = new ViewHolder();
            holder.message = (TextView) vi.findViewById(R.id.message);
            holder.likes = (TextView) vi.findViewById(R.id.nooflikes);
            holder.date = (TextView) vi.findViewById(R.id.date);
            holder.linearLayout = (CardView) vi.findViewById(R.id.cardview);
            holder.copy = (ImageButton) vi.findViewById(R.id.copy);
            holder.share = (ImageButton) vi.findViewById(R.id.share);
//            holder.report = (ImageButton) vi.findViewById(R.id.report);
            holder.favorite = (ImageButton) vi.findViewById(R.id.favorite);
            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        loadData(holder, position);

        return vi;

    }

    private void loadData(final ViewHolder holder, final int position) {

        holder.message.setText(messages.get(position).getMessage());
        holder.likes.setText(messages.get(position).getLikes());
        holder.date.setText(messages.get(position).getCreated_at());
        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("messagitory", messages.get(position).getMessage());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mActivity, "Text copied to clipboard", Toast.LENGTH_LONG).show();
            }
        });

        if (messages.get(position).isFavorite()) {
            int color = Color.parseColor("#2196F3"); //The color u want
            holder.favorite.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
            holder.favorite.setColorFilter(color);
        } else {
            holder.favorite.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.favorite));
        }
//        if (messages.get(position).isReport()) {
//            int color = Color.parseColor("#2196F3"); //The color u want
//            holder.report.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_report_white_24dp));
//            holder.report.setColorFilter(color);
//        } else {
//            holder.report.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_report_white_24dp));
//        }
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (messages.get(position).isFavorite()) {
//                    MessagesModel.getInstance().update(mActivity, messages.get(position).getMessage(), false);
//                    messages.get(position).setFavorite(false);
//                    notifyDataSetChanged();
//                } else {
//                    MessagesModel.getInstance().update(mActivity, messages.get(position).getMessage(), true);
//                    messages.get(position).setFavorite(true);
//                    notifyDataSetChanged();
//                }
                if (messages.get(position).isFavorite()) {
                    ;
                } else {
                    messages.get(position).setFavorite(true);
                    messages.get(position).setLikes("" + (Integer.parseInt(messages.get(position).getLikes()) + 1));
                    mClickCallback.doLike(messages.get(position).getId());
                }
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareBody = messages.get(position).getMessage();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                mActivity.startActivity(Intent.createChooser(sharingIntent, "Share Via..."));
            }
        });

    }

    public static class ViewHolder {
        public TextView message, date, likes;
        public CardView linearLayout;
        public ImageButton copy, share;
        public ImageButton favorite;//, report;
    }
}

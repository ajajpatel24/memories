package com.azzie.memories;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.like.LikeButton;
import com.like.OnLikeListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Ajaj Patel on  .
 */
public class FavoriteMessagesAdapter extends BaseAdapter {

    private LayoutInflater inflater = null;
    private Activity mActivity;
    private ArrayList<Message> messages;
    private SparseBooleanArray selected = new SparseBooleanArray();
    private ArrayList<Message> messagesCopy;

    public FavoriteMessagesAdapter(Activity activity, ArrayList<Message> messages) {
        mActivity = activity;
        this.messages = messages;
        this.messagesCopy = messages;
        inflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public static String getDate(long milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
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
            vi = inflater.inflate(R.layout.layout_message, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/
            holder = new ViewHolder();
            holder.txtCategoryName = (TextView) vi.findViewById(R.id.message);
            holder.date = (TextView) vi.findViewById(R.id.date);
            holder.linearLayout = (CardView) vi.findViewById(R.id.cardview);
            holder.copy = (ImageButton) vi.findViewById(R.id.copy);
            holder.share = (ImageButton) vi.findViewById(R.id.share);
            holder.delete = (ImageButton) vi.findViewById(R.id.delete);
            holder.favorite = (LikeButton) vi.findViewById(R.id.favorite);
            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        loadData(holder, position);

        return vi;

    }

    private void loadData(final ViewHolder holder, final int position) {
//        Random rnd = new Random();
//        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
//        holder.linearLayout.setBackgroundColor(color);
        //holder.linearLayout.setBackgroundResource(R.drawable.bubble_green);
        if (selected.get(position)) {
            holder.linearLayout.setBackgroundColor(mActivity.getResources().getColor(R.color.primary_light));
        } else {
            holder.linearLayout.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
        }
        holder.txtCategoryName.setText(messages.get(position).getMessage());
        holder.date.setText(getDate(Long.parseLong(messages.get(position).getDate())));
        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("memories", messages.get(position).getMessage());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mActivity, "Text copied to clipboard", Toast.LENGTH_LONG).show();
            }
        });
        if (messages.get(position).isFavorite()) {
            holder.favorite.setLiked(true);
        } else {
            holder.favorite.setLiked(false);
        }
        holder.favorite.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                MessagesModel.getInstance().update(mActivity, messages.get(position).getMessage(), true);
                messages.get(position).setFavorite(true);
                //((Main)mActivity).refresh();
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                MessagesModel.getInstance().update(mActivity, messages.get(position).getMessage(), false);
                messages.get(position).setFavorite(false);
                //((Main)mActivity).refresh();
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(mActivity, R.style.Theme_MyApp)).create();
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
                        MessagesModel.getInstance().delete(mActivity, messages.get(position).getMessage());
                        alertDialog.dismiss();
                        ((FavoriteMessages) mActivity).refresh();
                    }
                });
                alertDialog.show();
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
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((FavoriteMessages) mActivity).isMultiSelection()) {
                    if (selected.get(position)) {
                        selected.put(position, false);
                        if (noOfSelected() <= 0) {
                            ((FavoriteMessages) mActivity).setMultiSelection(false);
                            ((FavoriteMessages) mActivity).changeMenu(noOfSelected(), false);
                        } else {
                            ((FavoriteMessages) mActivity).changeMenu(noOfSelected(), true);
                        }
                        notifyDataSetChanged();
                    } else {
                        selected.put(position, true);
                        ((FavoriteMessages) mActivity).changeMenu(noOfSelected(), true);
                        notifyDataSetChanged();
                    }
                } else {
                    ;
                    //showDialog(position);
                }
            }
        });
        holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((FavoriteMessages) mActivity).setMultiSelection(true);
                selected.put(position, true);
                ((FavoriteMessages) mActivity).changeMenu(noOfSelected(), true);
                notifyDataSetChanged();
                return false;
            }
        });
        //holder.txtCategoryName.setBackgroundDrawable(bubblesChat);
    }

    public int noOfSelected() {
        int count = 0;
        for (int i = 0; i < selected.size(); i++) {
            if (selected.valueAt(i)) {
                count++;
            }
        }
        return count;
    }

    public void selectAll() {
        selected.clear();
        for (int i = 0; i < messages.size(); i++) {
            selected.put(i, true);
        }
        notifyDataSetChanged();
        ((FavoriteMessages) mActivity).changeMenu(noOfSelected(), true);
    }

    public void unselectAll() {
        selected.clear();
        notifyDataSetChanged();
    }

    public void deleteSelected() {
        for (int i = 0; i < selected.size(); i++) {
            if (selected.valueAt(i)) {
                MessagesModel.getInstance().delete(mActivity, messages.get(selected.keyAt(i)).getMessage());
            }
        }
    }

    void copySelected() {
        String finalString = "";
        for (int i = 0; i < selected.size(); i++) {
            if (selected.valueAt(i)) {
                finalString = finalString + messages.get(selected.keyAt(i)).getMessage() + "\n";
            }
        }
        ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("memories", finalString);
        clipboard.setPrimaryClip(clip);
        unselectAll();
        ((FavoriteMessages) mActivity).changeMenu(noOfSelected(), false);
        Toast.makeText(mActivity, "Text copied to clipboard", Toast.LENGTH_LONG).show();
    }

    public void filter(String text) {
        if (text.isEmpty()) {
            messages.clear();
            messages.addAll(messagesCopy);
        } else {
            ArrayList<Message> result = new ArrayList<>();
            text = text.toLowerCase();
            for (Message item : messagesCopy) {
                if (item.getMessage().toLowerCase().contains(text)) {
                    result.add(item);
                }
            }
            messages.clear();
            messages.addAll(result);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        public TextView txtCategoryName, date;
        public CardView linearLayout;
        public ImageButton copy, share, delete;
        public LikeButton favorite;
    }
}

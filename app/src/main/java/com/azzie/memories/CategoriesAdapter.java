package com.azzie.memories;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ajaj Patel on  .
 */
public class CategoriesAdapter extends BaseAdapter {

    public int width;
    RelativeLayout.LayoutParams params_size;
    ImageLoader imageLoader;
    private LayoutInflater inflater = null;
    private Activity mActivity;
    private ArrayList<Category> categoryArrayList;
    private Utils utils;
    HashMap<String, Integer> messageCount = new HashMap<>();
    Fragment fragment;

    public CategoriesAdapter(Activity activity, ArrayList<Category> categoryArrayList, HashMap<String, Integer> messageCount, Fragment fragment) {
        mActivity = activity;
        this.categoryArrayList = categoryArrayList;
        inflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.messageCount = messageCount;
        Utils.DeviceWidth = Utils.getScreenWidth(activity);
        width = Utils.DeviceWidth / 2;
        this.fragment = fragment;
        Log.d("width", "" + width);
        params_size = new RelativeLayout.LayoutParams(Utils.DeviceWidth, width);
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return categoryArrayList.size();
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
            vi = inflater.inflate(R.layout.layout_category_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/
            holder = new ViewHolder();
            holder.txtCategoryName = (TextView) vi.findViewById(R.id.txtCategoryName);
            holder.imgCategoryImage = (ImageView) vi.findViewById(R.id.imgCategoryImage);
            holder.favorite = (ImageButton) vi.findViewById(R.id.favorite);
            holder.count = (TextView) vi.findViewById(R.id.count);
            holder.delete = (ImageButton) vi.findViewById(R.id.delete);
            holder.edit = (ImageButton) vi.findViewById(R.id.edit);
            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        holder.imgCategoryImage.setLayoutParams(params_size);
        loadData(holder, position);

        return vi;

    }

    private void loadData(final ViewHolder holder, final int position) {
        if (categoryArrayList.get(position).isCustom()) {
            holder.delete.setVisibility(View.VISIBLE);
            holder.edit.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
            holder.edit.setVisibility(View.GONE);
        }
        holder.count.setText("" + messageCount.get(categoryArrayList.get(position).getName()));
        if (categoryArrayList.get(position).isFavorite()) {
            int color = Color.parseColor("#cf346a"); //The color u want
            holder.favorite.setColorFilter(color);
        } else {
            int color = Color.parseColor("#FFFFFF"); //The color u want
            holder.favorite.setColorFilter(color);
        }
        holder.imgCategoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mActivity, MessagesActivity.class);
                i.putExtra("categoryName", categoryArrayList.get(position).getName());
                mActivity.startActivity(i);
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
                        CategoryModel.getInstance().delete(mActivity, categoryArrayList.get(position).getName());
                        Toast.makeText(mActivity, "Category deleted successfully.", Toast.LENGTH_LONG).show();
                        Tracker t = AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
                        t.send(new HitBuilders.EventBuilder()
                                .setCategory("Category")
                                .setAction("Delete Category")
                                .setLabel("Delete")
                                .build());
                        if (fragment instanceof HomeFragment) {
                            ((HomeFragment) fragment).init();
                        }
                    }
                });
                alertDialog.show();
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActivity instanceof Main) {
                    ((Main) mActivity).showDialog(false, categoryArrayList.get(position).getName(), "");
                }
            }
        });
        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (categoryArrayList.get(position).isFavorite()) {
                    CategoryModel.getInstance().update(mActivity, categoryArrayList.get(position).getName(), false);
                    categoryArrayList.get(position).setFavorite(false);
                    notifyDataSetChanged();
                } else {
                    CategoryModel.getInstance().update(mActivity, categoryArrayList.get(position).getName(), true);
                    categoryArrayList.get(position).setFavorite(true);
                    notifyDataSetChanged();
                }
            }
        });
        holder.txtCategoryName.setText(categoryArrayList.get(position).getName());
        //Utils.showLog("AdapterCat_List", item.getCategory_name() + ":" + Constants.SERVICE_URL + Constants.IMAGE_CATEGORY_LOGO + item.getCategory_logo());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mActivity).build();
        ImageLoader.getInstance().init(config);
        if (categoryArrayList.get(position).getImage() == 0) {
            Picasso.with(mActivity).load(R.mipmap.memory).into(holder.imgCategoryImage);
            //imageLoader.displayImage("drawable://" + R.mipmap.memory, holder.imgCategoryImage);
        } else {
            Picasso.with(mActivity).load(categoryArrayList.get(position).getImage()).into(holder.imgCategoryImage);
            //imageLoader.displayImage("drawable://" + categoryArrayList.get(position).getImage(), holder.imgCategoryImage);
        }
        //holder.imgCategoryImage.setImageDrawable(mActivity.getResources().getDrawable(imageList[position]));
        Log.d("position", "" + position);
    }

    public static class ViewHolder {
        public TextView txtCategoryName, count;
        public ImageView imgCategoryImage;
        public ImageButton delete, edit;
        public ImageButton favorite;
    }

}

package com.messagitory;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ajaj Patel on  .
 */
public class CategoriesAdapter extends BaseAdapter {

    public static ImageLoader imageLoader;
    public int width;
    RelativeLayout.LayoutParams params_size;
    HashMap<String, Integer> messageCount = new HashMap<>();
    Fragment fragment;
    Bitmap bitmapImage;
    private LayoutInflater inflater = null;
    private Activity mActivity;
    private DisplayImageOptions options;
    private ArrayList<Category> categoryArrayList;
    private Utils utils;

    public CategoriesAdapter(Activity activity, ArrayList<Category> categoryArrayList, HashMap<String, Integer> messageCount, Fragment fragment) {
        mActivity = activity;
        this.categoryArrayList = categoryArrayList;
        inflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.messageCount = messageCount;
        Utils.DeviceWidth = Utils.getScreenWidth(activity);
        width = (Utils.DeviceWidth / 2)-50;
        this.fragment = fragment;
        Log.d("width", "" + width);
        params_size = new RelativeLayout.LayoutParams(Utils.DeviceWidth, width);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mActivity));

        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).showImageOnFail(R.drawable.header)
                .showImageOnLoading(R.drawable.header).build();
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
            vi = inflater.inflate(R.layout.layout_category_item, null);
            holder = new ViewHolder();
            holder.txtCategoryName = (TextView) vi.findViewById(R.id.txtCategoryName);
            holder.imgCategoryImage = (ImageView) vi.findViewById(R.id.imgCategoryImage);
            holder.favorite = (ImageButton) vi.findViewById(R.id.favorite);
            holder.count = (TextView) vi.findViewById(R.id.count);
            holder.delete = (ImageButton) vi.findViewById(R.id.delete);
            holder.edit = (ImageButton) vi.findViewById(R.id.edit);
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        holder.imgCategoryImage.setLayoutParams(params_size);
        loadData(holder, position);

        return vi;

    }

    private void loadData(final ViewHolder holder, final int position) {

        holder.count.setText("" + messageCount.get(categoryArrayList.get(position).getName()));
        if (categoryArrayList.get(position).isFavorite()) {
            int color = Color.parseColor("#2196F3"); //The color u want
            holder.favorite.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
            holder.favorite.setColorFilter(color);
        } else {
            holder.favorite.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.favorite));
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
        if (categoryArrayList.get(position).isCustom()) {
            try {
                holder.imgCategoryImage.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.imgCategoryImage.setBackgroundColor(Color.parseColor("#ffffff"));
                String fileurl = Environment.getExternalStorageDirectory() + File.separator + "MessagiTory" + File.separator + categoryArrayList.get(position).getName() + ".jpg";
                File imageFile = new File(fileurl);
                if(imageFile.exists()){
                    bitmapImage = BitmapFactory.decodeFile(fileurl);
                    int nh = (int) (bitmapImage.getHeight() * (512.0 / bitmapImage.getWidth()));
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmapImage, 512, nh, true);
                    holder.imgCategoryImage.setImageBitmap(scaled);
                }else{
                    holder.imgCategoryImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    holder.imgCategoryImage.setBackgroundColor(Color.parseColor("#2196F3"));
                    holder.imgCategoryImage.setImageResource(R.drawable.header);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            holder.imgCategoryImage.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.imgCategoryImage.setScaleType(ImageView.ScaleType.FIT_XY);
            String imageUri = "drawable://" + categoryArrayList.get(position).getImage();
            imageLoader.displayImage(imageUri, holder.imgCategoryImage,
                    options, new ImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String arg0, View arg1) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onLoadingFailed(String arg0, View arg1,
                                                    FailReason arg2) {
                            // TODO Auto-generated method stub


                        }

                        @Override
                        public void onLoadingComplete(String arg0, View arg1,
                                                      Bitmap arg2) {
                            // TODO Auto-generated method stub


                        }

                        @Override
                        public void onLoadingCancelled(String arg0, View arg1) {
                            // TODO Auto-generated method stub


                        }
                    });
        }
    }

    public static class ViewHolder {
        public TextView txtCategoryName, count;
        public ImageView imgCategoryImage;
        public ImageButton delete, edit;
        public ImageButton favorite;
    }
}

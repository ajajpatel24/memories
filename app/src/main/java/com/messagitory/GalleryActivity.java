package com.messagitory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class GalleryActivity extends AppCompatActivity {
    private static Uri[] mUrls = null;
    private static String[] strUrls = null;
    private String username;
    private String[] mNames = null;
    private GridView gridview = null;
    private Cursor cc = null;
    private ProgressDialog myProgressDialog = null;
    private TextView mTitle;
    private ProgressDialog waitDialog;
    private android.support.v7.app.AlertDialog userDialog;
    private int mAction = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gallery);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        username = getIntent().getStringExtra("name");
        mAction = getIntent().getIntExtra("no", 0);
        mTitle = (TextView) findViewById(R.id.title);
        mTitle.setText("Upload Profile Picture");
        // It have to be matched with the directory in SDCard
        cc = this.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
                null);
        if (cc != null) {
            myProgressDialog = new ProgressDialog(GalleryActivity.this);
            myProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            myProgressDialog.setMessage("Please Wait...");
            myProgressDialog.show();

//            new Thread() {
//                public void run() {
            try {
                cc.moveToFirst();
                mUrls = new Uri[cc.getCount()];
                strUrls = new String[cc.getCount()];
                mNames = new String[cc.getCount()];
                for (int i = 0; i < cc.getCount(); i++) {
                    cc.moveToPosition(i);
                    mUrls[i] = Uri.parse(cc.getString(1));
                    strUrls[i] = cc.getString(1);
                    mNames[i] = cc.getString(3);
                }

            } catch (Exception e) {
            }
            myProgressDialog.dismiss();
//                }
//            }.start();
            gridview = (GridView) findViewById(R.id.gridview);
            gridview.setAdapter(new ImageAdapter(this));

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        final int position, long id) {
                    Log.d("PATH Gallery", "" + strUrls[position]);
                    final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(GalleryActivity.this, R.style.Theme_MyApp)).create();
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setMessage("Are you sure ?");
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
                            Utils.URL = strUrls[position];
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    });
                    alertDialog.show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Utils.URL = "";
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    /**
     * This method is to scale down the image
     */
    public Bitmap decodeURI(String filePath) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        Boolean scaleByHeight = Math.abs(options.outHeight - 100) >= Math.abs(options.outWidth - 100);
        if (options.outHeight * options.outWidth * 2 >= 16384) {
            // Load, scaling to smallest power of 2 that'll get it <= desired dimensions
            double sampleSize = scaleByHeight
                    ? options.outHeight / 100
                    : options.outWidth / 100;
            options.inSampleSize =
                    (int) Math.pow(2d, Math.floor(
                            Math.log(sampleSize) / Math.log(2d)));
        }

        // Do the actual decoding
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[512];
        Bitmap output = BitmapFactory.decodeFile(filePath, options);

        return output;
    }

    /**
     * This class loads the image gallery in grid view.
     */
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return cc.getCount();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.galchild, null);

            try {

                ImageView imageView = (ImageView) v.findViewById(R.id.image);
                Bitmap bmp = decodeURI(mUrls[position].getPath());
                imageView.setImageBitmap(bmp);
//                TextView txtName = (TextView) v.findViewById(R.id.name);
//                txtName.setText(mNames[position]);
            } catch (Exception e) {

            }
            return v;
        }
    }

}
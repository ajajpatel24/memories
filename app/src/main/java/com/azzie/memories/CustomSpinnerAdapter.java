package com.azzie.memories;

/**
 * Created by BrillBrains-4 on 21-07-2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Custom Adapter for Spinner
 */
public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private LayoutInflater mInflater;
    private int mNo;
    private Context mContext;
    private ArrayList<String> mData;

    public CustomSpinnerAdapter(Context context, ArrayList<String> objects, int mNo) {
        super(context, R.layout.action_spinner, objects);
        this.mNo = mNo;
        mContext = context;
        mData = objects;

        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    // This funtion called for each row ( Called mData.size() times )
    public View getCustomView(int position, View convertView, ViewGroup parent) {

        View row = mInflater.inflate(R.layout.action_spinner, parent, false);
        TextView tvCategory;
        if (mNo == 0) {
            tvCategory = (TextView) row.findViewById(R.id.tvCategory);
        } else {
            tvCategory = (TextView) row.findViewById(R.id.tvCategory1);
        }

        tvCategory.setText(mData.get(position));

        return row;
    }
}

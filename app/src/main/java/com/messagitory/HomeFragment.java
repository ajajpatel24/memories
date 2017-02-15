package com.messagitory;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by admin on 11/29/2016.
 */

public class HomeFragment extends Fragment {
    TextView messagitory;
    CategoriesAdapter adapter;
    ListView gridView;
    ArrayList<Category> categoryArrayList;
    int images[] = {R.drawable.anniversary,
            R.drawable.bestwishes,
            R.drawable.birthday,
            R.drawable.boygirl,
            R.drawable.brokenheart,
            R.drawable.commercial,
            R.drawable.community,
            R.drawable.cricket,
            R.drawable.eid,
            R.drawable.exam,
            R.drawable.friendship,
            R.drawable.jokes,
            R.drawable.life,
            R.drawable.love,
            R.drawable.moral,
            R.drawable.movies,
            R.drawable.others,
            R.drawable.parentandchild,
            R.drawable.poetry,
            R.drawable.politics,
            R.drawable.quotes,
            R.drawable.marriage,
            R.drawable.teacherstudent,
            R.drawable.valentines};
    String categories[] = {"Anniversary SMS",
            "Best Wishes / Dua SMS",
            "Birthday SMS",
            "Boy & Girl SMS",
            "Broken Heart SMS",
            "Commercial SMS",
            "Community SMS",
            "Cricket SMS",
            "Eid SMS",
            "Exam SMS",
            "Friendship SMS",
            "Funny / Jokes SMS",
            "Life SMS",
            "Love SMS",
            "Moral SMS",
            "Movies SMS",
            "Other SMS",
            "Parent & Child SMS",
            "Poetry SMS",
            "Politics SMS",
            "Quotes SMS",
            "Marriage SMS",
            "Teacher & Student SMS",
            "Valentines Day SMS"};

    HashMap<String, Integer> messageCount = new HashMap<>();

    public HomeFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_home, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            v.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);
                }
            });
        }
        categoryArrayList = new ArrayList<>();
        SharedPreferences prefs = MyApplication.preferences;
        boolean firstTime = prefs.getBoolean("firstTime", true);
        gridView = (ListView) v.findViewById(R.id.gridViewMain);
        if (firstTime) {
            SharedPreferences.Editor editor = MyApplication.preferences.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();
            for (int i = 0; i < categories.length; i++) {
                Category categoryToInsert = new Category();
                categoryToInsert.setCustom(false);
                categoryToInsert.setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));
                categoryToInsert.setFavorite(false);
                categoryToInsert.setImage(images[i]);
                categoryToInsert.setName(categories[i]);
                CategoryModel.getInstance().insert(categoryToInsert, getActivity());
            }
        }
        init();
        return v;
    }

    void init() {
        categoryArrayList = CategoryModel.getInstance().getCategories(getActivity());
        Collections.sort(categoryArrayList, new Comparator<Category>() {
            @Override
            public int compare(Category abc1, Category abc2) {
                int b1 = abc1.isCustom() ? 1 : 0;
                int b2 = abc2.isCustom() ? 1 : 0;
                return b2 - b1;
            }
        });
        messageCount = MessagesModel.getInstance().countMessagesByCategory(getActivity());
        Log.e("MessageCount", "" + new Gson().toJson(messageCount));
        adapter = new CategoriesAdapter(getActivity(), categoryArrayList, messageCount, this);
        gridView.setAdapter(adapter);
    }
}

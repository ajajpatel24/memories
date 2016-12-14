package com.azzie.memories;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by admin on 11/29/2016.
 */

public class HomeFragment extends Fragment {
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1234;
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 5678;
    public static Button btnStartService, btnShowMsg;
    public static String dbName = "mydb";
    public static int dbVersion = 1;
    TextView memories;
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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            v.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    v.removeOnLayoutChangeListener(this);
                }
            });
        }
        categoryArrayList = new ArrayList<>();
        SharedPreferences prefs = getActivity().getSharedPreferences("Memories", MODE_PRIVATE);
        boolean firstTime = prefs.getBoolean("firstTime", true);
        gridView = (ListView) v.findViewById(R.id.gridViewMain);
        if (firstTime) {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("Memories", MODE_PRIVATE).edit();
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
        adapter = new CategoriesAdapter(getActivity(), categoryArrayList, messageCount,this);
        gridView.setAdapter(adapter);
//        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.e("clicked","clicked");
//                Intent i = new Intent(getActivity(), MessagesActivity.class);
//                i.putExtra("categoryName", categoryArrayList.get(position).getName());
//                startActivity(i);
//            }
//        });
    }
}

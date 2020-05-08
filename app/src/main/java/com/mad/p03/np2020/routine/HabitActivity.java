package com.mad.p03.np2020.routine;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mad.p03.np2020.routine.Class.CustomDialogFragment;
import com.mad.p03.np2020.routine.Class.HabitTracker;
import com.mad.p03.np2020.routine.Class.habitListAdapter;

import java.util.ArrayList;

public class HabitActivity extends AppCompatActivity {

    private static final String TAG = "HabitTracker";
    ArrayList<HabitTracker> habitList;
    private habitListAdapter adapter;
    Dialog dialog;
    ImageButton add_habit;
    private boolean isLargeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit);
        Log.v(TAG,"onCreate");

        isLargeLayout = getResources().getBoolean(R.bool.large_layout);
//        View view = getLayoutInflater().inflate(R.layout.activity_focus,null);
//        dialog = new Dialog(this,android.R.style.Theme_DeviceDefault_Dialog);
//        dialog.setContentView(view);

        add_habit = findViewById(R.id.add_habit);
        add_habit.setBackgroundColor(Color.TRANSPARENT);
        add_habit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
//                dialog.show();
                Log.v(TAG,"BTN");
            }
        });
        ListView mListView = (ListView) findViewById(R.id.listView);


        HabitTracker act1 = new HabitTracker("Drink water",20,0);
        HabitTracker act2 = new HabitTracker("Exercise",7,0);
        HabitTracker act3 = new HabitTracker("Revision",2,0);
        HabitTracker act4 = new HabitTracker("Eating snack",2,0);

        habitList = new ArrayList<>();
        habitList.add(act1);
        habitList.add(act2);
        habitList.add(act3);
        habitList.add(act4);

        adapter = new habitListAdapter(this,R.layout.habit_adapter_view, habitList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG,"yo");
            }

        });
    }

    public void showDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CustomDialogFragment newFragment = new CustomDialogFragment();

        if (isLargeLayout) {
            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "dialog");
        } else {
            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.add(android.R.id.content, newFragment)
                    .addToBackStack(null).commit();
        }
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.habit_menu, menu);
//
//        ImageButton addHabit_btn = (ImageButton) menu.findItem(R.id.menu_add_btn).getActionView();
//        addHabit_btn.setImageResource(R.drawable.menu_addhabit);
//        addHabit_btn.setBackgroundColor(Color.TRANSPARENT);
//
//        addHabit_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                habitList.add(new HabitTracker("Drink water",20,0));
////                adapter.notifyDataSetChanged();
//                dialog.show();
//            }
//
//        });
//
//        return super.onCreateOptionsMenu(menu);
//    }
}

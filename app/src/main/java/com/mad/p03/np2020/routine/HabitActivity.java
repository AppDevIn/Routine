package com.mad.p03.np2020.routine;
import androidx.appcompat.app.AppCompatActivity;
import com.mad.p03.np2020.routine.Class.HabitTracker;
import com.mad.p03.np2020.routine.Class.habitListAdapter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import java.util.ArrayList;

public class HabitActivity extends AppCompatActivity {

    private static final String TAG = "HabitTracker";
    ArrayList<HabitTracker> habitList;
    private habitListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit);
        Log.v(TAG,"onCreate");
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






}

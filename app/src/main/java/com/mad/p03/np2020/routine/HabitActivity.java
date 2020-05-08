package com.mad.p03.np2020.routine;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mad.p03.np2020.routine.Class.HabitTracker;
import com.mad.p03.np2020.routine.Class.habitListAdapter;

import java.util.ArrayList;

public class HabitActivity extends AppCompatActivity {

    private static final String TAG = "HabitTracker";
    ArrayList<HabitTracker> habitList;
    private habitListAdapter adapter;
    ImageButton add_habit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit);
        Log.v(TAG,"onCreate");

        add_habit = findViewById(R.id.add_habit);
        add_habit.setBackgroundColor(Color.TRANSPARENT);
        add_habit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rect displayRectangle = new Rect();
                Window window = HabitActivity.this.getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
                final AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this,R.style.CustomAlertDialog);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.add_habit, viewGroup, false);
                dialogView.setMinimumWidth((int)(displayRectangle.width() * 1f));
                dialogView.setMinimumHeight((int)(displayRectangle.height() * 1f));
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                final TextView menu_count = dialogView.findViewById(R.id.menu_count);
                final TextView habit_name = dialogView.findViewById(R.id.add_habit_name);
                final TextView habit_occur = dialogView.findViewById(R.id.habit_occurence);
                Button buttonClose = dialogView.findViewById(R.id.habit_close);
                buttonClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                Button buttonOk = dialogView.findViewById(R.id.create_habit);
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cnt = menu_count.getText().toString();
                        String name = habit_name.getText().toString();
                        String occur = habit_occur.getText().toString();
                        HabitTracker habit = new HabitTracker(name,Integer.parseInt(occur),Integer.parseInt(cnt));
                        habitList.add(habit);
                        adapter.notifyDataSetChanged();
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();


                ImageButton add_btn = dialogView.findViewById(R.id.menu_add_count);
                ImageButton minus_btn = dialogView.findViewById(R.id.menu_minus_count);

                add_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cnt = menu_count.getText().toString();
                        int count = Integer.parseInt(cnt);
                        count++;
                        menu_count.setText(String.valueOf(count));
                    }
                });

                minus_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cnt = menu_count.getText().toString();
                        int count = Integer.parseInt(cnt);
                        if (count > 0){
                            count--;
                        }
                       menu_count.setText(String.valueOf(count));
                    }
                });

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

}

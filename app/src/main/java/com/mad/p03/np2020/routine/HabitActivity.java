package com.mad.p03.np2020.routine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Class.Habit;

import java.util.ArrayList;

import static java.lang.String.format;

public class HabitActivity extends AppCompatActivity {

    private static final String TAG = "HabitTracker";
    Habit.HabitList habitList;
    ImageButton add_habit;
    RecyclerView mRecyclerView;
    HabitAdapter myAdapter;

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
                        int cnt = Integer.parseInt(menu_count.getText().toString());
                        String name = habit_name.getText().toString();
                        int occur = Integer.parseInt(habit_occur.getText().toString());
                        myAdapter._habitList.addItem(name, occur, cnt);
                        myAdapter.notifyDataSetChanged();
                        alertDialog.dismiss();
                    }
                });

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

                alertDialog.show();
                Log.v(TAG,"BTN");
            }
        });

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        myAdapter = new HabitAdapter(this, getList());
        mRecyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new HabitAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this);
//                builder.setTitle("Delete");
//                builder.setMessage("Are you sure you want to delete this task?");
//                builder.setCancelable(false);
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.v(TAG, format("%s deleted!",habitList.getItemAt(position).getTitle()));
//                        myAdapter._habitList.removeItemAt(position);
//                        myAdapter.notifyItemRemoved(position);
//                        myAdapter.notifyItemRangeChanged(position, habitList.size());
//                        myAdapter.notifyDataSetChanged();
//                    }
//                });

//                builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
//                    public void onClick(DialogInterface dialog, int id){
//                        Log.v(TAG,"User refuses to delete!");
//                    }
//                });
//
//                AlertDialog alert = builder.create();
//                alert.show();

            }
        });
    }

    public Habit.HabitList getList() {
        habitList = new Habit.HabitList();
        habitList.addItem("Drink water", 20, 0);
        habitList.addItem("Exercise", 7,0 );
        habitList.addItem("Revision", 2, 0);
        habitList.addItem("Eating snack", 2, 0);

        return habitList;
    }
}

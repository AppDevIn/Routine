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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Class.Habit;

public class HabitActivity extends AppCompatActivity {

    private static final String TAG = "HabitTracker";
    Habit.HabitList habitList;
    ImageButton add_habit;
    RecyclerView mRecyclerView;
    HabitAdapter myAdapter;
    private final static int [] period_buttonIDS = {R.id.daily_period, R.id.weekly_period, R.id.monthly_period, R.id.yearly_period};
    private final static String[] period_textList = {"DAILY", "WEEKLY", "MONTHLY", "YEARLY"};

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

                final AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this,R.style.CustomAlertDialog);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                final View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.add_habit, viewGroup, false);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();

                final TextView menu_count = dialogView.findViewById(R.id.menu_count);
                final TextView habit_name = dialogView.findViewById(R.id.add_habit_name);
                final TextView habit_occur = dialogView.findViewById(R.id.habit_occurence);
                final TextView period_text = dialogView.findViewById(R.id.period_txt);

                for (final int i :period_buttonIDS){
                    final Button btn = dialogView.findViewById(i);
                    btn.setBackgroundColor(Color.TRANSPARENT);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = btn.getId();

                            for (int i = 0; i < 4; i++){
                                Button _btn = dialogView.findViewById(period_buttonIDS[i]);
                                if (id == period_buttonIDS[i]){
                                    _btn.setBackgroundColor(Color.parseColor("#dfdfdf"));
                                    period_text.setText(period_textList[i]);
                                }else {
                                    _btn.setBackgroundColor(Color.TRANSPARENT);
                                }

                            }
                        }
                    });
                }

                dialogView.findViewById(R.id.daily_period).setBackgroundColor(Color.parseColor("#dfdfdf"));

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
                final Habit habit = myAdapter._habitList.getItemAt(position);
                final String _habitTitle = habit.getTitle().toUpperCase();
                final int _habitCount = habit.getCount();


                final AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this,R.style.CustomAlertDialog);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.habit_view, viewGroup, false);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();

                final TextView title = dialogView.findViewById(R.id.habit_view_title);
                final TextView cnt = dialogView.findViewById(R.id.habit_view_count);
                final ImageButton reduceBtn = dialogView.findViewById(R.id.habit_view_reduce);
                final ImageButton addBtn = dialogView.findViewById(R.id.habit_view_add);
                final ImageButton modifyBtn = dialogView.findViewById(R.id.habit_view_modify);
                final ImageButton closeBtn = dialogView.findViewById(R.id.habit_view_close);
                final ImageButton editBtn = dialogView.findViewById(R.id.habit_view_edit);

                title.setText(_habitTitle);
                cnt.setText(String.valueOf(_habitCount));
                reduceBtn.setBackgroundColor(Color.TRANSPARENT);
                addBtn.setBackgroundColor(Color.TRANSPARENT);
                modifyBtn.setBackgroundColor(Color.TRANSPARENT);
                closeBtn.setBackgroundColor(Color.TRANSPARENT);
                editBtn.setBackgroundColor(Color.TRANSPARENT);

                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int _cnt = Integer.parseInt(cnt.getText().toString());
                        habit.modifyCount(_cnt);
                        myAdapter.notifyDataSetChanged();
                        alertDialog.dismiss();
                    }
                });


                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       habit.addCount();
                       myAdapter.notifyDataSetChanged();
                       cnt.setText(String.valueOf(habit.getCount()));
                    }
                });

                reduceBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        habit.minusCount();
                        myAdapter.notifyDataSetChanged();
                        cnt.setText(String.valueOf(habit.getCount()));
                    }
                });

                modifyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this);
                        ViewGroup viewGroup = findViewById(android.R.id.content);
                        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_view_modifycnt_dialog, viewGroup, false);
                        builder.setView(dialogView);
                        final AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        final TextView dialog_title = dialogView.findViewById(R.id.habit_view_dialog_title);
                        final Button cancelBtn = dialogView.findViewById(R.id.cancel_dialog);
                        final Button saveBtn = dialogView.findViewById(R.id.save_dialog);
                        final EditText dialog_cnt = dialogView.findViewById(R.id.dialog_cnt);

                        dialog_title.setText(_habitTitle);
                        dialog_cnt.setHint(cnt.getText().toString());

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });

                        saveBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int dialogCnt = Integer.parseInt(dialog_cnt.getText().toString());
                                habit.modifyCount(dialogCnt);
                                myAdapter.notifyDataSetChanged();
                                cnt.setText(String.valueOf(dialogCnt));
                                alertDialog.dismiss();
                            }
                        });
                    }
                });

                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this,R.style.CustomAlertDialog);
                        ViewGroup viewGroup = findViewById(android.R.id.content);
                        final View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_edit, viewGroup, false);
                        builder.setView(dialogView);
                        final AlertDialog alertDialog = builder.create();

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
                                habit.modifyCount(5);
                                myAdapter.notifyDataSetChanged();
                                Log.d(TAG, String.valueOf(habit.getCount()));
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.show();
                    }
                });


                alertDialog.show();

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

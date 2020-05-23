package com.mad.p03.np2020.routine;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Class.AlarmReceiver;
import com.mad.p03.np2020.routine.Class.Habit;
import com.mad.p03.np2020.routine.Class.HabitReminder;

import java.util.Calendar;
import java.util.Date;

import static java.lang.String.format;

@RequiresApi(api = Build.VERSION_CODES.N)
public class HabitActivity extends AppCompatActivity {

    private static final String TAG = "HabitTracker";
    private String channelId = "001";
    Habit.HabitList habitList;
    ImageButton add_habit;
    ImageButton habit_chart;
    ImageButton habit_dashboard;
    RecyclerView mRecyclerView;
    HabitAdapter myAdapter;
    private final static int [] period_buttonIDS = {R.id.daily_period, R.id.weekly_period, R.id.monthly_period, R.id.yearly_period};
    private final static String[] period_textList = {"DAY", "WEEK", "MONTH", "YEAR"};
    private final static int[] period_countList = {1, 7, 30, 365};
    private final static int[] color_buttonIDS = {R.id.lightcoral_btn, R.id.slightdesblue_btn, R.id.fadepurple_btn, R.id.cyangreen_btn};
    private final static int[] color_schemeIDS = {R.color.colorLightCoral, R.color.colorSlightDesBlue, R.color.colorFadePurple, R.color.colorCyanGreen};
    private final static String[] colorList = {"lightcoral", "slightdesblue", "fadepurple", "cyangreen"};
    public static final String SHARED_PREFS = "sharedPrefs";
    int minutes;
    int hours;


    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit);
        Log.v(TAG,"onCreate");

//        initData();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "HabitTracker";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
        }

        add_habit = findViewById(R.id.add_habit);
        habit_chart = findViewById(R.id.habit_chart);
        habit_dashboard = findViewById(R.id.habit_dashboard);

        add_habit.setBackgroundColor(Color.TRANSPARENT);
        habit_chart.setBackgroundColor(Color.TRANSPARENT);
        habit_dashboard.setBackgroundColor(Color.TRANSPARENT);

        add_habit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] custom_text = {""};
                final boolean[] reminder_flag = {false};
                final AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this,R.style.CustomAlertDialog);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                final View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.add_habit, viewGroup, false);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();

                final TextView menu_count = dialogView.findViewById(R.id.menu_count);
                final TextView habit_name = dialogView.findViewById(R.id.add_habit_name);
                final TextView habit_occur = dialogView.findViewById(R.id.habit_occurence);
                final TextView period_text = dialogView.findViewById(R.id.period_txt);
                final TextView habit_reminder_indicate_text = dialogView.findViewById(R.id.reminder_indicate_text);

                final int[] period = new int[1];

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
                                    period[0] = period_countList[i];
                                }else {
                                    _btn.setBackgroundColor(Color.TRANSPARENT);
                                }

                            }
                        }
                    });
                }
                period[0] = 1;
                dialogView.findViewById(R.id.daily_period).setBackgroundColor(Color.parseColor("#dfdfdf"));


                final String[] color = new String[1];
                final Button lightcoral_btn = dialogView.findViewById(R.id.lightcoral_btn);
                final Button slightdesblue_btn = dialogView.findViewById(R.id.slightdesblue_btn);
                final Button fadepurple_btn = dialogView.findViewById(R.id.fadepurple_btn);
                final Button cyangreen_btn = dialogView.findViewById(R.id.cyangreen_btn);

                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setStroke(5, Color.BLACK);
                drawable.setColor(getResources().getColor(R.color.colorLightCoral));
                lightcoral_btn.setBackground(drawable);
                color[0] = "lightcoral";

                for (final int i :color_buttonIDS){
                    final Button btn = dialogView.findViewById(i);

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = btn.getId();

                            for (int i = 0; i < 4; i++){
                                Button _btn = dialogView.findViewById(color_buttonIDS[i]);
                                if (id == color_buttonIDS[i]){
                                    GradientDrawable drawable = new GradientDrawable();
                                    drawable.setShape(GradientDrawable.RECTANGLE);
                                    drawable.setStroke(5, Color.BLACK);
                                    drawable.setColor(getResources().getColor(color_schemeIDS[i]));
                                    _btn.setBackground(drawable);
                                    color[0] = colorList[i];
                                }else {
                                    _btn.setBackgroundResource(color_schemeIDS[i]);
                                }
                            }
                        }
                    });
                }


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
                        String name = habit_name.getText().toString();
                        if (name.equalsIgnoreCase("")){
                            habit_name.setError("Please enter habit name");
                            return;
                        }

                        int occur = Integer.parseInt(habit_occur.getText().toString());
                        int cnt = Integer.parseInt(menu_count.getText().toString());
                        Date date = new Date();
                        HabitReminder hr = null;
                        if (reminder_flag[0]){
                            int id = getData();
                            String txt = custom_text[0];
                            setReminder(name,minutes,hours,id,txt);
                            hr = new HabitReminder(name,id,minutes,hours,txt);
                        }
                        myAdapter._habitList.addItem(name, occur, cnt, period[0], dateFormat.format(date),color[0],hr);
                        myAdapter.notifyDataSetChanged();
                        Toast.makeText(HabitActivity.this, format("Habit %shas been created.",capitalise(name)), Toast.LENGTH_SHORT).show();
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

                habit_reminder_indicate_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this,R.style.CustomAlertDialog);
                        ViewGroup viewGroup = findViewById(android.R.id.content);
                        final View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_reminder_view, viewGroup, false);
                        builder.setView(dialogView);
                        final AlertDialog alertDialog = builder.create();

                        final ImageView close_btn = dialogView.findViewById(R.id.habit_reminder_view_close);
                        final Switch reminder_switch = dialogView.findViewById(R.id.habit_reminder_view_switch);
                        final TextView reminder_displayTime = dialogView.findViewById(R.id.habit_reminder_view_displaytime);
                        final TimePicker timePicker = dialogView.findViewById(R.id.habit_reminder_view_timepicker);
                        final TextView _custom_text = dialogView.findViewById(R.id.habit_reminder_view_customtext);
                        final ImageView save_btn = dialogView.findViewById(R.id.habit_reminder_view_save);

                        if (reminder_flag[0]){
                            Calendar c = Calendar.getInstance();
                            c.set(Calendar.HOUR_OF_DAY,hours);
                            c.set(Calendar.MINUTE, minutes);
                            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
                            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
                            reminder_switch.setChecked(true);
                            reminder_displayTime.setText(format("%d:%d",hours,minutes));
                        }else{
                            if (Build.VERSION.SDK_INT <= 23) {
                                minutes = timePicker.getCurrentMinute(); // before api level 23
                                hours = timePicker.getCurrentHour(); // before api level 23
                            }else{
                                minutes = timePicker.getMinute(); // after api level 23
                                hours  = timePicker.getHour(); // after api level 23
                            }
                            reminder_switch.setChecked(false);
                            reminder_displayTime.setText(format("%d:%d",hours,minutes));
                        }


                        if (custom_text[0] != ""){
                            _custom_text.setText(custom_text[0]);
                        }

                        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                            @Override
                            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                                if (Build.VERSION.SDK_INT <= 23) {
                                    minutes = timePicker.getCurrentMinute(); // before api level 23
                                    hours = timePicker.getCurrentHour(); // before api level 23
                                }else{
                                    minutes = timePicker.getMinute(); // after api level 23
                                    hours  = timePicker.getHour(); // after api level 23
                                }
                                reminder_displayTime.setText(format("%d:%d",hours,minutes));
                            }
                        });

                        save_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (reminder_switch.isChecked()){
                                    habit_reminder_indicate_text.setText((format("%d:%d",hours,minutes)));
                                    if (_custom_text.getText().toString() != ""){
                                        custom_text[0] = _custom_text.getText().toString();
                                    }
                                    reminder_flag[0] = true;
//                                    Toast.makeText(getApplicationContext(),"The reminder settings has been saved.", Toast.LENGTH_SHORT).show();
                                }else{
                                    reminder_flag[0] = false;
                                    habit_reminder_indicate_text.setText("NONE");
                                    custom_text[0] = "";
                                }

                                alertDialog.dismiss();
                            }
                        });

                        close_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.show();
                    }
                });


                        
                alertDialog.show();

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

                Log.d(TAG, format(habit.getTitle() + " "+ habit.getCount() + "/" + (habit.getOccurrence()) + " " + habit.getPeriod()));
                Log.d(TAG, habit.getTime_created());
                Log.d(TAG, habit.getHolder_color());

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
                final ImageButton deletebtn = dialogView.findViewById(R.id.habit_view_delete);
                final TextView occurrence = dialogView.findViewById(R.id.habitOccurence);
                final TextView cnt2 = dialogView.findViewById(R.id.habitCount);
                final TextView period = dialogView.findViewById(R.id.habit_period);
                final LinearLayout habit_view_upper = dialogView.findViewById(R.id.habit_view_upper);

                title.setText(habit.getTitle());
                cnt.setText(String.valueOf(habit.getCount()));
                occurrence.setText(String.valueOf(habit.getOccurrence()));
                cnt2.setText(String.valueOf(habit.getCount()));
                period.setText(habit.returnPeriodText(habit.getPeriod()));

                reduceBtn.setBackgroundColor(Color.TRANSPARENT);
                addBtn.setBackgroundColor(Color.TRANSPARENT);
                modifyBtn.setBackgroundColor(Color.TRANSPARENT);
                closeBtn.setBackgroundColor(Color.TRANSPARENT);
                editBtn.setBackgroundColor(Color.TRANSPARENT);
                deletebtn.setBackgroundColor(Color.TRANSPARENT);

                habit_view_upper.setBackgroundColor(getResources().getColor(habit.returnColorID(habit.getHolder_color())));

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
                        cnt2.setText(String.valueOf(habit.getCount()));
                    }
                });

                reduceBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        habit.minusCount();
                        myAdapter.notifyDataSetChanged();
                        cnt.setText(String.valueOf(habit.getCount()));
                        cnt2.setText(String.valueOf(habit.getCount()));
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

                        dialog_title.setText(habit.getTitle());
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
                                cnt.setText(String.valueOf(habit.getCount()));
                                cnt2.setText(String.valueOf(habit.getCount()));
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

                        final boolean[] reminder_flag = {false};
                        final String[] txt = {""};
                        final boolean[] modified_reminder = {false};

                        final TextView habit_name = dialogView.findViewById(R.id.add_habit_name);
                        final TextView habit_occur = dialogView.findViewById(R.id.habit_occurence);
                        final TextView period_text = dialogView.findViewById(R.id.period_txt);
                        final TextView habit_reminder_indicate_text = dialogView.findViewById(R.id.reminder_indicate_text);

                        final HabitReminder habitReminder = habit.getHabitReminder();
                        if (habitReminder != null){
                            habit_reminder_indicate_text.setText((format("%d:%d",habitReminder.getHours(),habitReminder.getMinutes())));
                            reminder_flag[0] = true;
                        }

                        habit_reminder_indicate_text.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this,R.style.CustomAlertDialog);
                                ViewGroup viewGroup = findViewById(android.R.id.content);
                                final View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_reminder_view, viewGroup, false);
                                builder.setView(dialogView);
                                final AlertDialog alertDialog = builder.create();

                                final ImageView close_btn = dialogView.findViewById(R.id.habit_reminder_view_close);
                                final Switch reminder_switch = dialogView.findViewById(R.id.habit_reminder_view_switch);
                                final TextView reminder_displayTime = dialogView.findViewById(R.id.habit_reminder_view_displaytime);
                                final TimePicker timePicker = dialogView.findViewById(R.id.habit_reminder_view_timepicker);
                                final TextView _custom_text = dialogView.findViewById(R.id.habit_reminder_view_customtext);
                                final ImageView save_btn = dialogView.findViewById(R.id.habit_reminder_view_save);


                                if (habitReminder != null){
                                    _custom_text.setText(habitReminder.getCustom_text());
                                    reminder_switch.setChecked(true);
                                    reminder_displayTime.setText(habit_reminder_indicate_text.getText().toString());
                                    Calendar c = Calendar.getInstance();
                                    c.set(Calendar.HOUR_OF_DAY,habitReminder.getHours());
                                    c.set(Calendar.MINUTE, habitReminder.getMinutes());
                                    timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
                                    timePicker.setCurrentMinute(c.get(Calendar.MINUTE));

                                }else if (modified_reminder[0] == true && reminder_flag[0]){
                                    reminder_switch.setChecked(true);
                                    _custom_text.setText(txt[0]);
                                    reminder_displayTime.setText(format("%d:%d",hours,minutes));
                                    Calendar c = Calendar.getInstance();
                                    c.set(Calendar.HOUR_OF_DAY,hours);
                                    c.set(Calendar.MINUTE, minutes);
                                    timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
                                    timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
                                }else{
                                    if (Build.VERSION.SDK_INT <= 23) {
                                        minutes = timePicker.getCurrentMinute(); // before api level 23
                                        hours = timePicker.getCurrentHour(); // before api level 23
                                    }else{
                                        minutes = timePicker.getMinute(); // after api level 23
                                        hours  = timePicker.getHour(); // after api level 23
                                    }
                                    reminder_switch.setChecked(false);
                                    reminder_displayTime.setText(format("%d:%d",hours,minutes));
                                }


                                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                                    @Override
                                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                                        if (Build.VERSION.SDK_INT <= 23) {
                                            minutes = timePicker.getCurrentMinute(); // before api level 23
                                            hours = timePicker.getCurrentHour(); // before api level 23
                                        }else{
                                            minutes = timePicker.getMinute(); // after api level 23
                                            hours  = timePicker.getHour(); // after api level 23
                                        }
                                        reminder_displayTime.setText(format("%d:%d",hours,minutes));
                                    }
                                });

                                save_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (reminder_switch.isChecked()){
                                            habit_reminder_indicate_text.setText((format("%d:%d",hours,minutes)));
                                            txt[0] = _custom_text.getText().toString();
                                            reminder_flag[0] = true;
                                            modified_reminder[0] = true;
                                        }else{
                                            habit_reminder_indicate_text.setText("NONE");
                                            reminder_flag[0] = false;
                                            txt[0] = "";
                                        }

                                        alertDialog.dismiss();
                                    }
                                });

                                close_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });

                                alertDialog.show();
                            }
                        });

                        final int[] _period = new int[1];

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
                                            _btn.setBackgroundColor(getResources().getColor(R.color.colorWhiteGrey));
                                            period_text.setText(period_textList[i]);
                                            _period[0] = period_countList[i];
                                        }else {
                                            _btn.setBackgroundColor(Color.TRANSPARENT);
                                        }

                                    }
                                }
                            });
                        }

                        for(int i = 0; i < 4; i++){
                            if (period_countList[i] == habit.getPeriod()){
                                _period[0] = period_countList[i];
                                period_text.setText(period_textList[i]);
                                dialogView.findViewById(period_buttonIDS[i]).setBackgroundColor(getResources().getColor(R.color.colorWhiteGrey));
                                break;
                            }
                        }


                        final String[] _color = new String[1];
                        final Button lightcoral_btn = dialogView.findViewById(R.id.lightcoral_btn);
                        final Button slightdesblue_btn = dialogView.findViewById(R.id.slightdesblue_btn);
                        final Button fadepurple_btn = dialogView.findViewById(R.id.fadepurple_btn);
                        final Button cyangreen_btn = dialogView.findViewById(R.id.cyangreen_btn);

                        for(int i = 0; i < 4; i++){
                            if (colorList[i].equals(habit.getHolder_color())){
                                _color[0] = colorList[i];
                                GradientDrawable drawable = new GradientDrawable();
                                drawable.setShape(GradientDrawable.RECTANGLE);
                                drawable.setStroke(5, Color.BLACK);
                                drawable.setColor(getResources().getColor(color_schemeIDS[i]));
                                dialogView.findViewById(color_buttonIDS[i]).setBackground(drawable);
                                break;
                            }
                        }


                        for (final int i :color_buttonIDS){
                            final Button btn = dialogView.findViewById(i);

                            btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int id = btn.getId();

                                    for (int i = 0; i < 4; i++){
                                        Button _btn = dialogView.findViewById(color_buttonIDS[i]);
                                        if (id == color_buttonIDS[i]){
                                            GradientDrawable drawable = new GradientDrawable();
                                            drawable.setShape(GradientDrawable.RECTANGLE);
                                            drawable.setStroke(5, Color.BLACK);
                                            drawable.setColor(getResources().getColor(color_schemeIDS[i]));
                                            _btn.setBackground(drawable);
                                            _color[0] = colorList[i];
                                        }else
                                            _btn.setBackgroundResource(color_schemeIDS[i]);
                                        }
                                    }
                                });
                            }


                        habit_name.setText(habit.getTitle());
                        habit_occur.setText(String.valueOf(habit.getOccurrence()));

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
                                habit.modifyTitle(habit_name.getText().toString());
                                habit.setOccurrence(Integer.parseInt(habit_occur.getText().toString()));
                                habit.setPeriod(_period[0]);
                                habit.setHolder_color(_color[0]);
                                if (reminder_flag[0]){
                                    if (habit.getHabitReminder() == null){
                                        int id = getData();
                                        String _txt = txt[0];
                                        habit.setHabitReminder(new HabitReminder(habit.getTitle(),id,minutes,hours,_txt));
                                        setReminder(habit.getTitle(),minutes,hours,id,txt[0]);
                                    }else{
                                        int id = habit.getHabitReminder().getId();
                                        // reuse the id assigned before to fire the alarm
                                        String _txt = txt[0];
                                        habit.setHabitReminder(new HabitReminder(habit.getTitle(),id,minutes,hours,_txt));
                                        setReminder(habit.getTitle(),minutes,hours,id,txt[0]);
                                    }
                                }else{
                                    if (habit.getHabitReminder() != null){
                                        cancelReminder(habit.getTitle(),habit.getHabitReminder().getId(),habit.getHabitReminder().getCustom_text());
                                    }
                                    habit.setHabitReminder(null);
                                    habit_reminder_indicate_text.setText("NONE");

                                }


                                title.setText(habit_name.getText().toString());
                                occurrence.setText(String.valueOf(habit.getOccurrence()));
                                period.setText(habit.returnPeriodText(habit.getPeriod()));
                                habit_view_upper.setBackgroundResource(habit.returnColorID(habit.getHolder_color()));

                                myAdapter.notifyDataSetChanged();
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.show();
                    }
                });

                deletebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this);
                        builder.setTitle("Delete");
                        builder.setMessage("Are you sure you want to delete this habit?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.v(TAG, format("%s deleted!",habitList.getItemAt(position).getTitle()));
                                myAdapter._habitList.removeItemAt(position);
                                myAdapter.notifyItemRemoved(position);
                                myAdapter.notifyItemRangeChanged(position, habitList.size());
                                myAdapter.notifyDataSetChanged();
                                alertDialog.dismiss();
                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Log.v(TAG,"User refuses to delete!");
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });


                alertDialog.show();

            }
        });
    }

    public Habit.HabitList getList() {
        habitList = new Habit.HabitList();
        Date date = new Date();
        habitList.addItem("Drink water", 20, 0,1, dateFormat.format(date),"lightcoral",null);
        habitList.addItem("Exercise", 7,0 ,7,dateFormat.format(date),"cyangreen",null);
        habitList.addItem("Revision", 2, 0,365,dateFormat.format(date),"fadepurple",null);
        habitList.addItem("Eating snack", 2, 0,30, dateFormat.format(date),"slightdesblue",null);
        return habitList;
    }

    public void setReminder(String name, int minutes, int hours,int id, String custom_txt){

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.setAction("HabitTracker");
        intent.putExtra("Name", name);
        intent.putExtra("id",id);
        intent.putExtra("custom_txt",custom_txt);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int type = AlarmManager.RTC_WAKEUP;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MINUTE,minutes);
        c.set(Calendar.HOUR_OF_DAY,hours);
        c.set(Calendar.SECOND,0);
        Log.d(TAG, String.valueOf(c.getTime()));
        long time = c.getTime().getTime();

        Log.d(TAG, "setReminder: "+ id);
        am.setRepeating(type,time,AlarmManager.INTERVAL_DAY,pi);
    }

    public void cancelReminder(String name,int id, String custom_txt){
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.setAction("HabitTracker");
        intent.putExtra("Name", name);
        intent.putExtra("id",id);
        intent.putExtra("custom_txt",custom_txt);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Log.d(TAG, "cancelReminder: "+ id);
        am.cancel(pi);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    public String capitalise(String text){
        String txt = "";
        String[] splited = text.split("\\s+");
        for (String s: splited){
            txt += s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase() + " ";
        }
        return txt;
    }


    public void initData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("alarm_id",0);
        editor.apply();

    }

    public int getData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = sharedPreferences.getInt("alarm_id",-1);
        editor.putInt("alarm_id",++id);
        editor.apply();
        return id;

    }

}

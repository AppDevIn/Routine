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
import android.view.WindowManager;
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
import com.mad.p03.np2020.routine.Class.HabitGroup;
import com.mad.p03.np2020.routine.Class.HabitGroupAdapter;
import com.mad.p03.np2020.routine.Class.HabitReminder;
import com.mad.p03.np2020.routine.database.HabitDBHelper;
import com.mad.p03.np2020.routine.database.HabitGroupDBHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.lang.String.format;

@RequiresApi(api = Build.VERSION_CODES.N)
public class HabitActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HabitTracker";
    private String channelId = "001";
    Habit.HabitList habitList;
    ArrayList<HabitGroup> habitGroup_reference;

    RecyclerView mRecyclerView;
    HabitAdapter myAdapter;
    RecyclerView groupRecyclerView;
    HabitGroupAdapter groupAdapter;

    private final static int [] period_buttonIDS = {R.id.daily_period, R.id.weekly_period, R.id.monthly_period, R.id.yearly_period};
    private final static String[] period_textList = {"DAY", "WEEK", "MONTH", "YEAR"};
    private final static int[] period_countList = {1, 7, 30, 365};
    private final static int[] color_buttonIDS = {R.id.lightcoral_btn, R.id.slightdesblue_btn, R.id.fadepurple_btn, R.id.cyangreen_btn};
    private final static int[] color_schemeIDS = {R.color.colorLightCoral, R.color.colorSlightDesBlue, R.color.colorFadePurple, R.color.colorCyanGreen};
    private final static String[] colorList = {"lightcoral", "slightdesblue", "fadepurple", "cyangreen"};
    public static final String SHARED_PREFS = "sharedPrefs";
    int minutes;
    int hours;

    HabitDBHelper habit_dbHandler;
    HabitGroupDBHelper group_dbhandler;
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    // add habit
//    private TextView menu_count,habit_name, habit_occur, period_text, habit_reminder_indicate_text, group_indicate_text ;
//    private ImageView add_btn, minus_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit);
        // set the layout in full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        habit_dbHandler = new HabitDBHelper(this); // initialise the HabitDBHelper
        group_dbhandler = new HabitGroupDBHelper(this); // initialise the HabitGroupDBHelper

        habitGroup_reference = group_dbhandler.getAllGroups();
        Log.v(TAG,"onCreate");

        initData(); // initialise the shared preferences if it is not done so

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // if api > 28, create a notification channel named "HabitTracker"
            String channelName = "HabitTracker";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);
        }


        ImageView add_habit = findViewById(R.id.add_habit);
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
                final TextView habit_reminder_indicate_text = dialogView.findViewById(R.id.reminder_indicate_text);
                final TextView group_indicate_text = dialogView.findViewById(R.id.group_indicate_text);
                final ImageButton add_btn = dialogView.findViewById(R.id.menu_add_count);
                final ImageButton minus_btn = dialogView.findViewById(R.id.menu_minus_count);

                final int[] period = new int[1];
                populatePeriodBtn(dialogView, period, period_text);
                habit_add_initialise_periodSection(dialogView,period);

                final String[] color = new String[1];
                populateColorBtn(dialogView,color);
                habit_add_initialise_colorSection(dialogView,color);


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

                final String[] _grp_name = {null};
                group_indicate_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this,R.style.CustomAlertDialog);
                        LayoutInflater inflater = getLayoutInflater();
                        View convertView = inflater.inflate(R.layout.habit_group, null);

                        ImageView close = convertView.findViewById(R.id.habit_group_view_close);
                        Button cancel = convertView.findViewById(R.id.habit_group_view_cancel);
                        Button create_grp = convertView.findViewById(R.id.habit_group_view_create_group);
                        final TextView curr_grp = convertView.findViewById(R.id.current_grp);


                        if (_grp_name[0] != null){
                            curr_grp.setText(_grp_name[0]);
                        }else{
                            curr_grp.setText("None");
                        }


                        groupRecyclerView = convertView.findViewById(R.id.habit_recycler_view);
                        groupRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        groupAdapter= new HabitGroupAdapter(habitGroup_reference,getApplicationContext());
                        groupRecyclerView.setAdapter(groupAdapter);
                        builder.setView(convertView);
                        final AlertDialog alertDialog = builder.create();
                        groupAdapter.setOnItemClickListener(new HabitGroupAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                HabitGroup grp = groupAdapter._habitGroupList.get(position);
                                group_indicate_text.setText(grp.getGrp_name());
                                _grp_name[0] = grp.getGrp_name();
                                alertDialog.dismiss();
                            }
                        });

                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                _grp_name[0] = null;
                                group_indicate_text.setText("NONE");
                                alertDialog.dismiss();
                            }
                        });


                        create_grp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this);
                                ViewGroup viewGroup = findViewById(android.R.id.content);
                                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_group_create, viewGroup, false);
                                builder.setView(dialogView);
                                final AlertDialog alertDialog = builder.create();

                                final Button cancelBtn = dialogView.findViewById(R.id.group_cancel);
                                final Button saveBtn = dialogView.findViewById(R.id.group_save);
                                final EditText name = dialogView.findViewById(R.id.creating_group_name);

                                cancelBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });

                                saveBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String grp_name = name.getText().toString();
                                        HabitGroup grp = new HabitGroup(grp_name);
                                        long grp_id = group_dbhandler.insertGroup(grp);

                                        if (grp_id != -1){
                                            grp.setGrp_id(grp_id);
                                            groupAdapter._habitGroupList.add(grp);
                                            groupAdapter.notifyDataSetChanged();
                                            Toast.makeText(HabitActivity.this, "New group has been created.", Toast.LENGTH_SHORT).show();
                                        }

                                        alertDialog.dismiss();
                                    }
                                });

                                alertDialog.show();
                            }
                        });


                        alertDialog.show();
                    }
                });

                final int[] chosen_hours = new int[1];
                final int[] chosen_minutes = new int[1];
                final String[] custom_text = {""};
                final boolean[] reminder_flag = {false};
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
                            c.set(Calendar.HOUR_OF_DAY, chosen_hours[0]);
                            c.set(Calendar.MINUTE, chosen_minutes[0]);
                            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
                            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
                            reminder_switch.setChecked(true);
                            reminder_displayTime.setText(format("%d:%d",chosen_hours[0],chosen_minutes[0]));
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
                                    chosen_hours[0] = hours;
                                    chosen_minutes[0] = minutes;
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
                            int id = getUniqueHabitReminderID();
                            String txt = custom_text[0];
                            setReminder(name, chosen_minutes[0], chosen_hours[0], id, txt);
                            hr = new HabitReminder(name, id, chosen_minutes[0], chosen_hours[0], txt);
                        }

                        HabitGroup hg = null;
                        if (_grp_name[0] != null){
                            hg = new HabitGroup(_grp_name[0]);
                        }


                        Habit habit = new Habit(name, occur, cnt, period[0], dateFormat.format(date),color[0],hr,hg);
                        long habitID = habit_dbHandler.insertHabit(habit);
                        if (habitID != -1){ //if habitID returned is legit
                            habit.setHabitID(habitID); // attach the id to the habit
                            myAdapter._habitList.addItem(habit);
                            myAdapter.notifyDataSetChanged();

                            Log.d(TAG, "onClick: "+habit.getHabitID());
                            Toast.makeText(HabitActivity.this, format("Habit %shas been created.",capitalise(name)), Toast.LENGTH_SHORT).show();
                        }

                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();

            }
        });

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        myAdapter = new HabitAdapter(this, habit_dbHandler.getAllHabits());
        mRecyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(new HabitAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                final Habit habit = myAdapter._habitList.getItemAt(position);

                Log.d(TAG, "onItemClick: "+ position + " " + habit.getTitle());

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
                        alertDialog.dismiss();
                    }
                });


                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Habit: Add Count");
                        habit.addCount();
                        myAdapter.notifyDataSetChanged();
                        habit_dbHandler.updateCount(habit);
                        cnt.setText(String.valueOf(habit.getCount()));
                        cnt2.setText(String.valueOf(habit.getCount()));
                    }
                });

                reduceBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Habit: Minus Count");
                        habit.minusCount();
                        myAdapter.notifyDataSetChanged();
                        habit_dbHandler.updateCount(habit);
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
                                Log.d(TAG, "Habit: Modify Count");
                                int dialogCnt = Integer.parseInt(dialog_cnt.getText().toString());
                                habit.modifyCount(dialogCnt);
                                myAdapter.notifyDataSetChanged();
                                habit_dbHandler.updateCount(habit);
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


                        final TextView habit_name = dialogView.findViewById(R.id.add_habit_name);
                        final TextView habit_occur = dialogView.findViewById(R.id.habit_occurence);
                        final TextView period_text = dialogView.findViewById(R.id.period_txt);
                        final TextView habit_reminder_indicate_text = dialogView.findViewById(R.id.reminder_indicate_text);
                        final TextView group_indicate_text = dialogView.findViewById(R.id.group_indicate_text);


                        final int[] _period = new int[1];
                        populatePeriodBtn(dialogView, _period, period_text);
                        habit_edit_initialise_periodSection(dialogView, habit, _period, period_text);


                        final String[] _color = new String[1];
                        populateColorBtn(dialogView, _color);
                        habit_edit_initialise_colorSection(dialogView, habit, _color);

                        habit_name.setText(habit.getTitle());
                        habit_occur.setText(String.valueOf(habit.getOccurrence()));

                        // Modified group part
                        final HabitGroup habitGroup = habit.getGroup();

                        if (habitGroup != null ){
                            group_indicate_text.setText(habitGroup.getGrp_name());
                        }else{
                            group_indicate_text.setText("NONE");
                        }

                        final String[] _grp_name = {null};
                        final boolean[] modified_grp = {false};
                        final boolean[] _cancel = {false};
                        group_indicate_text.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this,R.style.CustomAlertDialog);
                                LayoutInflater inflater = getLayoutInflater();
                                View convertView = inflater.inflate(R.layout.habit_group, null);

                                ImageView close = convertView.findViewById(R.id.habit_group_view_close);
                                final Button cancel = convertView.findViewById(R.id.habit_group_view_cancel);
                                Button create_grp = convertView.findViewById(R.id.habit_group_view_create_group);
                                TextView curr_grp = convertView.findViewById(R.id.current_grp);

                                final HabitGroup habitGroup = habit.getGroup();
                                if (habitGroup != null && !modified_grp[0]){
                                   curr_grp.setText(habitGroup.getGrp_name());
                                }else if (modified_grp[0] && _grp_name[0] != null){
                                    curr_grp.setText(_grp_name[0]);
                                }else{
                                    curr_grp.setText("None");
                                }



                                groupRecyclerView = convertView.findViewById(R.id.habit_recycler_view);
                                groupRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                groupAdapter= new HabitGroupAdapter(habitGroup_reference, getApplicationContext());
                                groupRecyclerView.setAdapter(groupAdapter);
                                builder.setView(convertView);
                                final AlertDialog alertDialog = builder.create();
                                groupAdapter.setOnItemClickListener(new HabitGroupAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        HabitGroup grp = groupAdapter._habitGroupList.get(position);
                                        group_indicate_text.setText(grp.getGrp_name());
                                        _grp_name[0] = grp.getGrp_name();
                                        modified_grp[0] = true;
                                        _cancel[0] = false;
                                        alertDialog.dismiss();
                                    }
                                });

                                close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                    }
                                });

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        _grp_name[0] = null;
                                        group_indicate_text.setText("NONE");
                                        _cancel[0] = true;
                                        alertDialog.dismiss();
                                    }
                                });


                                create_grp.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(HabitActivity.this);
                                        ViewGroup viewGroup = findViewById(android.R.id.content);
                                        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_group_create, viewGroup, false);
                                        builder.setView(dialogView);
                                        final AlertDialog alertDialog = builder.create();

                                        final Button cancelBtn = dialogView.findViewById(R.id.group_cancel);
                                        final Button saveBtn = dialogView.findViewById(R.id.group_save);
                                        final EditText name = dialogView.findViewById(R.id.creating_group_name);

                                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                alertDialog.dismiss();
                                            }
                                        });

                                        saveBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                String grp_name = name.getText().toString();
                                                HabitGroup grp = new HabitGroup(grp_name);
                                                long grp_id = group_dbhandler.insertGroup(grp);

                                                if (grp_id != -1){
                                                    grp.setGrp_id(grp_id);
                                                    groupAdapter._habitGroupList.add(grp);
                                                    groupAdapter.notifyDataSetChanged();
                                                    Toast.makeText(HabitActivity.this, "New group has been created.", Toast.LENGTH_SHORT).show();
                                                }

                                                alertDialog.dismiss();
                                            }
                                        });

                                        alertDialog.show();
                                    }
                                });

                                alertDialog.show();
                            }
                        });

                        // Edit reminder
                        final HabitReminder habitReminder = habit.getHabitReminder();
                        final boolean[] reminder_flag = {false};
                        final String[] txt = {""};
                        final boolean[] modified_reminder = {false};
                        if (habitReminder != null){
                            habit_reminder_indicate_text.setText((format("%d:%d",habitReminder.getHours(),habitReminder.getMinutes())));
                            reminder_flag[0] = true;
                            minutes = habitReminder.getMinutes();
                            hours = habitReminder.getHours();
                            txt[0] = habitReminder.getCustom_text();
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


                                if (!reminder_flag[0]){
                                    if (Build.VERSION.SDK_INT <= 23) {
                                        minutes = timePicker.getCurrentMinute(); // before api level 23
                                        hours = timePicker.getCurrentHour(); // before api level 23
                                    }else{
                                        minutes = timePicker.getMinute(); // after api level 23
                                        hours  = timePicker.getHour(); // after api level 23
                                    }
                                    reminder_switch.setChecked(false);
                                    reminder_displayTime.setText(format("%d:%d",hours,minutes));
                                }else if (habitReminder != null && !modified_reminder[0]){
                                    _custom_text.setText(habitReminder.getCustom_text());
                                    reminder_switch.setChecked(true);
                                    reminder_displayTime.setText(habit_reminder_indicate_text.getText().toString());
                                    Calendar c = Calendar.getInstance();
                                    c.set(Calendar.HOUR_OF_DAY,habitReminder.getHours());
                                    c.set(Calendar.MINUTE, habitReminder.getMinutes());
                                    timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
                                    timePicker.setCurrentMinute(c.get(Calendar.MINUTE));

                                }else if (modified_reminder[0] && reminder_flag[0]){
                                    reminder_switch.setChecked(true);
                                    _custom_text.setText(txt[0]);
                                    reminder_displayTime.setText(format("%d:%d",hours,minutes));
                                    Calendar c = Calendar.getInstance();
                                    c.set(Calendar.HOUR_OF_DAY,hours);
                                    c.set(Calendar.MINUTE, minutes);
                                    timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
                                    timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
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
                                int affected_row = 0;

                                HabitReminder check_reminder = habit.getHabitReminder();

                                if (!habit.getTitle().equals(habit_name.getText().toString())){
                                    Log.d(TAG, "HabitReminder: Update habit title");
                                    habit.modifyTitle(habit_name.getText().toString());
                                    affected_row++;
                                    modified_reminder[0] = true; // to trigger changes to alarm
                                }

                                if (habit.getOccurrence() != Integer.parseInt(habit_occur.getText().toString())){
                                    Log.d(TAG, "HabitReminder: Update habit occurrence");
                                    habit.setOccurrence(Integer.parseInt(habit_occur.getText().toString()));
                                    affected_row++;
                                }

                                if (habit.getPeriod() != _period[0]){
                                    Log.d(TAG, "HabitReminder: Update habit period");
                                    habit.setPeriod(_period[0]);
                                    affected_row++;
                                }

                                if (!habit.getHolder_color().equals(_color[0])){
                                    Log.d(TAG, "HabitReminder: Update habit holder color");
                                    habit.setHolder_color(_color[0]);
                                    affected_row++;
                                }


                                if (modified_grp[0] && _grp_name[0] != null){
                                    Log.d(TAG, "HabitGroup: Modified group ");
                                    habit.setGroup(new HabitGroup(_grp_name[0]));
                                    affected_row++;
                                }else if (_cancel[0] && habit.getGroup() != null){
                                    Log.d(TAG, "HabitGroup: Removed group");
                                    habit.setGroup(null);
                                    affected_row++;
                                }


                                if (reminder_flag[0]){
                                    String _txt = txt[0];
                                    if (check_reminder == null){
                                        Log.d(TAG, "HabitReminder: Set a new alarm");
                                        int id = getUniqueHabitReminderID(); // assign a new id to habit reminder

                                        habit.setHabitReminder(new HabitReminder(habit.getTitle(),id,minutes,hours,_txt));
                                        setReminder(habit.getTitle(),minutes,hours,id,txt[0]);
                                        affected_row++;
                                    }else{
                                        if (modified_reminder[0]){

                                            Log.d(TAG, "HabitReminder: Update an existing alarm");
                                            // cancel the previous alarm
                                            cancelReminder(habit.getTitle(),habit.getHabitReminder().getId(),habit.getHabitReminder().getCustom_text());

                                            if (!_txt.equals(check_reminder.getCustom_text())){
                                                Log.d(TAG, "HabitReminder: Update custom text");
                                                check_reminder.setCustom_text(_txt);
                                                affected_row++;
                                            }

                                            if (check_reminder.getMinutes() != minutes || check_reminder.getHours()!= hours){

                                                if (check_reminder.getMinutes() != minutes){
                                                    Log.d(TAG, "HabitReminder: Update minutes");
                                                    check_reminder.setMinutes(minutes);
                                                    affected_row++;
                                                }

                                                if (check_reminder.getHours() != hours){
                                                    Log.d(TAG, "HabitReminder: Update hours");
                                                    check_reminder.setHours(hours);
                                                    affected_row++;
                                                }
                                            }

                                            setReminder(habit.getTitle(),check_reminder.getMinutes(),check_reminder.getHours(),check_reminder.getId(),check_reminder.getCustom_text());

                                        }
                                    }
                                }else{
                                    Log.d(TAG, "HabitReminder: Cancel HabitReminder");
                                    if (habit.getHabitReminder() != null){
                                        cancelReminder(habit.getTitle(),habit.getHabitReminder().getId(),habit.getHabitReminder().getCustom_text());
                                        affected_row++;
                                    }
                                    habit.setHabitReminder(null);
                                    habit_reminder_indicate_text.setText("NONE");

                                }


                                title.setText(habit_name.getText().toString());
                                occurrence.setText(String.valueOf(habit.getOccurrence()));
                                period.setText(habit.returnPeriodText(habit.getPeriod()));
                                habit_view_upper.setBackgroundResource(habit.returnColorID(habit.getHolder_color()));

                                if (affected_row > 0){
                                    habit_dbHandler.updateHabit(habit);
                                    myAdapter.notifyDataSetChanged();
                                    Log.d(TAG, "HabitEdit/Affeceted rows: "+ affected_row);
                                }

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
                                Log.v(TAG, format("%s deleted!",myAdapter._habitList.getItemAt(position).getTitle()));
                                habit_dbHandler.deleteHabit(myAdapter._habitList.getItemAt(position));
                                myAdapter._habitList.removeItemAt(position);
                                myAdapter.notifyItemRemoved(position);
                                myAdapter.notifyItemRangeChanged(position, myAdapter._habitList.size());
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


    @Override
    public void onClick(View v){
        Log.d(TAG, "onClick: "+v.getId());
        switch (v.getId()){
            case R.id.habit_group_view_create_group:

                break;

        }
    }

    public void populatePeriodBtn(final View dialogView, final int[] period, final TextView period_text ){
         // set listener on buttons to change the color based on the user's option in period section
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

    }

    public void populateColorBtn(final View dialogView, final String[] color){
        // set listener on buttons to change the color based on the user's option in color section
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

    }

    public void habit_add_initialise_colorSection(final View dialogView, final String[] color){
        // initialise the color button at color section since nothing is chosen at first
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(5, Color.BLACK);
        drawable.setColor(getResources().getColor(R.color.colorLightCoral));
        dialogView.findViewById(R.id.lightcoral_btn).setBackground(drawable);
        color[0] = "lightcoral";
    }

    public void habit_add_initialise_periodSection(final View dialogView, final int[] period){
        // initialise the color of btn of "daily" button at period section since nothing is chosen at first
        period[0] = 1;
        dialogView.findViewById(R.id.daily_period).setBackgroundColor(Color.parseColor("#dfdfdf"));
    }

    public void habit_edit_initialise_colorSection(View dialogView, final Habit habit, final String[] _color){
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

    }

    public void habit_edit_initialise_periodSection(View dialogView, final Habit habit, final int[] _period, final TextView period_text){
        for(int i = 0; i < 4; i++){
            if (period_countList[i] == habit.getPeriod()){
                _period[0] = period_countList[i];
                period_text.setText(period_textList[i]);
                dialogView.findViewById(period_buttonIDS[i]).setBackgroundColor(getResources().getColor(R.color.colorWhiteGrey));
                break;
            }
        }
    }


    public void setReminder(String name, int minutes, int hours, int id, String custom_txt){

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.setAction("HabitTracker");
        intent.putExtra("Name", name);
        intent.putExtra("id", id);
        intent.putExtra("custom_txt", custom_txt);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int type = AlarmManager.RTC_WAKEUP;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MINUTE,minutes);
        c.set(Calendar.HOUR_OF_DAY,hours);
        c.set(Calendar.SECOND,0);

        if (System.currentTimeMillis() > c.getTimeInMillis()){
            // increment one day to prevent setting for past alarm
            c.add(Calendar.DATE, 1);
        }

        long time = c.getTime().getTime();

        Log.d(TAG, "setReminder for ID "+ id + " at " + c.getTime());
        am.setRepeating(type, time, AlarmManager.INTERVAL_DAY, pi);
    }

    public void cancelReminder(String name,int id, String custom_txt){
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.setAction("HabitTracker");
        intent.putExtra("Name", name);
        intent.putExtra("id",id);
        intent.putExtra("custom_txt",custom_txt);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Log.d(TAG, "cancelReminder for ID "+ id);
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

        if (sharedPreferences.getInt("alarm_id",-1)<=0){
            editor.putInt("alarm_id",0);
            editor.apply();
        }

    }

    public int getUniqueHabitReminderID(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = sharedPreferences.getInt("alarm_id",-1);
        int _new = ++id;
        editor.putInt("alarm_id", _new);
        Log.d(TAG, "getUniqueHabitReminderID: " + _new);
        editor.apply();
        return id;

    }

}

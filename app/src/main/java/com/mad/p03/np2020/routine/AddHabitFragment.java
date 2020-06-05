package com.mad.p03.np2020.routine;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.mad.p03.np2020.routine.Adapter.HabitAdapter;
import com.mad.p03.np2020.routine.Adapter.HabitGroupAdapter;
import com.mad.p03.np2020.routine.Adapter.OnItemClickListener;
import com.mad.p03.np2020.routine.Class.AlarmReceiver;
import com.mad.p03.np2020.routine.Class.Habit;
import com.mad.p03.np2020.routine.Class.HabitGroup;
import com.mad.p03.np2020.routine.Class.HabitReminder;
import com.mad.p03.np2020.routine.Class.User;
import com.mad.p03.np2020.routine.background.HabitGroupWorker;
import com.mad.p03.np2020.routine.background.HabitWorker;
import com.mad.p03.np2020.routine.database.HabitDBHelper;
import com.mad.p03.np2020.routine.database.HabitGroupDBHelper;

import java.util.Calendar;
import java.util.Date;

import static java.lang.String.format;

public class AddHabitFragment extends Fragment {

    private static final String TAG = "AddHabitFragment";

    private OnFragmentInteractionListener mListener;

    // initialise recyclerview and adapter
    private HabitAdapter habitAdapter;
    private RecyclerView habitGroupRecyclerView;
    private HabitGroupAdapter groupAdapter;

    // widgets
    private TextView menu_count;
    private TextView habit_name;
    private TextView habit_occur;
    private TextView period_text;
    private TextView habit_reminder_indicate_text;
    private TextView group_indicate_text;
    private ImageButton add_btn;
    private ImageButton minus_btn;
    private Button buttonClose;
    private Button buttonOk;

    // initialise period section
    private int [] period_buttonIDS;
    private String[] period_textList;
    private int[] period_countList;
    private int[] period;

    // initialise color section
    private int[] color_buttonIDS;
    private int[] color_schemeIDS;
    private String[] colorList;
    private String[] color;

    // group
    private String[] _grp_name;
    private long[] _grp_id;

    // reminder
    private int[] chosen_hours;
    private int[] chosen_minutes;
    private String[] custom_text;
    private boolean[] reminder_flag;

    // initialise the handler
    private HabitDBHelper habit_dbHandler;
    private HabitGroupDBHelper group_dbhandler;

    // initialise sharedPrefs
    private static String SHARED_PREFS;

    private User user;

    private int minutes, hours;

    // initialise the dateFormat
    private DateFormat dateFormat;

    public AddHabitFragment() { }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.add_habit, container, false);

        // initialise widgets
        menu_count = view.findViewById(R.id.menu_count);
        habit_name = view.findViewById(R.id.add_habit_name);
        habit_occur = view.findViewById(R.id.habit_occurence);
        period_text = view.findViewById(R.id.period_txt);
        habit_reminder_indicate_text = view.findViewById(R.id.reminder_indicate_text);
        group_indicate_text = view.findViewById(R.id.group_indicate_text);
        add_btn = view.findViewById(R.id.menu_add_count);
        minus_btn = view.findViewById(R.id.menu_minus_count);
        buttonClose = view.findViewById(R.id.habit_close);
        buttonOk = view.findViewById(R.id.create_habit);

        // set values for period section
        period_buttonIDS = new int[]{R.id.daily_period, R.id.weekly_period, R.id.monthly_period, R.id.yearly_period};
        period_textList = new String[]{"DAY", "WEEK", "MONTH", "YEAR"};
        period_countList = new int[]{1, 7, 30, 365};

        // set values for color section
        color_buttonIDS = new int[]{R.id.lightcoral_btn, R.id.slightdesblue_btn, R.id.fadepurple_btn, R.id.cyangreen_btn};
        color_schemeIDS = new int[]{R.color.colorLightCoral, R.color.colorSlightDesBlue, R.color.colorFadePurple, R.color.colorCyanGreen};
        colorList = new String[]{"lightcoral", "slightdesblue", "fadepurple", "cyangreen"};


        // initialise period
        period = new int[1]; // this is used to store the chosen period
        // initialise holder color
        color = new String[1]; // this is used to store the chosen color

        // initialise group section
        _grp_name = new String[]{null}; // this is used to store the chosen group name
        _grp_id = new long[1]; // this is used to store the chosen group id

        // initialise reminder
        chosen_hours = new int[1]; // this is used to store the chosen reminder hours
        chosen_minutes = new int[1]; // this is used to store the chosen reminder minutes
        custom_text = new String[]{""}; // this is used to store the chosen reminder's custom text
        reminder_flag = new boolean[]{false}; // this is used to store the reminder flag which indicates active or inactive of the reminder

        user = new User();
        user.setUID(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // set the HabitDBHelper
        habit_dbHandler = new HabitDBHelper(getContext());
        // set the HabitGroupDBHelper
        group_dbhandler = new HabitGroupDBHelper(getContext());

        // set sharedPreferences
        SHARED_PREFS = "sharedPrefs";

        // initialise dateFormat
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        Bundle args = getArguments();
        habitAdapter = (HabitAdapter) args.get("habitDBHelper");

        showSoftKeyboard(habit_name);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        populatePeriodBtn(view, period, period_text);
        habit_add_initialise_periodSection(view,period);

        populateColorBtn(view,color);
        habit_add_initialise_colorSection(view,color);

        // set onClickListener on close button
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                sendBack();  // dismiss the add_habit fragment
            }
        });

        // set onClickListener on the add count button
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cnt = menu_count.getText().toString();
                // add the count by 1
                int count = Integer.parseInt(cnt);
                count++;
                // set the count in the TextView
                menu_count.setText(String.valueOf(count));
            }
        });

        // set onClickListener on the minus count button
        minus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cnt = menu_count.getText().toString();
                // minus the count by 1 if count > 0
                int count = Integer.parseInt(cnt);
                if (count > 0){
                    count--;
                }
                // set the count in the TextView
                menu_count.setText(String.valueOf(count));
            }
        });

        // set onClickListener on the group indicate text
        group_indicate_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an alert dialog (group view)
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog); // use the custom alert dialog type
                LayoutInflater inflater = getLayoutInflater();
                View convertView = inflater.inflate(R.layout.habit_group, null); // inflate the layout

                // initialise widgets
                ImageView close = convertView.findViewById(R.id.habit_group_view_close);
                Button cancel = convertView.findViewById(R.id.habit_group_view_cancel);
                Button create_grp = convertView.findViewById(R.id.habit_group_view_create_group);
                final TextView curr_grp = convertView.findViewById(R.id.current_grp);

                // set the current group text based on the chosen group
                if (_grp_name[0] != null){
                    // set the chosen group on TextView
                    curr_grp.setText(_grp_name[0]);
                }else{
                    // set "None" on TextView
                    curr_grp.setText("None");
                }

                // inflate habitGroup RecyclerView
                habitGroupRecyclerView = convertView.findViewById(R.id.habit_recycler_view);
                habitGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                // initialise group adapter
                groupAdapter= new HabitGroupAdapter(group_dbhandler.getAllGroups(),getContext());
                // set groupAdapter on RecyclerView
                habitGroupRecyclerView.setAdapter(groupAdapter);
                builder.setView(convertView); // set the view of the builder
                final AlertDialog alertDialog = builder.create(); // build the dialog (group view)

                // set OnItemClickListener on group adapter
                groupAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        // when a holder is clicked

                        // retrieve the group of the holder
                        HabitGroup grp = groupAdapter._habitGroupList.get(position);
                        group_indicate_text.setText(grp.getGrp_name()); // update the chosen group on the group_indicate_text TextView
                        _grp_name[0] = grp.getGrp_name(); // update the chosen group name
                        _grp_id[0] = grp.getGrp_id(); // update the chosen group id
                        alertDialog.dismiss(); // dismiss the dialog (group view)
                    }
                });

                // set onClickListener on close button
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss(); // dismiss the dialog (group view)
                    }
                });

                // set onClickListener on cancel group button
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // cancel the existing group
                        _grp_name[0] = null; // update the chosen group name to null
                        _grp_id[0] = -1; // update the chosen group id to negative(ineffective)
                        group_indicate_text.setText("NONE"); // set "None" on group_indicate_text
                        alertDialog.dismiss(); // dismiss the dialog (group view)
                    }
                });

                // set onClickListener on create group button
                create_grp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // create an alert dialog (create group)
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        ViewGroup viewGroup = getView().findViewById(android.R.id.content);
                        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_group_create, viewGroup, false); // inflate the view
                        builder.setView(dialogView); // set the view of the builder
                        final AlertDialog alertDialog = builder.create(); // build the dialog (create group)

                        // initialise widgets
                        final Button cancelBtn = dialogView.findViewById(R.id.group_cancel);
                        final Button saveBtn = dialogView.findViewById(R.id.group_save);
                        final EditText name = dialogView.findViewById(R.id.creating_group_name);

                        // setonClickListener on cancel button
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss(); // dismiss the dialog (create group)
                            }
                        });

                        // setonClickListener on save button
                        saveBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Retrieve the values from the input field
                                String grp_name = name.getText().toString();
                                HabitGroup grp = new HabitGroup(grp_name);

                                // Insert the group into SQLiteDatabase
                                long grp_id = group_dbhandler.insertGroup(grp);
                                // The insert process is success if group id returned is not equal to 1

                                if (grp_id != -1){ // check if group id returned is not equal to 1
                                    grp.setGrp_id(grp_id); // set the group id to the group object
                                    groupAdapter._habitGroupList.add(grp); // add the group to the adapter list
                                    groupAdapter.notifyDataSetChanged(); // notify data set has changed
                                    writeHabitGroup_Firebase(grp, user.getUID()); // write habitGroup to firebase
                                    Toast.makeText(getContext(), "New group has been created.", Toast.LENGTH_SHORT).show();
                                }

                                alertDialog.dismiss(); // dismiss the dialog after the process (create group)
                            }
                        });

                        alertDialog.show(); // show the create group dialog
                    }
                });

                alertDialog.show(); // show the group view dialog
            }
        });

        // set onClickListener on habit_reminder_indicate_text
        habit_reminder_indicate_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an alert dialog (set reminder)
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog); // use the custom alert dialog type
                ViewGroup viewGroup = getView().findViewById(android.R.id.content);
                final View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_reminder_view, viewGroup, false); // inflate the view
                builder.setView(dialogView); // set the view of builder
                final AlertDialog alertDialog = builder.create(); // build the dialog (set reminder)

                // initialise widgets
                final ImageView close_btn = dialogView.findViewById(R.id.habit_reminder_view_close);
                final Switch reminder_switch = dialogView.findViewById(R.id.habit_reminder_view_switch);
                final TextView reminder_displayTime = dialogView.findViewById(R.id.habit_reminder_view_displaytime);
                final TimePicker timePicker = dialogView.findViewById(R.id.habit_reminder_view_timepicker);
                final TextView _custom_text = dialogView.findViewById(R.id.habit_reminder_view_customtext);
                final ImageView save_btn = dialogView.findViewById(R.id.habit_reminder_view_save);

                // to determine what should be displayed on timePicker and time indicate field
                if (reminder_flag[0]){ // if the flag is true which indicates active reminder
                    Calendar c = Calendar.getInstance();
                    // display time and timePicker based on the chosen hours and minutes
                    c.set(Calendar.HOUR_OF_DAY, chosen_hours[0]);
                    c.set(Calendar.MINUTE, chosen_minutes[0]);
                    timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
                    timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
                    reminder_switch.setChecked(true); // set the switch checked as the reminder is active
                    reminder_displayTime.setText(format("%d:%d",chosen_hours[0],chosen_minutes[0])); // set the text based on the chosen timing

                }else{ // if the flag is false which indicates inactive reminder
                    // set the minutes and hours based on the current time
                    if (Build.VERSION.SDK_INT <= 23) {
                        minutes = timePicker.getCurrentMinute(); // before api level 23
                        hours = timePicker.getCurrentHour(); // before api level 23
                    }else{
                        minutes = timePicker.getMinute(); // after api level 23
                        hours  = timePicker.getHour(); // after api level 23
                    }
                    reminder_switch.setChecked(false); // set the switch unchecked as the reminder is inactive
                    reminder_displayTime.setText(format("%d:%d",hours,minutes)); // set the text based on the chosen timing
                }

                // leave the custom text input field as blank if nothing has been filled and recorded down
                // or set the custom text based on the chosen custom text
                if (custom_text[0] != ""){
                    _custom_text.setText(custom_text[0]);
                }

                // set onTimeChangedListener on timePicker
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
                        reminder_displayTime.setText(format("%d:%d",hours,minutes)); // update the text based on the chosen timing
                    }
                });

                // set onClickListener on save button
                save_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (reminder_switch.isChecked()){ // if switch is switched, turn the reminder active
                            // set the text on habit_reminder_indicate_text based on chosen timing
                            habit_reminder_indicate_text.setText((format("%d:%d",hours,minutes)));
                            if (_custom_text.getText().toString() != ""){
                                // update the chosen custom text if it is not blank
                                custom_text[0] = _custom_text.getText().toString();
                            }
                            reminder_flag[0] = true; // turn on the flag
                            chosen_hours[0] = hours; // record down the chosen hours
                            chosen_minutes[0] = minutes; // record down the chosen minutes

                        }else{ // if switch is unchecked, turn the reminder inactive
                            reminder_flag[0] = false; // turn off thr flag
                            habit_reminder_indicate_text.setText("NONE"); // set "None" on habit_reminder_indicate_text
                            custom_text[0] = ""; // reset the custom text as nothing
                        }

                        alertDialog.dismiss(); // dismiss the dialog (set reminder)
                    }
                });

                // set onClickListener on close button
                close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss(); // dismiss the dialog (set reminder)
                    }
                });

                alertDialog.show(); // show the alert dialog (set reminder)
            }
        });

        // set onClickListener on create button
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                String name = habit_name.getText().toString(); // retrieve the title of the habit from the input field
                if (name.equalsIgnoreCase("")){
                    // set Error Message if the user never input the habit title
                    habit_name.setError("Please enter habit name");
                    return;
                }

                int occur = Integer.parseInt(habit_occur.getText().toString()); // retrieve the occurrence of the habit from the input field
                int cnt = Integer.parseInt(menu_count.getText().toString()); // retrieve the count of the habit from the input field

                Date date = new Date(); // call the date function
                HabitReminder hr = null;
                if (reminder_flag[0]){ // set a reminder if the reminder is in active
                    int id = getUniqueHabitReminderID(); // retrieve the unique habitReminder ID
                    String txt = custom_text[0]; // retrieve the custom text
                    setReminder(name, chosen_minutes[0], chosen_hours[0], id, txt); // set the reminder
                    hr = new HabitReminder(name, id, chosen_minutes[0], chosen_hours[0], txt); // set a new habitReminder object
                }

                HabitGroup hg = null;
                if (_grp_name[0] != null){ // set a new habitGroup object if the user chooses something for the group name
                    hg = new HabitGroup(_grp_id[0], _grp_name[0]);
                }

                // create a habit object
                Habit habit = new Habit(name, occur, cnt, period[0], dateFormat.format(date),color[0],hr,hg);

                // insert the habit into SQLiteDatabase
                long habitID = habit_dbHandler.insertHabit(habit, user.getUID());
                // The insert process is success if habit id returned is not equal to 1

                if (habitID != -1){ // if habitID returned is legit
                    habit.setHabitID(habitID); // set the id to the habit
                    habitAdapter._habitList.addItem(habit); // add the habit into the adapter list
                    habitAdapter.notifyDataSetChanged(); // notify the data set has changed
                    writeHabit_Firebase(habit, user.getUID(), false); // write habit to firebase

                    Log.d(TAG, "onClick: "+habit.getHabitID());
                    // toast a message to alert the habit has been created
                    Toast.makeText(getContext(), format("Habit %shas been created.",capitalise(name)), Toast.LENGTH_SHORT).show();
                    sendBack();
                }


            }
        });


    }

    /**
     *
     * Method to be called once the fragment is associated with its activity.
     * @param context set the context to this content
     * */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    private void sendBack() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
            Log.v(TAG, "Sending back");
        }
    }

    /**
     *
     * Callback interface
     * */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }


    /**
     *
     * This method is used to set the habitReminder.
     *
     * @param name This parameter refers to the title of the reminder.
     *
     * @param minutes This parameter refers to the minutes set of the reminder.
     *
     * @param hours  This parameter refers to the hours set of the reminder.
     *
     * @param custom_txt This parameter refers to the custom message of the reminder.
     *
     * */
    public void setReminder(String name, int minutes, int hours, int id, String custom_txt){
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.setAction("HabitTracker");
        intent.putExtra("Name", name);
        intent.putExtra("id", id);
        intent.putExtra("custom_txt", custom_txt);
        // This initialise the pending intent which will be sent to the broadcastReceiver
        PendingIntent pi = PendingIntent.getBroadcast(getContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) this.getActivity().getSystemService(Context.ALARM_SERVICE);
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
        // AlarmManager set the daily repeating alarm on time chosen by the user.
        // The broadcastReceiver will receive the pending intent on the time.
        am.setRepeating(type, time, AlarmManager.INTERVAL_DAY, pi);
    }

    /**
     *
     * This method is used to cancel the habitReminder.
     *
     * @param name This parameter refers to the title of the reminder.
     *
     * @param id This parameter refers to the unique id of the alarm.
     *
     * @param custom_txt This parameter refers to the custom message of the reminder.
     *
     * */
    public void cancelReminder(String name,int id, String custom_txt){
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.setAction("HabitTracker");
        intent.putExtra("Name", name);
        intent.putExtra("id",id);
        intent.putExtra("custom_txt",custom_txt);
        // fill in the same pending intent as when setting it
        PendingIntent pi = PendingIntent.getBroadcast(getContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) this.getActivity().getSystemService(Context.ALARM_SERVICE);

        Log.d(TAG, "cancelReminder for ID "+ id);
        // Alarm manager cancel the reminder
        am.cancel(pi);
    }
    /**
     *
     * This method is used to get the unique habit reminder id
     *
     * @return int It will return the unique id.
     * */
    public int getUniqueHabitReminderID(){
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = sharedPreferences.getInt("alarm_id",-1);
        int _new = ++id;
        editor.putInt("alarm_id", _new);
        Log.d(TAG, "getUniqueHabitReminderID: " + _new);
        editor.apply();
        return id;
    }

    /**
     *
     * This method is used to initialise the color button at color section since nothing is chosen at first
     *
     * @param dialogView This parameter refers to the view
     *
     * @param color This parameter refers to the color list which indicates the chosen color
     *
     * */
    public void habit_add_initialise_colorSection(final View dialogView, final String[] color){
        // initialise the color button at color section since nothing is chosen at first
        // At default, lightCoral color is chosen.
        // A black border will surround the color.
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(5, Color.BLACK);
        drawable.setColor(getResources().getColor(R.color.colorLightCoral));
        dialogView.findViewById(R.id.lightcoral_btn).setBackground(drawable);
        color[0] = "lightcoral";
    }

    /**
     *
     * This method is used to initialise the color of "daily" button at period section since nothing is chosen at first
     *
     * @param dialogView This parameter refers to the view
     *
     * @param period This parameter refers to the period list which indicates the chosen period
     *
     * */
    public void habit_add_initialise_periodSection(final View dialogView, final int[] period){
        // initialise the color of "daily" button at period section since nothing is chosen at first
        // At default, "daily" period is chosen at first.
        // A grey background will surround the "daily" button.
        period[0] = 1;
        dialogView.findViewById(R.id.daily_period).setBackgroundColor(Color.parseColor("#dfdfdf"));
    }

    /**
     *
     * This method is used to populate the period button by setting onclickListener on them
     * and it will change the color based on the user's option in period section.
     *
     * @param dialogView This parameter refers to the view
     *
     * @param period This parameter refers to the period list which indicates the chosen period
     *
     * @param period_text This parameter refers to the period TextView in the view
     * */
    public void populatePeriodBtn(final View dialogView, final int[] period, final TextView period_text){
        // set listener on buttons to change the color based on the user's option in period section
        for (final int i :period_buttonIDS){
            final Button btn = dialogView.findViewById(i); // find button in the view
            btn.setBackgroundColor(Color.TRANSPARENT);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = btn.getId(); // retrieve the buttonID

                    for (int i = 0; i < 4; i++){
                        Button _btn = dialogView.findViewById(period_buttonIDS[i]);
                        if (id == period_buttonIDS[i]){
                            // if is selected by the user, add a grey background.
                            _btn.setBackgroundColor(Color.parseColor("#dfdfdf"));
                            period_text.setText(period_textList[i]);  // set the period text
                            period[0] = period_countList[i];  // update the chosen period
                        }else {
                            // if is not selected by the user, remove the grey background.
                            _btn.setBackgroundColor(Color.TRANSPARENT);
                        }

                    }
                }
            });
        }

    }

    /**
     *
     * This method is used to populate the color button by setting onclickListener on them
     * and it will change the color based on the user's option in color section.
     *
     * @param dialogView This parameter refers to the view
     *
     * @param color This parameter refers to the color list which indicates the chosen color
     *
     * */
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
                            // if is selected by the user, add a black border surrounding the color button.
                            GradientDrawable drawable = new GradientDrawable();
                            drawable.setShape(GradientDrawable.RECTANGLE);
                            drawable.setStroke(5, Color.BLACK);
                            drawable.setColor(getResources().getColor(color_schemeIDS[i]));
                            _btn.setBackground(drawable);
                            color[0] = colorList[i];
                        }else {
                            // if is not selected by the user, remove the black border.
                            _btn.setBackgroundResource(color_schemeIDS[i]);
                        }
                    }
                }
            });
        }

    }

    /**
     *
     * This method is used to send the work request
     *  to the HabitWorker(WorkManager) to do the writing firebase action
     *  when the network is connected.
     *
     * @param habit This parameter is used to get the habit object
     *
     * @param UID This parameter is used to get the userID
     *
     * @param isDeletion This parameter is used to indicate whether it is deletion of habit to firebase
     *
     * */
    public void writeHabit_Firebase(Habit habit, String UID, boolean isDeletion){
        Log.i(TAG, "Uploading to Firebase");

        // set constraint that the network must be connected
        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // put data in a data builder
        Data firebaseUserData = new Data.Builder()
                .putString("ID", UID)
                .putString("habitData", habit_serializeToJson(habit))
                .putBoolean("deletion", isDeletion)
                .build();

        // send a work request
        OneTimeWorkRequest mywork =
                new OneTimeWorkRequest.Builder(HabitWorker.class)
                        .setConstraints(myConstraints)
                        .setInputData(firebaseUserData)
                        .build();

        WorkManager.getInstance(getContext()).enqueue(mywork);
    }

    /**
     *
     * This method is used to send the work request
     *  to the HabitGroupWorker(WorkManager) to do the writing firebase action
     *  when the network is connected.
     *
     * @param habitGroup This parameter is used to get the habitGroup object
     *
     * @param UID This parameter is used to get the userID
     *
     * */
    public void writeHabitGroup_Firebase(HabitGroup habitGroup, String UID){
        Log.i(TAG, "Uploading to Firebase");

        // set constraint that the network must be connected
        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // put data in a data builder
        Data firebaseUserData = new Data.Builder()
                .putString("ID", UID)
                .putString("habitData", habitGroup_serializeToJson(habitGroup))
                .build();

        // send a work request
        OneTimeWorkRequest mywork =
                new OneTimeWorkRequest.Builder(HabitGroupWorker.class)
                        .setConstraints(myConstraints)
                        .setInputData(firebaseUserData)
                        .build();

        WorkManager.getInstance(getContext()).enqueue(mywork);
    }

    /**
     *
     * This method is used to serialize a single object. (into Json String)
     *
     * @param habit This parameter is used to get the habit object
     *
     * @return String This returns the serialized object.
     *
     * */
    public String habit_serializeToJson(Habit habit) {
        Gson gson = new Gson();
        Log.i(TAG,"Object serialize");
        return gson.toJson(habit);
    }

    /**
     *
     * This method is used to serialize a single object. (into Json String)
     *
     * @param habitGroup This parameter is used to get the habitGroup object
     *
     * @return String This returns the serialized object.
     *
     * */
    public String habitGroup_serializeToJson(HabitGroup habitGroup) {
        Gson gson = new Gson();
        Log.i(TAG,"Object serialize");
        return gson.toJson(habitGroup);
    }


    /**
     *
     * This method is used to format text by capitalising the first text of each split text
     *
     * @param text This parameter is used to get the text
     *
     * @return String This returns the formatted text
     * */
    public String capitalise(String text){
        String txt = "";
        String[] splited = text.split("\\s+");
        for (String s: splited){
            txt += s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase() + " ";
        }
        return txt;
    }

    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)getActivity(). getSystemService(Activity.INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }
}

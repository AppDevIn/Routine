package com.mad.p03.np2020.routine;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.mad.p03.np2020.routine.DAL.HabitRepetitionDBHelper;
import com.mad.p03.np2020.routine.background.HabitRepetitionWorker;
import com.mad.p03.np2020.routine.models.AlarmReceiver;
import com.mad.p03.np2020.routine.models.Habit;
import com.mad.p03.np2020.routine.models.HabitGroup;
import com.mad.p03.np2020.routine.models.HabitReminder;
import com.mad.p03.np2020.routine.models.HabitRepetition;
import com.mad.p03.np2020.routine.models.User;
import com.mad.p03.np2020.routine.background.HabitWorker;
import com.mad.p03.np2020.routine.DAL.HabitDBHelper;

import java.util.ArrayList;
import java.util.Calendar;

import static java.lang.String.format;

/**
 *
 * Habit activity used to manage the edit habit layout section
 *
 * @author Hou Man
 * @since 02-06-2020
 */

public class HabitEditActivity extends AppCompatActivity {

    private static final String TAG = "HabitEditActivity";
    private static final String SHARED_PREFS = "sharedPrefs"; // initialise sharedPrefs

    private TextView habit_name, habit_occur, period_text, habit_reminder_indicate_text, group_indicate_text, habit_count;
    private Button buttonClose, buttonOk, buttonDelete;
    private ImageButton menu_add_count, menu_minus_count, menu_edit_count;

    private Habit habit;

    // initialise the handler
    private HabitDBHelper habit_dbHandler;
    private HabitRepetitionDBHelper habitRepetitionDBHelper;

    //User
    private User user;

    // Period and color section
    private int[] period;
    private String[] color;

    /**
     *
     * This method will be called when the start of the HabitActivity.
     * This will initialise the widgets and set onClickListener on them.
     *
     * @param savedInstanceState This parameter refers to the saved state of the bundle object.
     *
     * */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habit_edit);

        // set the layout in full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // initialise the widgets
        habit_name = findViewById(R.id.add_habit_name);
        habit_occur = findViewById(R.id.habit_occurence);
        period_text = findViewById(R.id.period_txt);
        habit_reminder_indicate_text = findViewById(R.id.reminder_indicate_text);
        group_indicate_text = findViewById(R.id.group_indicate_text);
        buttonClose = findViewById(R.id.habit_close);
        buttonOk = findViewById(R.id.create_habit);
        habit_count = findViewById(R.id.menu_count);
        menu_add_count = findViewById(R.id.menu_add_count);
        menu_minus_count = findViewById(R.id.menu_minus_count);
        menu_edit_count = findViewById(R.id.menu_edit_count);
        buttonDelete = findViewById(R.id.habit_view_edit_delete);

        // set the HabitDBHelper
        habit_dbHandler = new HabitDBHelper(this);
        habitRepetitionDBHelper = new HabitRepetitionDBHelper(this);

        // set new user
        user = new User();
        user.setUID(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // initialise period section
        period = new int[1]; // this is used to store the chosen period
        populatePeriodBtn(this, period, period_text);

        // initialise color section
        color = new String[1]; // this is used to store the chosen color
        populateColorBtn(this,color);

        // This is to get the habit object from intent bundle
        Intent intent = getIntent();
        if (intent.hasExtra("recorded_habit")){
            habit = deserializeFromJson(intent.getExtras().getString("recorded_habit"));
        }else{
            Log.d(TAG, "LOADING HABIT ERROR ");
        }

        // initialise the period and color section
        habit_edit_initialise_periodSection(this, habit, period, period_text);
        habit_edit_initialise_colorSection(this, habit, color);

        // set text on the input fields based on habit
        habit_name.setText(habit.getTitle());
        habit_occur.setText(String.valueOf(habit.getOccurrence()));
        habit_count.setText(String.valueOf(habit.getCount()));
        HideKeyboard();
        // Retrieve tha habitGroup object
        final HabitGroup habitGroup = habit.getGroup();

        if (habitGroup != null ){ // if habitGroup object is not null, set text based on its group name on the TextView
            group_indicate_text.setText(habitGroup.getGrp_name());
        }else{ // if habitGroup object is null, set "NONE" name on the TextView
            group_indicate_text.setText("NONE");
        }

        // Retrieve tha habitGroup object
        final HabitReminder habitReminder = habit.getHabitReminder();

        if (habitReminder != null){ // if habitReminder object is not null, set text based on its reminder timing on the TextView
            habit_reminder_indicate_text.setText((format("%02d:%02d",habitReminder.getHours(),habitReminder.getMinutes()))); // set the timing on the TextView
        }else{
            habit_reminder_indicate_text.setText("NONE");
        }

//        menu_add_count.setBackgroundColor(Color.TRANSPARENT);
//        menu_minus_count.setBackgroundColor(Color.TRANSPARENT);
        menu_edit_count.setBackgroundColor(Color.TRANSPARENT);

        // set onClickListener on the add count button
        menu_add_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cnt = habit_count.getText().toString();
                // add the count by 1
                int count = Integer.parseInt(cnt);
                count++;
                // set the count in the TextView
                habit_count.setText(String.valueOf(count));
            }
        });

        // set onClickListener on the minus count button
        menu_minus_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cnt = habit_count.getText().toString();
                // minus the count by 1 if count > 0
                int count = Integer.parseInt(cnt);
                if (count > 0){
                    count--;
                }
                // set the count in the TextView
                habit_count.setText(String.valueOf(count));
            }
        });

        // set onClickListener on modify count button
        menu_edit_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Habit: Modify Count");
                // Create an alert dialog (modify count)
                AlertDialog.Builder builder = new AlertDialog.Builder(HabitEditActivity.this); // initialise the builder
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_view_modifycnt_dialog, viewGroup, false); // inflate the view
                builder.setView(dialogView); //set view to the builder
                final AlertDialog alertDialog = builder.create(); // build the alert dialog

                // initialise the widgets
                final TextView dialog_title = dialogView.findViewById(R.id.habit_view_dialog_title);
                final Button cancelBtn = dialogView.findViewById(R.id.cancel_dialog);
                final Button saveBtn = dialogView.findViewById(R.id.save_dialog);
                final EditText dialog_cnt = dialogView.findViewById(R.id.dialog_cnt);

                // set text on the input fields based on the habit
                dialog_title.setText(habit.getTitle());
                dialog_cnt.setHint(habit_count.getText().toString());

                dialog_cnt.requestFocus();
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                // set onClickListener on the cancel button
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss(); // dismiss the alert dialog (modify count)
                        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    }
                });

                // set onClickListener on the save button
                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cntString = dialog_cnt.getText().toString();
                        if (cntString.equalsIgnoreCase("")){
                            dialog_cnt.setError("Please enter a number");
                            return;
                        }
                        int dialogCnt = Integer.parseInt(cntString); // retrieve the count from the input field
                        if (dialogCnt > 1000 ){
                            dialog_cnt.setError("Please enter a smaller number");
                            return;
                        }

                        habit_count.setText(cntString);
                        alertDialog.dismiss(); // dismiss the alert dialog (modify count)
                        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    }
                });

                alertDialog.show(); // show the alert dialog (modify count)
            }
        });

        // set onClickListener on the group indicate text
        group_indicate_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go the habit group activity
                Intent activityName = new Intent(HabitEditActivity.this, HabitGroupActivity.class);
                Bundle extras = new Bundle();
                extras.putString("recorded_habit", habit_serializeToJson(recordCurrentHabit()));
                extras.putString("action", "edit");
                activityName.putExtras(extras);
                startActivity(activityName);
                finish();
            }
        });

        // set onClickListener on the habit reminder indicate text
        habit_reminder_indicate_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // got to habit reminder activity
                Intent activityName = new Intent(HabitEditActivity.this, HabitReminderActivity.class);
                Bundle extras = new Bundle();
                extras.putString("recorded_habit", habit_serializeToJson(recordCurrentHabit()));
                extras.putString("action", "edit");
                activityName.putExtras(extras);
                startActivity(activityName);
                finish();
            }
        });

        // set onClickListener on close button
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go back to habit view activity
                Habit initial_habit = habit_dbHandler.getHabit(habit);
                Intent activityName = new Intent(HabitEditActivity.this, HabitViewActivity.class);
                Bundle extras = new Bundle();
                extras.putString("recorded_habit", habit_serializeToJson(initial_habit));
                activityName.putExtras(extras);
                startActivity(activityName);
                finish();
            }
        });

        // set onClickListener on delete habit button
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an alert dialog (delete habit)
                AlertDialog.Builder builder = new AlertDialog.Builder(HabitEditActivity.this); // initialise the builder of alert dialog
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete this habit?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // if the user choose to delete the habit

                        Log.v(TAG, format("%s deleted!",habit.getTitle()));

                        if (habit.getHabitReminder() != null){ // if the reminder of the habit object is not null
                            // cancel the reminder if existed
                            cancelReminder(habit.getTitle(),habit.getHabitReminder().getId(),habit.getHabitReminder().getCustom_text());
                        }

                        // delete the habit
                        habit_dbHandler.deleteHabit(habit); // delete the habit in SQLiteDatabase
                        ArrayList<Long> arr = habitRepetitionDBHelper.getAllHabitRepetitionsIDByHabitID(habit.getHabitID());
                        HabitRepetition hr = new HabitRepetition();
                        hr.setRowList(arr);
                        habitRepetitionDBHelper.deleteHabitRepetition(habit);

                        writeHabit_Firebase(habit, user.getUID(), true); // delete the habit in the firebase
                        writeHabitRepetition_Firebase(hr, user.getUID(), true);

                        // go back to habit activity
                        Intent activityName = new Intent(HabitEditActivity.this, HabitActivity.class);
                        activityName.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(activityName);
                        finish();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        // if the user refused to delete the habit
                        Log.v(TAG,"User refuses to delete!");
                    }
                });

                AlertDialog alert = builder.create(); // build the dialog
                alert.show(); // show the alert dialog (delete habit)
            }
        });

        // set onClickListener on save button
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // update habit title if modified
                if (!habit.getTitle().equals(habit_name.getText().toString())){
                    Log.d(TAG, "Habit: Update habit title");
                    habit.modifyTitle(habit_name.getText().toString()); // modify the title
                    if (habit.getHabitReminder() != null){
                        habit.getHabitReminder().setMessage(habit_name.getText().toString());
                    }
                }

                // update habit count if modified
                if (habit.getCount() != Integer.parseInt(habit_count.getText().toString())){
                    Log.d(TAG, "Habit: Update habit count");
                    habit.setCount(Integer.parseInt(habit_count.getText().toString())); // modify the occurrence
                }

                // update habit occurrence if modified
                if (habit.getOccurrence() != Integer.parseInt(habit_occur.getText().toString())){
                    Log.d(TAG, "Habit: Update habit occurrence");
                    habit.setOccurrence(Integer.parseInt(habit_occur.getText().toString())); // modify the occurrence
                }

                // update habit period if modified
                if (habit.getPeriod() != period[0]){
                    Log.d(TAG, "Habit: Update habit period");
                    habit.setPeriod(period[0]); // modify the period
                }

                // update habit holder color if modified
                if (!habit.getHolder_color().equals(color[0])){
                    Log.d(TAG, "Habit: Update habit holder color");
                    habit.setHolder_color(color[0]); // modify the holder color
                }

                // set or cancel the alarm based on the habit reminder
                HabitReminder reminder = habit.getHabitReminder();
                if (habit_dbHandler.isReminderExisted(habit)){
                    if (reminder == null){
                        Log.d(TAG, "HabitReminder: Cancel an existing alarm");
                        HabitReminder cancel = habit_dbHandler.getReminder(habit);
                        Log.d(TAG, cancel.getMessage());
                        cancelReminder(habit.getTitle(), cancel.getId(), cancel.getCustom_text());
                    }else{
                        if (!reminder.isIdentical(habit_dbHandler.getReminder(habit))){
                            Log.d(TAG, "HabitReminder: Overriding the alarm");
                            cancelReminder(habit.getTitle(), reminder.getId(), reminder.getCustom_text());
                            setReminder(habit.getTitle(),reminder.getMinutes(),reminder.getHours(),reminder.getId(),reminder.getCustom_text());
                        }
                    }
                }else{
                    if (reminder != null){
                        Log.d(TAG, "Set a new alarm");
                        int id = getUniqueHabitReminderID(); // assign a new id to habit reminder
                        reminder.setId(id);
                        setReminder(habit.getTitle(),reminder.getMinutes(),reminder.getHours(),id,reminder.getCustom_text());
                    }
                }

                // write habits to firebase
                habit_dbHandler.updateHabit(habit); // update the habit in SQLiteDatabase
                writeHabit_Firebase(habit, user.getUID(), false); // write the habit to the firebase

                // go back to habit view activity
                Intent activityName = new Intent(HabitEditActivity.this, HabitViewActivity.class);
                Bundle extras = new Bundle();
                extras.putString("recorded_habit", habit_serializeToJson(habit));
                activityName.putExtras(extras);
                startActivity(activityName);
                finish();
            }
        });

    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    /**
     *
     * This method is used to record the current habit based on the input field
     *
     * @return Habit This will return the habit
     * */
    public Habit recordCurrentHabit(){
        Habit _habit = habit;

        String name = habit_name.getText().toString(); // retrieve the title of the habit from the input field
        int occur = Integer.parseInt(habit_occur.getText().toString()); // retrieve the occurrence of the habit from the input field

        _habit.setTitle(name);
        _habit.setOccurrence(occur);
        _habit.setPeriod(period[0]);
        _habit.setHolder_color(color[0]);

        return _habit;

    }

    /**
     *
     * This method is used to populate the period button by setting onclickListener on them
     * and it will change the color based on the user's option in period section.
     * @param dialogView This parameter refers to the view
     *
     * @param period This parameter refers to the period list which indicates the chosen period
     * @param period_text This parameter refers to the period TextView in the view  */
    public void populatePeriodBtn(final HabitEditActivity dialogView, final int[] period, final TextView period_text){
        // set listener on buttons to change the color based on the user's option in period section
        for (final int i :Habit.period_buttonIDS){
            final Button btn = dialogView.findViewById(i); // find button in the view
            btn.setBackgroundColor(Color.TRANSPARENT);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = btn.getId(); // retrieve the buttonID

                    for (int i = 0; i < 4; i++){
                        Button _btn = dialogView.findViewById(Habit.period_buttonIDS[i]);
                        if (id == Habit.period_buttonIDS[i]){
                            // if is selected by the user, add a grey background.
                            _btn.setBackgroundColor(Color.parseColor("#dfdfdf"));
                            period_text.setText(Habit.period_textList[i]);  // set the period text
                            period[0] = Habit.period_countList[i];  // update the chosen period
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
     * @param dialogView This parameter refers to the view
     *
     * @param color This parameter refers to the color list which indicates the chosen color */
    public void populateColorBtn(final HabitEditActivity dialogView, final String[] color){
        // set listener on buttons to change the color based on the user's option in color section
        for (final int i : Habit.color_buttonIDS){
            final Button btn = dialogView.findViewById(i);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = btn.getId();

                    for (int i = 0; i < 4; i++){
                        Button _btn = dialogView.findViewById(Habit.color_buttonIDS[i]);
                        if (id == Habit.color_buttonIDS[i]){
                            // if is selected by the user, add a black border surrounding the color button.
                            GradientDrawable drawable = new GradientDrawable();
                            drawable.setShape(GradientDrawable.RECTANGLE);
                            drawable.setStroke(5, Color.BLACK);
                            drawable.setColor(getResources().getColor(Habit.color_schemeIDS[i]));
                            _btn.setBackground(drawable);
                            color[0] = Habit.colorList[i];
                        }else {
                            // if is not selected by the user, remove the black border.
                            _btn.setBackgroundResource(Habit.color_schemeIDS[i]);
                        }
                    }
                }
            });
        }

    }

    /**
     *
     * This method is used to initialise the holder color on the color section based on the habit holder color of the habit
     * @param dialogView This parameter refers to the view
     *
     * @param habit This parameter refers to the habit object.
     * @param color This parameter refers to the color list which indicates the chosen color */
    public void habit_edit_initialise_colorSection(HabitEditActivity dialogView, final Habit habit, final String[] color){
        // initialise the holder color on the color section based on the habit holder color of the habit
        for(int i = 0; i < 4; i++){
            if (Habit.colorList[i].equals(habit.getHolder_color())){ // loop to find matched values
                color[0] = Habit.colorList[i]; // update the chosen holder color
                // a black border will surround the habit's holder color
                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setStroke(5, Color.BLACK);
                drawable.setColor(getResources().getColor(Habit.color_schemeIDS[i]));
                dialogView.findViewById(Habit.color_buttonIDS[i]).setBackground(drawable);
                break;
            }
        }

    }

    /**
     *
     * This method is used to initialise the color of button at period section based on the habit period of the habit
     * @param dialogView This parameter refers to the view
     *
     * @param habit This parameter refers to the habit object.
     * @param period This parameter refers to the period list which indicates the chosen period
     * @param period_text This parameter refers to the period TextView in the view */
    public void habit_edit_initialise_periodSection(HabitEditActivity dialogView, final Habit habit, final int[] period, final TextView period_text){
        // initialise the color of button at period section based on the habit period of the habit
        for(int i = 0; i < 4; i++){
            if (Habit.period_countList[i] == habit.getPeriod()){ // loop to find matches value
                period[0] = Habit.period_countList[i]; // update the chosen period
                period_text.setText(Habit.period_textList[i]); // set the period text
                // set grey background on the period button
                dialogView.findViewById(Habit.period_buttonIDS[i]).setBackgroundColor(getResources().getColor(R.color.colorWhiteGrey));
                break;
            }
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

        WorkManager.getInstance(this).enqueue(mywork);
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
     * This method is used to deserialize to single object. (from Json)
     *
     * @param jsonString This parameter is used to get json string
     *
     * @return String This returns the deserialized Habit object.
     *
     * */
    private Habit deserializeFromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Habit.class);
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
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.setAction("HabitTracker");
        intent.putExtra("Name", name);
        intent.putExtra("id", id);
        intent.putExtra("custom_txt", custom_txt);
        // This initialise the pending intent which will be sent to the broadcastReceiver
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
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.setAction("HabitTracker");
        intent.putExtra("Name", name);
        intent.putExtra("id",id);
        intent.putExtra("custom_txt",custom_txt);
        // fill in the same pending intent as when setting it
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

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
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = sharedPreferences.getInt("alarm_id",-1);
        int _new = ++id;
        editor.putInt("alarm_id", _new);
        Log.d(TAG, "getUniqueHabitReminderID: " + _new);
        editor.apply();
        return id;
    }

    /**
     * Hide soft keyboard for user input task
     */
    private void HideKeyboard() {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert mgr != null;
        mgr.hideSoftInputFromWindow(habit_name.getWindowToken(), 0);
        Log.i(TAG, "Hide Keyboard");
    }

    /**
     *
     * This method is used to send the work request
     *  to the HabitWorker(WorkManager) to do the writing firebase action
     *  when the network is connected.
     *
     * @param habitRepetition This parameter is used to get the habit object
     *
     * @param UID This parameter is used to get the userID
     *
     * */
    public void writeHabitRepetition_Firebase(HabitRepetition habitRepetition, String UID, boolean isDeletion){
        Log.i(TAG, "Uploading to Firebase");

        // set constraint that the network must be connected
        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // put data in a data builder
        Data firebaseUserData = new Data.Builder()
                .putString("ID", UID)
                .putString("habitRepetition", habitRepetition_serializeToJson(habitRepetition))
                .putBoolean("deletion", isDeletion)
                .build();

        // send a work request
        OneTimeWorkRequest mywork =
                new OneTimeWorkRequest.Builder(HabitRepetitionWorker.class)
                        .setConstraints(myConstraints)
                        .setInputData(firebaseUserData)
                        .build();

        WorkManager.getInstance(this).enqueue(mywork);
    }

    /**
     *
     * This method is used to serialize a single object. (into Json String)
     *
     * @param habitRepetition This parameter is used to get the habitRepetition object
     *
     * @return String This returns the serialized object.
     *
     * */
    public String habitRepetition_serializeToJson(HabitRepetition habitRepetition) {
        Gson gson = new Gson();
        Log.i(TAG,"Object serialize");
        return gson.toJson(habitRepetition);
    }

}

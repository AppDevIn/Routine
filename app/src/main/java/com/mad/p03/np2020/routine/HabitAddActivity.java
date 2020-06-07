package com.mad.p03.np2020.routine;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.mad.p03.np2020.routine.Class.AlarmReceiver;
import com.mad.p03.np2020.routine.Class.Habit;
import com.mad.p03.np2020.routine.Class.HabitGroup;
import com.mad.p03.np2020.routine.Class.HabitReminder;
import com.mad.p03.np2020.routine.Class.User;
import com.mad.p03.np2020.routine.background.HabitWorker;
import com.mad.p03.np2020.routine.database.HabitDBHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.String.format;

/**
 *
 * Habit activity used to manage the add habit layout section
 *
 * @author Hou Man
 * @since 02-06-2020
 */


public class HabitAddActivity extends AppCompatActivity {

    private static final String TAG = "AddHabitActivity" ;
    private static final String SHARED_PREFS = "sharedPrefs";
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

    // initialise the dateFormat
    private DateFormat dateFormat;

    // initialise the handler
    private HabitDBHelper habit_dbHandler;

    //User
    private User user;

    // Period and Color
    private int[] period;
    private String[] color;

    // Habit
    private Habit habit;

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
        setContentView(R.layout.add_habit);

        // set the layout in full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // initialise widgets
        menu_count = findViewById(R.id.menu_count);
        habit_name = findViewById(R.id.add_habit_name);
        habit_occur = findViewById(R.id.habit_occurence);
        period_text = findViewById(R.id.period_txt);
        habit_reminder_indicate_text = findViewById(R.id.reminder_indicate_text);
        group_indicate_text = findViewById(R.id.group_indicate_text);
        add_btn = findViewById(R.id.menu_add_count);
        minus_btn = findViewById(R.id.menu_minus_count);
        buttonClose = findViewById(R.id.habit_close);
        buttonOk = findViewById(R.id.create_habit);

        // initialise dateFormat
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        // set the HabitDBHelper
        habit_dbHandler = new HabitDBHelper(this);

        // initialise user
        user = new User();
        user.setUID(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // initialise period section
        period = new int[1]; // this is used to store the chosen period
        populatePeriodBtn(this, period, period_text);

        // initialise color section
        color = new String[1]; // this is used to store the chosen color
        populateColorBtn(this,color);

        // This is to get the habit object from intent bundle and initialise the period and color section
        Intent intent = getIntent();
        if (intent.hasExtra("recorded_habit")){
            habit = deserializeFromJson(intent.getExtras().getString("recorded_habit"));
            setTextField(habit);
            habit_edit_initialise_colorSection(this, habit, color);
            habit_edit_initialise_periodSection(this, habit, period, period_text);
        }else{
            habit = null;
            habit_add_initialise_periodSection(this,period);
            habit_add_initialise_colorSection(this,color);
        }

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
                // go the habit group activity
                Intent activityName = new Intent(HabitAddActivity.this, HabitGroupActivity.class);
                Bundle extras = new Bundle();
                extras.putString("recorded_habit", habit_serializeToJson(recordCurrentHabit()));
                extras.putString("action", "add");
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
                Intent activityName = new Intent(HabitAddActivity.this, HabitReminderActivity.class);
                Bundle extras = new Bundle();
                extras.putString("recorded_habit", habit_serializeToJson(recordCurrentHabit()));
                extras.putString("action", "add");
                activityName.putExtras(extras);
                startActivity(activityName);
                finish();
            }
        });

        // set onClickListener on close button
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go back to habit activity
                Intent activityName = new Intent(HabitAddActivity.this, HabitActivity.class);
                activityName.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(activityName);
                finish();
            }
        });

        // set onClickListener on create button
        buttonOk.setOnClickListener(new View.OnClickListener() {
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
                HabitGroup hg = null;

                if (habit != null){

                    if (habit.getHabitReminder() != null){ // set a reminder if the reminder is in active
                        int id = getUniqueHabitReminderID(); // retrieve the unique habitReminder ID
                        habit.getHabitReminder().setId(id);

                        setReminder(habit);
                        hr = habit.getHabitReminder();

                    }

                    if (habit.getGroup() != null){ // set a new habitGroup object if the user chooses something for the group name
                        hg = habit.getGroup();
                    }
                }

                // create a habit object
                Habit habit = new Habit(name, occur, cnt, period[0], dateFormat.format(date),color[0],hr,hg);

                // insert the habit into SQLiteDatabase
                long habitID = habit_dbHandler.insertHabit(habit, user.getUID());
                // The insert process is success if habit id returned is not equal to 1

                if (habitID != -1){ // if habitID returned is legit
                    habit.setHabitID(habitID); // set the id to the habit
                    writeHabit_Firebase(habit, user.getUID(), false); // write habit to firebase

                    Log.d(TAG, "onClick: "+habit.getHabitID());
                    // toast a message to alert the habit has been created
                    Toast.makeText(HabitAddActivity.this, format("Habit %shas been created.",capitalise(name)), Toast.LENGTH_SHORT).show();
                }


                Intent activityName = new Intent(HabitAddActivity.this, HabitActivity.class);
                activityName.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
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
        String name = habit_name.getText().toString(); // retrieve the title of the habit from the input field
        int occur = Integer.parseInt(habit_occur.getText().toString()); // retrieve the occurrence of the habit from the input field
        int cnt = Integer.parseInt(menu_count.getText().toString()); // retrieve the count of the habit from the input field

        Habit _habit = new Habit(name, occur, cnt, period[0], color[0]);

        if (habit != null){
            if (habit.getGroup() != null){
                _habit.setGroup(habit.getGroup());
            }
            if (habit.getHabitReminder() != null){
                _habit.setHabitReminder(habit.getHabitReminder());
            }
        }

        return _habit;

    }

    /**
     * This method is used to set the text field based on the habit
     *
     * @param habit This is to get the habit object
     * */
    public void setTextField(Habit habit){
        if (!habit.getTitle().equals("")){
            habit_name.setText(habit.getTitle());
        }
        habit_occur.setText(String.valueOf(habit.getOccurrence()));
        menu_count.setText(String.valueOf(habit.getCount()));

        if (habit.getGroup() != null){
            group_indicate_text.setText(habit.getGroup().getGrp_name());
        }

        if (habit.getHabitReminder() != null){
            habit_reminder_indicate_text.setText((format("%02d:%02d",habit.getHabitReminder().getHours(),habit.getHabitReminder().getMinutes()))); // set the timing on the TextView
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
     * This method is used to populate the period button by setting onclickListener on them
     * and it will change the color based on the user's option in period section.
     *  @param dialogView This parameter refers to the view
     *
     * @param period This parameter refers to the period list which indicates the chosen period
     *@param period_text This parameter refers to the period TextView in the view  */
    public void populatePeriodBtn(final HabitAddActivity dialogView, final int[] period, final TextView period_text){
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
     *  @param dialogView This parameter refers to the view
     *
     * @param color This parameter refers to the color list which indicates the chosen color
     * */
    public void populateColorBtn(final HabitAddActivity dialogView, final String[] color){
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
     * This method is used to initialise the color button at color section since nothing is chosen at first
     *  @param dialogView This parameter refers to the view
     *
     * @param color This parameter refers to the color list which indicates the chosen color
     * */
    public void habit_add_initialise_colorSection(final HabitAddActivity dialogView, final String[] color){
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
     * @param dialogView This parameter refers to the view
     * @param period This parameter refers to the period list which indicates the chosen period
     * */
    public void habit_add_initialise_periodSection(final HabitAddActivity dialogView, final int[] period){
        // initialise the color of "daily" button at period section since nothing is chosen at first
        // At default, "daily" period is chosen at first.
        // A grey background will surround the "daily" button.
        period[0] = 1;
        dialogView.findViewById(R.id.daily_period).setBackgroundColor(Color.parseColor("#dfdfdf"));
    }

    /**
     *
     * This method is used to format text by capitalising the first text of each split text
     *
     * @param text This parameter is used to get the text
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

    /**
     *
     * This method is used to initialise the holder color on the color section based on the habit holder color of the habit
     * @param dialogView This parameter refers to the view
     * @param habit This parameter refers to the habit object.
     * @param color This parameter refers to the color list which indicates the chosen color
     *  */
    public void habit_edit_initialise_colorSection(HabitAddActivity dialogView, final Habit habit, final String[] color){
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
     * @param habit This parameter refers to the habit object.
     * @param period This parameter refers to the period list which indicates the chosen period
     * @param period_text This parameter refers to the period TextView in the view
     *   */
    public void habit_edit_initialise_periodSection(HabitAddActivity dialogView, final Habit habit, final int[] period, final TextView period_text){
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
     *
     * This method is used to set the habitReminder.
     *
     * @param habit This parameter is to get the habit object.
     *
     * */
    public void setReminder(Habit habit){
        HabitReminder reminder = habit.getHabitReminder();
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.setAction("HabitTracker");
        intent.putExtra("Name", habit.getTitle());
        intent.putExtra("id", reminder.getId());
        intent.putExtra("custom_txt", reminder.getCustom_text());
        // This initialise the pending intent which will be sent to the broadcastReceiver
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int type = AlarmManager.RTC_WAKEUP;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MINUTE,reminder.getMinutes());
        c.set(Calendar.HOUR_OF_DAY,reminder.getHours());
        c.set(Calendar.SECOND,0);

        if (System.currentTimeMillis() > c.getTimeInMillis()){
            // increment one day to prevent setting for past alarm
            c.add(Calendar.DATE, 1);
        }

        long time = c.getTime().getTime();

        Log.d(TAG, "setReminder for ID "+ reminder.getId() + " at " + c.getTime());
        // AlarmManager set the daily repeating alarm on time chosen by the user.
        // The broadcastReceiver will receive the pending intent on the time.
        am.setRepeating(type, time, AlarmManager.INTERVAL_DAY, pi);
    }
}

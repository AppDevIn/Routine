package com.mad.p03.np2020.routine;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.mad.p03.np2020.routine.Class.AlarmReceiver;
import com.mad.p03.np2020.routine.Class.Habit;
import com.mad.p03.np2020.routine.Class.User;
import com.mad.p03.np2020.routine.background.HabitWorker;
import com.mad.p03.np2020.routine.database.HabitDBHelper;

import static java.lang.String.format;

/**
 *
 * Habit activity used to manage the habit view layout section
 *
 * @author Hou Man
 * @since 02-06-2020
 */

public class HabitViewActivity extends AppCompatActivity {

    private static final String TAG = "HabitViewActivity";

    // Widgets
    private TextView title, cnt, occurrence, cnt2, period;
    private ImageButton reduceCountBtn, addCountBtn, modifyCountBtn, closeBtn, editBtn, deletebtn;
    private LinearLayout habit_view_upper;

    // Habit
    private Habit habit;

    // HabitDBHandler
    private HabitDBHelper habit_dbHandler;

    // User
    private User user;

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
        setContentView(R.layout.habit_view);

        // set the layout in full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // initialise widgets
        title = findViewById(R.id.habit_view_title);
        cnt = findViewById(R.id.habit_view_count);
        reduceCountBtn = findViewById(R.id.habit_view_reduce);
        addCountBtn = findViewById(R.id.habit_view_add);
        modifyCountBtn = findViewById(R.id.habit_view_modify);
        closeBtn = findViewById(R.id.habit_view_close);
        editBtn = findViewById(R.id.habit_view_edit);
        deletebtn = findViewById(R.id.habit_view_delete);
        occurrence = findViewById(R.id.habitOccurence);
        cnt2 = findViewById(R.id.habitCount);
        period = findViewById(R.id.habit_period);
        habit_view_upper = findViewById(R.id.habit_view_upper);

        // initialise habitDBHandler
        habit_dbHandler = new HabitDBHelper(this);

        // set user
        user = new User();
        user.setUID(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // This is to get the habit object from intent bundle
        Intent intent = getIntent();
        if (intent.hasExtra("recorded_habit")){
            habit = deserializeFromJson(intent.getExtras().getString("recorded_habit"));
        }else{
            Log.d(TAG, "LOADING HABIT ERROR ");
        }

        // set text on input fields based on the habit object
        title.setText(habit.getTitle());
        cnt.setText(String.valueOf(habit.getCount()));
        occurrence.setText(String.valueOf(habit.getOccurrence()));
        cnt2.setText(String.valueOf(habit.getCount()));
        period.setText(habit.returnPeriodText(habit.getPeriod()));

        // set the transparent background of the button
        reduceCountBtn.setBackgroundColor(Color.TRANSPARENT);
        addCountBtn.setBackgroundColor(Color.TRANSPARENT);
        modifyCountBtn.setBackgroundColor(Color.TRANSPARENT);
        closeBtn.setBackgroundColor(Color.TRANSPARENT);
        editBtn.setBackgroundColor(Color.TRANSPARENT);
        deletebtn.setBackgroundColor(Color.TRANSPARENT);

        // set the background color of upper habit view as the holder color
        habit_view_upper.setBackgroundColor(getResources().getColor(habit.returnColorID(habit.getHolder_color())));

        // set onClickListener on close button
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activityName = new Intent(HabitViewActivity.this, HabitActivity.class);
                startActivity(activityName);
            }
        });

        // set onClickListener on add count button
        addCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Habit: Add Count");
                // trigger the Habit class add count method
                habit.addCount(); // increase the count by 1
                habit_dbHandler.updateCount(habit); // update the habit data in SQliteDatabase
                writeHabit_Firebase(habit, user.getUID(), false); // write the habit data into firebase
                cnt.setText(String.valueOf(habit.getCount())); // set text on the count TextView
                cnt2.setText(String.valueOf(habit.getCount())); // set text on the count TextView
            }
        });

        // set onClickListener on reduce count button
        reduceCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Habit: Minus Count");
                // trigger the Habit class minus count method
                habit.minusCount(); // minus the count by 1
                habit_dbHandler.updateCount(habit); // update the habit data in SQliteDatabase
                writeHabit_Firebase(habit, user.getUID(), false); // write the habit data into firebase
                cnt.setText(String.valueOf(habit.getCount())); // set text on the count TextView
                cnt2.setText(String.valueOf(habit.getCount())); // set text on the count TextView
            }
        });

        // set onClickListener on modify count button
        modifyCountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Habit: Modify Count");
                // Create an alert dialog (modify count)
                AlertDialog.Builder builder = new AlertDialog.Builder(HabitViewActivity.this); // initialise the builder
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_view_modifycnt_dialog, viewGroup, false); // inflate the view
                builder.setView(dialogView); //set view to the builder
                final AlertDialog alertDialog = builder.create(); // build the alert dialog
                alertDialog.show(); // show the alert dialog (modify count)

                // initialise the widgets
                final TextView dialog_title = dialogView.findViewById(R.id.habit_view_dialog_title);
                final Button cancelBtn = dialogView.findViewById(R.id.cancel_dialog);
                final Button saveBtn = dialogView.findViewById(R.id.save_dialog);
                final EditText dialog_cnt = dialogView.findViewById(R.id.dialog_cnt);

                // set text on the input fields based on the habit
                dialog_title.setText(habit.getTitle());
                dialog_cnt.setHint(cnt.getText().toString());

                // set onClickListener on the cancel button
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss(); // dismiss the alert dialog (modify count)
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
                        // trigger the habit class modify count method
                        habit.modifyCount(dialogCnt); // modify the count
                        habit_dbHandler.updateCount(habit); // update the habit data in SQLiteDatabase
                        writeHabit_Firebase(habit, user.getUID(), false); // write habit data to firebase
                        cnt.setText(String.valueOf(habit.getCount()));  // set text on the count TextView
                        cnt2.setText(String.valueOf(habit.getCount()));  // set text on the count TextView
                        alertDialog.dismiss(); // dismiss the alert dialog (modify count)
                    }
                });
            }
        });

        // set onClickListener on edit habit button
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go the edit habit activity
                Intent activityName = new Intent(HabitViewActivity.this, HabitEditActivity.class);
                Bundle extras = new Bundle();
                extras.putString("recorded_habit", habit_serializeToJson(habit));
                activityName.putExtras(extras);
                startActivity(activityName);
            }
        });

        // set onClickListener on delete habit button
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create an alert dialog (delete habit)
                AlertDialog.Builder builder = new AlertDialog.Builder(HabitViewActivity.this); // initialise the builder of alert dialog
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

                        writeHabit_Firebase(habit, user.getUID(), true); // delete the habit in the firebase

                        // go back to habit activity
                        Intent activityName = new Intent(HabitViewActivity.this, HabitActivity.class);
                        startActivity(activityName);

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
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
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
}

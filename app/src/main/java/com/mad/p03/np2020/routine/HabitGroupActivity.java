package com.mad.p03.np2020.routine;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.mad.p03.np2020.routine.Adapter.HabitGroupAdapter;
import com.mad.p03.np2020.routine.helpers.OnItemClickListener;
import com.mad.p03.np2020.routine.models.Habit;
import com.mad.p03.np2020.routine.models.HabitGroup;
import com.mad.p03.np2020.routine.models.User;
import com.mad.p03.np2020.routine.background.HabitGroupWorker;
import com.mad.p03.np2020.routine.DAL.HabitGroupDBHelper;
<<<<<<< HEAD
=======

/**
 *
 * Habit activity used to manage the habit group layout section
 *
 * @author Hou Man
 * @since 02-06-2020
 */
>>>>>>> master

public class HabitGroupActivity extends AppCompatActivity {

    private static final String TAG = "HabitGroupActivity";

    // HabitGroup RecyclerView and Adapter
    private RecyclerView habitGroupRecyclerView;
    private HabitGroupAdapter groupAdapter;

    // initialise the handler
    private HabitGroupDBHelper group_dbhandler;

    // User
    private User user;

    // Widgets
    private ImageView close;
    private Button cancel;
    private Button create_grp;
    private TextView curr_grp;

    // Habit
    private Habit habit;

    // to record the activity action(add/edit habit)
    private String action;

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
        setContentView(R.layout.habit_group);

        // set the layout in full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // initialise groupDBHandler
        group_dbhandler = new HabitGroupDBHelper(this);

        // initialise widgets
        close = findViewById(R.id.habit_group_view_close);
        cancel = findViewById(R.id.habit_group_view_cancel);
        create_grp = findViewById(R.id.habit_group_view_create_group);
        curr_grp = findViewById(R.id.current_grp);

        // set user
        user = new User();
        user.setUID(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // This is to get the habit object from intent bundle and set the text for the current group
        Intent intent = getIntent();
        if (intent.hasExtra("recorded_habit")){
            habit = deserializeFromJson(intent.getExtras().getString("recorded_habit"));
            if (habit.getGroup() != null){
                curr_grp.setText(habit.getGroup().getGrp_name());
            }
        }else{
            curr_grp.setText("NONE");
        }

        // get the activity action
        if (intent.hasExtra("action")){
            action = intent.getExtras().getString("action");
        }

        // inflate habitGroup RecyclerView
        habitGroupRecyclerView = findViewById(R.id.habit_recycler_view);
        habitGroupRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        // initialise group adapter
        groupAdapter= new HabitGroupAdapter(group_dbhandler.getAllGroups(), this);
        // set groupAdapter on RecyclerView
        habitGroupRecyclerView.setAdapter(groupAdapter);

        // set OnItemClickListener on group adapter
        groupAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // when a holder is clicked

                // retrieve the group of the holder
                HabitGroup chosen_grp = groupAdapter._habitGroupList.get(position);
                habit.setGroup(chosen_grp);

                // go to respective activity based on the action
                Intent activityName = new Intent(HabitGroupActivity.this, HabitAddActivity.class);
                if (action.equals("edit")){
                    activityName = new Intent(HabitGroupActivity.this, HabitEditActivity.class);
                }

                Bundle extras = new Bundle();
                extras.putString("recorded_habit", habit_serializeToJson(habit));
                activityName.putExtras(extras);

                finish();
                startActivity(activityName);
            }
        });

        // set onClickListener on close button
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to respective activity based on the action
                Intent activityName = new Intent(HabitGroupActivity.this, HabitAddActivity.class);
                if (action.equals("edit")){
                    activityName = new Intent(HabitGroupActivity.this, HabitEditActivity.class);
                }
                Bundle extras = new Bundle();
                extras.putString("recorded_habit", habit_serializeToJson(habit));
                activityName.putExtras(extras);

                finish();
                startActivity(activityName);

            }
        });

        // set onClickListener on cancel group button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // cancel the existing group
                habit.setGroup(null);

                // go to respective activity based on the action
                Intent activityName = new Intent(HabitGroupActivity.this, HabitAddActivity.class);
                if (action.equals("edit")){
                    activityName = new Intent(HabitGroupActivity.this, HabitEditActivity.class);
                }

                Bundle extras = new Bundle();
                extras.putString("recorded_habit", habit_serializeToJson(habit));
                activityName.putExtras(extras);

                finish();
                startActivity(activityName);
            }
        });

        // set onClickListener on create group button
        create_grp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                // create an alert dialog (create group)
                AlertDialog.Builder builder = new AlertDialog.Builder(HabitGroupActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.habit_group_create, viewGroup, false); // inflate the view
                builder.setView(dialogView); // set the view of the builder
                final AlertDialog alertDialog = builder.create(); // build the dialog

                // initialise widgets
                final Button cancelBtn = dialogView.findViewById(R.id.group_cancel);
                final Button saveBtn = dialogView.findViewById(R.id.group_save);
                final EditText name = dialogView.findViewById(R.id.creating_group_name);

                name.requestFocus();
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

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

                        if (grp_id != -1) { // check if group id returned is not equal to 1
                            grp.setGrp_id(grp_id); // set the group id to the group object
                            groupAdapter._habitGroupList.add(grp); // add the group to the adapter list
                            groupAdapter.notifyDataSetChanged(); // notify data set has changed
                            writeHabitGroup_Firebase(grp, user.getUID()); // write habitGroup to firebase
                            Toast.makeText(HabitGroupActivity.this, "New group has been created.", Toast.LENGTH_SHORT).show();
                        }

                        alertDialog.dismiss(); // dismiss the dialog (create group)
                    }
                });
                alertDialog.show();
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

        WorkManager.getInstance(this).enqueue(mywork);
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
}

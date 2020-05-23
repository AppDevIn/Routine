package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.p03.np2020.routine.Class.FocusAdapter;
import com.mad.p03.np2020.routine.Class.FocusHolder;
import com.mad.p03.np2020.routine.Class.LocalDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FocusActivity extends AppCompatActivity implements View.OnClickListener, HistoryFragment.OnFragmentInteractionListener, View.OnLongClickListener, View.OnTouchListener {

    SQLiteDatabase myLocalDatabase;

    //Timer widgets
    private Button focusButton; //Button for the timer

    private int tmins, tsecs = 0; //Timer for minutes and seconds

    private String BUTTON_STATE = "EnterTask"; //This button state is used to track the timer button next state

    private ImageButton taskSubmit; //This button is to submit the task that user key
    private EditText taskInput; //User keys in the task

    private TextView min, sec, semicolon, textDisplay; //Display of the timer

    private long mTimeLeftInMillis = 0; //Time left

    private ImageView minup, mindown, secup, secdown, mface; //Button to control the timer

    private boolean bupmin, bdownmin, bupsec, bdownsec; //Button used for event control the timer

    private Handler repeatUpdateHandler = new Handler(); //For long touch view
    private CountDownTimer mCountDownTimer; //Main Counteractive for Focus
    private CountDownTimer eCountDownTimer; //Auto closed for Focus

    //History Widgets
    private String dateTimeTask, currentTask, mCompletion;
    private final String TAG = "Focus";

    //Notification variables
    public static final String CHANNEL_1_ID = "channel1";

    final String title = "You have an ongoing Focus";
    final String message = "Come back now before your Sun becomes depressed!";

    //Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    private ArrayList<FocusHolder> FocusList = new ArrayList<>();

    //Local Database
    LocalDatabase localDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Animation translateAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_anims);
        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
        initialization(); //Process of data

        //ImageButton
        ImageButton imageButton = findViewById(R.id.history);
        focusButton = findViewById(R.id.start);
        taskSubmit = findViewById(R.id.taskSubmit);

        //ImageView
        minup = findViewById(R.id.minUp);
        mindown = findViewById(R.id.minDown);
        secup = findViewById(R.id.secUp);
        secdown = findViewById(R.id.secDown);
        mface = findViewById(R.id.assistant);

        //TextView
        min = findViewById(R.id.mins);
        sec = findViewById(R.id.secs);
        semicolon = findViewById(R.id.semicolon);
        textDisplay = findViewById(R.id.diplayText);
        taskInput = findViewById(R.id.taskInput);

        //SetOnclickListener
        imageButton.setOnClickListener(this);
        minup.setOnClickListener(this);
        secdown.setOnClickListener(this);
        secup.setOnClickListener(this);
        mindown.setOnClickListener(this);
        focusButton.setOnClickListener(this);
        taskSubmit.setOnClickListener(this);

        //SetLongClickListener
        minup.setOnLongClickListener(this);
        secdown.setOnLongClickListener(this);
        secup.setOnLongClickListener(this);
        mindown.setOnLongClickListener(this);

        //SetLongClickListener
        minup.setOnTouchListener(this);
        secdown.setOnTouchListener(this);
        secup.setOnTouchListener(this);
        mindown.setOnTouchListener(this);

        mface.startAnimation(translateAnimation);
    }

    //Add to local data from firebase to local database
    private void initDatabase(List<FocusHolder> focusList) {
        for (FocusHolder item : focusList) {
            localDatabase.addData(item);
        }
    }

    private void addLocalDatabase(FocusHolder focus){
        localDatabase.addData(focus);
    }

    private void initialization() {
        //How this intialization will work
        //The data will be pulled from the local database and update to the firebase if user is already signed in

        localDatabase = new LocalDatabase(FocusActivity.this);
        tmins = 0;
        tsecs = 0;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        FirebaseDatabase();

        //Add it to LocalDatabase List
        FocusList = localDatabase.getAllData();
        Log.v(TAG, "Local database: " + localDatabase.getAllData().toString());
    }

    //Running, Fail, Success, Reset
    //This function is to track the button state so that it can show its respective view
    private void focusTime() {
        switch (BUTTON_STATE) {
            case "EnterTask": //Enter Task view where user can enter its view
                ShowKeyboard();
                taskInput.setText("");
                taskSubmit.setVisibility(View.VISIBLE);
                break;
            case "Reset": //Reset view where the view will become its inititate state
                textDisplay.setText("What are you focusing today?");
                timerReset();
                BUTTON_STATE = "EnterTask";
                break;

            case "Running":
                textDisplay.setText("While you are at it, Keep fighting!");
                timeRunner();
                long totaltime = (tmins * 60) + tsecs;
                long millisInput = totaltime * 1000;
                Log.v(TAG, String.valueOf(millisInput));
                BUTTON_STATE = "Fail";
                setTime(millisInput);
                break;

            case "Success":
                textDisplay.setText("Yayyyy! You have reach your goal!");
                timerSuccess();
                BUTTON_STATE = "Reset";
                mCompletion = "True";
                FocusHolder focusViewHolder = new FocusHolder(currentTask, dateTimeTask, tmins + " Minutes", mCompletion);
                addLocal(focusViewHolder);
                break;

            case "Fail":
                textDisplay.setText("Thanks for giving your best!");
                timerFail();
                mCountDownTimer.cancel(); //Pause timer
                BUTTON_STATE = "Reset";
                mCompletion = "False";
                FocusHolder focusHolder = new FocusHolder(currentTask, dateTimeTask, tmins + " Minutes", mCompletion);
                addLocal(focusHolder);
                break;
        }
    }

    //Local Database
    private void addLocal(FocusHolder focusHolder){
        localDatabase.addData(focusHolder);
        FocusList.add(focusHolder);
        writeFirebase(focusHolder);
    }

    //Used for update button sequencing (timer)
    private void timeRunner() { //Timer running
        BUTTON_STATE = "Running";

        focusButton.setText(R.string.StopTimer);
        minup.setVisibility(View.INVISIBLE);
        mindown.setVisibility(View.INVISIBLE);
        secup.setVisibility(View.INVISIBLE);
        secdown.setVisibility(View.INVISIBLE);
        mface.setImageResource(R.drawable.ic_focus_ast_down);
    }

    private void timerFail() { //Give up
        BUTTON_STATE = "Fail";

        sec.setTextColor(Color.RED);
        min.setTextColor(Color.RED);
        semicolon.setTextColor(Color.RED);
        focusButton.setText("Try Again");
        mface.setImageResource(R.drawable.ic_focus_ast_sad);
    }

    private void timerSuccess() { //Timer hits 0
        BUTTON_STATE = "Success";

        sec.setTextColor(Color.parseColor("#CAEFD1"));
        min.setTextColor(Color.parseColor("#CAEFD1"));
        semicolon.setTextColor(Color.parseColor("#CAEFD1"));
        focusButton.setText("Restart");
        mface.setImageResource(R.drawable.ic_focus_ast_happy);
    }

    private void timerReset() { //Resetting to state
        BUTTON_STATE = "Reset";

        sec.setTextColor(Color.BLACK);
        min.setTextColor(Color.BLACK);
        semicolon.setTextColor(Color.BLACK);
        focusButton.setText(R.string.startTimer);
        mface.setImageResource(R.drawable.focus_ast);

        minup.setVisibility(View.VISIBLE);
        mindown.setVisibility(View.VISIBLE);
        secup.setVisibility(View.VISIBLE);
        secdown.setVisibility(View.VISIBLE);

        min.setText("00");
        sec.setText("00");
        tsecs = 0;
        tmins = 0;
        focusButton.setText("Start");
    }

    private void setTime(long milliseconds) {
        StartTimer(milliseconds);
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        Log.v(TAG, "Counting down");
        min.setText(String.format("%02d", minutes));
        sec.setText(String.format("%02d", seconds));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId() /*to get clicked view id**/) {
            case R.id.history: //Open history page
                openHistory("History");
                Log.v(TAG, "Open History Page");
                break;
            case R.id.minUp: //Increment Minutes
                tmins = increment(tmins, "min");
                min.setText(String.format("%02d", tmins));
                Log.v(TAG, "Increase Minutes: " + tmins);
                break;
            case R.id.minDown:  //Decrement Minutes
                tmins = decrement(tmins, "min");
                min.setText(String.format("%02d", tmins));
                Log.v(TAG, "Decrease Minutes: " + tmins);
                break;
            case R.id.secUp:    //Increment seconds
                tsecs = increment(tsecs, "sec");
                sec.setText(String.format("%02d", tsecs));
                Log.v(TAG, "Increase Seconds:" + tsecs);
                break;
            case R.id.secDown:  //Decrement seconds
                tsecs = decrement(tsecs, "sec");
                sec.setText(String.format("%02d", tsecs));
                Log.v(TAG, "Decrease Seconds:" + tsecs);
                break;
            case R.id.start:
                //this button has 4 types: Start, Give up, Try Again, Restart, Enter Task
                focusTime();
                break;
            case R.id.taskSubmit:
                HideKeyboard();
                currentTask = taskInput.getText().toString();
                dateTimeTask = new SimpleDateFormat("dd/MM/yyyy, HH:mm").format(new Date());
                Log.v(TAG, "Task: " + currentTask + " Date: " + dateTimeTask);
                BUTTON_STATE = "Running";
                taskSubmit.setVisibility(View.INVISIBLE);
                focusTime();
                break;
        }
    }

    //This onLongClick Listener purpose is for user to increase the timer recursively
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId() /*to get clicked view id**/) {
            case R.id.minUp:
                bupmin = true;
                break;
            case R.id.minDown:
                bdownmin = true;
                break;
            case R.id.secUp:
                bupsec = true;
                break;
            case R.id.secDown:
                bdownsec = true;
                break;
        }
        repeatUpdateHandler.post(new RptUpdater());
        return false;
    }

    public boolean touchRelease(boolean btimer, MotionEvent event) { //On release hold of timer
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL && btimer) {
            return false;
        }
        return false;
    }

    class RptUpdater implements Runnable {
        public void run() {
            if (bupmin) {
                tmins = increment(tmins, "min");
                min.setText(String.format("%02d", tmins));
            } else if (bdownmin) {
                tmins = decrement(tmins, "min");
                min.setText(String.format("%02d", tmins));
            } else if (bupsec) {
                tsecs = increment(tsecs, "sec");
                sec.setText(String.format("%02d", tsecs));
            } else if (bdownsec) {
                tsecs = decrement(tsecs, "sec");
                sec.setText(String.format("%02d", tsecs));
            }
            repeatUpdateHandler.postDelayed(new RptUpdater(), 150);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId() /*to get clicked view id**/) {
            case R.id.minUp:
                bupmin = touchRelease(bupmin, event);
                break;
            case R.id.minDown:
                bdownmin = touchRelease(bdownmin, event);
                break;
            case R.id.secUp:
                bupsec = touchRelease(bupsec, event);
                break;
            case R.id.secDown:
                bdownsec = touchRelease(bdownsec, event);
                break;
        }
        return false;
    }

    //Event State
    @Override
    protected void onPause() {
        super.onPause();

        //If user exited the app, notification is pushed
        //Within 10 seconds, it will automatically count as fail

        if (BUTTON_STATE.equals("Fail")) {
            createNotification(); //Notification pushed
            eCountDownTimer = new CountDownTimer(10000, 1000) { //timer countdown start
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.v(TAG, "Time left for user to entry before auto destroy: " + millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    focusButton.callOnClick(); //Simulates the button onclick to assume the failure of the task after 10 seconds
                    Log.v(TAG, "Destroyed");
                }
            }.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (eCountDownTimer != null) {
            eCountDownTimer.cancel();
            Log.v(TAG, "Resume");
        }
    }

    //History Fragment
    public void openHistory(String text) { //Open history tab
        HistoryFragment fragmentfocus = HistoryFragment.newInstance(FocusList);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.fragment_container, fragmentfocus, "HISTORY FRAGMENT").commit();
    }

    @Override
    public void onFragmentInteraction() {
        onBackPressed();
    }

    //Decrement time
    public int decrement(int tChill, String type) { //Increment method for timer
        if (type.equals("min")) {
            if (tChill != 0) tChill--;
        } else {
            if (tChill != 0) {
                tChill -= 5;
            } else {
                tChill = 55;
            }
        }
        return tChill;
    }

    //Increment time
    public int increment(int tChill, String type) { //Decrement method for timer
        if (type.equals("min")) {
            tChill++;
        } else {
            if (tChill != 55) {
                tChill += 5;
            } else {
                tChill = 0;
            }
        }
        return tChill;
    }

    //Start Timer for focus
    private void StartTimer(long TimeLeftInMillis) {
        Log.v(TAG, "Timer Start");

        mCountDownTimer = new CountDownTimer(TimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.v(TAG, "OnTick: " + millisUntilFinished);
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                Log.v(TAG, "Completed");
                BUTTON_STATE = "Success";
                focusTime();
            }
        }.start();
    }

    //Notification
    private void createNotification() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) { //API level for Kitkat
            Intent intent = new Intent(this, FocusActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(FocusActivity.this, (int) System.currentTimeMillis(), intent, 0);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification noti = new Notification.Builder(FocusActivity.this).setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.ic_launcher_foreground).setContentIntent(pIntent).build();
            notificationManager.notify(0, noti);
            Log.v("Notification", "Pushed");
        } else { //API level for other than kitkat
            //Creation Channel
            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Focus");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            sendChannel1();
        }

    }

    //Send notification to channel 1, used for api above 24
    private void sendChannel1() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), FocusActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID).setContentIntent(pIntent).setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle(title).setContentText(message).setPriority(NotificationCompat.PRIORITY_HIGH).setCategory(NotificationCompat.CATEGORY_MESSAGE).build();
        notificationManager.notify(1, notification);

    }

    //Firebase
    private void FirebaseDatabase() { //Firebase Reference
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("FocusData");
    }

    // Read from the database
    private List<FocusHolder> readFirebase(){
        final List<FocusHolder> focusList = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    FocusHolder focus = singleSnapshot.getValue(FocusHolder.class);
                    focusList.add(focus);
                    Log.v(TAG, "Adding each list");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });

        return focusList;
    }

    //Write to firebase
    private void writeFirebase(FocusHolder focusHolder) {
        myRef.child(String.valueOf(FocusList.size() + 1)).setValue(focusHolder);
    }

    //Soft Keyboard methods
    private void ShowKeyboard() {
        taskInput.setVisibility(View.VISIBLE);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(taskInput, InputMethodManager.SHOW_IMPLICIT);
    }

    private void HideKeyboard() {
        taskInput.setVisibility(View.INVISIBLE);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(taskInput.getWindowToken(), 0);
    }
}

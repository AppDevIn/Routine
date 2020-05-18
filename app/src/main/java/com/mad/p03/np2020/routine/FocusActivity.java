package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.p03.np2020.routine.Class.Focus;
import com.mad.p03.np2020.routine.Class.FocusAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FocusActivity extends AppCompatActivity implements View.OnClickListener, historyfocus.OnFragmentInteractionListener, View.OnLongClickListener, View.OnTouchListener {

    private ImageButton taskSubmit;
    private Button startTimer;
    private String dateTimeTask, currentTask, mCompletion;

    private TextView min, sec, semicolon, textDisplay;
    private ImageView minup, mindown, secup, secdown, mface;

    private EditText taskInput;

    private int tmins, tsecs = 0;
    private boolean bupmin, bdownmin, bupsec, bdownsec;

    private Handler repeatUpdateHandler = new Handler(); //For long touch view
    private CountDownTimer mCountDownTimer; //Main Countdowntimer for Focus
    private CountDownTimer eCountDownTimer; //Auto closed for Focus

    private boolean mAutoIncrement = false;
    private boolean mAutoDecrement = false;
    private String BUTTON_STATE = "EnterTask";

    private long mTimeLeftInMillis = 0;

    private final String TAG = "Focus";

    //Notfication variables
    public static final String CHANNEL_1_ID = "channel1";

    String title = "You have an ongoing Focus";
    String message = "Come back now before your Sun becomes depressed!";

    //Firebase Notification
    private FirebaseDatabase mDatabase;
    private DatabaseReference myRef;

    private ArrayList<Focus> focusList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Animation translateAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_anims);
        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
        intializtion(); //Process of data

        //ImageButton
        ImageButton imageButton = findViewById(R.id.history);
        startTimer = findViewById(R.id.start);
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
        startTimer.setOnClickListener(this);
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

    private void focusTime() {
        //Running, Fail, Success, Reset
        switch (BUTTON_STATE) {
            case "EnterTask":
                ShowKeyboard();
                taskInput.setText("");
                taskSubmit.setVisibility(View.VISIBLE);
                break;
            case "Reset":
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
                writeFirebase(currentTask, dateTimeTask, tmins + " Minutes", mCompletion);
                break;

            case "Fail":
                textDisplay.setText("Thanks for giving your best!");
                timerFail();
                mCountDownTimer.cancel(); //Pause timer
                BUTTON_STATE = "Reset";
                mCompletion = "False";
                writeFirebase(currentTask, dateTimeTask, tmins + " Minutes", mCompletion);
                break;
        }
    }

    //Used for update button sequencing (timer)

    private void timeRunner() { //Timer running
        BUTTON_STATE = "Running";

        startTimer.setText(R.string.StopTimer);
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
        startTimer.setText("Try Again");
        mface.setImageResource(R.drawable.ic_focus_ast_sad);
    }

    private void timerSuccess() { //Timer hits 0
        BUTTON_STATE = "Success";

        sec.setTextColor(Color.parseColor("#CAEFD1"));
        min.setTextColor(Color.parseColor("#CAEFD1"));
        semicolon.setTextColor(Color.parseColor("#CAEFD1"));
        startTimer.setText("Restart");
        mface.setImageResource(R.drawable.ic_focus_ast_happy);
    }

    private void timerReset() { //Resetting to state
        BUTTON_STATE = "Reset";

        sec.setTextColor(Color.BLACK);
        min.setTextColor(Color.BLACK);
        semicolon.setTextColor(Color.BLACK);
        startTimer.setText(R.string.startTimer);
        mface.setImageResource(R.drawable.focus_ast);

        minup.setVisibility(View.VISIBLE);
        mindown.setVisibility(View.VISIBLE);
        secup.setVisibility(View.VISIBLE);
        secdown.setVisibility(View.VISIBLE);

        min.setText("00");
        sec.setText("00");
        tsecs = 0;
        tmins = 0;
        startTimer.setText("Start");
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
    public void onFragmentInteraction() {
        onBackPressed();
    }

    @Override
    public boolean onLongClick(View v) { //Hold down the button
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

    @Override
    protected void onPause() {
        super.onPause();
        if (BUTTON_STATE.equals("Fail")) {
            createNotification();
            eCountDownTimer = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.v(TAG, "Time left for user to entry before auto destroy: " + millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    startTimer.callOnClick();
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

    public boolean touchRelease(boolean btimer, MotionEvent event) { //On release hold of timer
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL && btimer) {
            return false;
        }
        return false;
    }

    public void openHistory(String text) { //Open history tab
        historyfocus fragmentfocus = historyfocus.newInstance(focusList);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.fragment_container, fragmentfocus, "HISTORY FRAGMENT").commit();
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

    private void intializtion() {
        tmins = 0;
        tsecs = 0;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        FirebaseDatabase();
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) { //API level for Kitkat
            Intent intent = new Intent(this, FocusActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(FocusActivity.this, (int) System.currentTimeMillis(), intent, 0);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification noti = new Notification.Builder(FocusActivity.this).setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.ic_launcher_foreground).setContentIntent(pIntent).build();
            notificationManager.notify(0, noti);
            Log.v("Notification", "Pushed");
        } else { //API level for others
            //Creation Channel
            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Focus");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            sendtoChannel1();
        }

    }

    private void sendtoChannel1() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), FocusActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID).setContentIntent(pIntent).setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle(title).setContentText(message).setPriority(NotificationCompat.PRIORITY_HIGH).setCategory(NotificationCompat.CATEGORY_MESSAGE).build();
        notificationManager.notify(1, notification);

    }

    //Firebase
    private void FirebaseDatabase(){ //Firebase Reference
        mDatabase = FirebaseDatabase.getInstance();
        myRef = mDatabase.getReference("FocusData");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                focusList.clear();
                for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){
                    Focus focus = singleSnapshot.getValue(Focus.class);
                    focusList.add(focus);
                    Log.v(TAG, "Adding each list");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }

    private void writeFirebase(String mTask, String mDateTimeTask, String mDuration, String mCompletion) {
        Focus user = new Focus(mDateTimeTask, mDuration, mTask, mCompletion);
        myRef.child(String.valueOf(focusList.size() + 1)).setValue(user);
    }


    private void ShowKeyboard(){
        taskInput.setVisibility(View.VISIBLE);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(taskInput, InputMethodManager.SHOW_IMPLICIT);
    }

    private void HideKeyboard(){
        taskInput.setVisibility(View.INVISIBLE);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(taskInput.getWindowToken(), 0);
    }
}

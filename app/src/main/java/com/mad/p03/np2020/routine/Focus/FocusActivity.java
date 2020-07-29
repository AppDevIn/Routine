package com.mad.p03.np2020.routine.Focus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.mad.p03.np2020.routine.Focus.DAL.AchievementDBHelper;
import com.mad.p03.np2020.routine.Focus.Fragment.AchievementFragment;
import com.mad.p03.np2020.routine.Habit.HabitActivity;
import com.mad.p03.np2020.routine.NavBarHelper;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Focus.Interface.FocusDBObserver;
import com.mad.p03.np2020.routine.Focus.Model.Achievement;
import com.mad.p03.np2020.routine.models.CircularProgressBar;
import com.mad.p03.np2020.routine.Focus.Model.Focus;
import com.mad.p03.np2020.routine.models.User;
import com.mad.p03.np2020.routine.Focus.Fragment.HistoryFragment;
import com.mad.p03.np2020.routine.background.BoundService;
import com.mad.p03.np2020.routine.background.FocusWorker;
import com.mad.p03.np2020.routine.Focus.DAL.FocusDBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.lang.String.format;
import static java.lang.String.valueOf;


/**
 * Focus activity used to manage the focus layout section
 *
 * @author Lee Quan Sheng
 * @since 02-06-2020
 */


public class FocusActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnClickListener, HistoryFragment.OnFragmentInteractionListener, AchievementFragment.OnFragmentInteractionListener, View.OnLongClickListener, View.OnTouchListener, LifecycleObserver, FocusDBObserver {


    //Fragment Variables
    HistoryFragment fragmentFocus;
    AchievementFragment fragmentAchievements;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    //Cloud storage
    private StorageReference mStorageRef;

    AchievementFragment fragmentAchievement;

    public CircularProgressBar circularProgressBar;
    public CardView cdView;
    /**
     * Button for timer
     */
    public Button focusButton;
    public ImageButton imageButton;

    /**
     * Timer for minutes and seconds
     */
    private int thours, tmins, tsecs = 0;
    private long timeTaken = 0;
    /**
     * This button state is used to track the timer button next state
     */
    private String BUTTON_STATE = "EnterTask";

    /**
     * This button is to submit the task that user key
     */
    private ImageButton taskSubmit; //

    /**
     * EditText for User to enter in the task
     */
    private EditText taskInput; //

    /**
     * TextView on the display of the timer
     */
    private TextView hour, min, sec, semicolon, textDisplay, semicolon1;

    /**
     * Used to track the timer left for Focus
     */
    private long mTimeLeftInMillis = 0;
    long millisInput = 0;
    int totalTimeSeconds = 0;

    /**
     * Button to control the timer
     */
    private ImageView hourup, hourdown, minup, mindown, secup, secdown, mface;

    /**
     * Button used for event control the timer
     */
    private boolean bupmin, bdownmin, bupsec, bdownsec, buphour, bdownhour;

    private Handler repeatUpdateHandler = new Handler(); //For long touch view
    private CountDownTimer mCountDownTimer; //Main Counteractive for Focus
    private CountDownTimer eCountDownTimer; //Auto closed for Focus

    //History Widgets
    private String dateTimeTask, currentTask, mCompletion;
    private final String TAG = "Focus";

    /**
     * Notification channel ID is set to channel 1
     */
    private static final String CHANNEL_1_ID = "channel1";

    final String title = "You have an ongoing Focus";
    final String message = "Come back now before your Sun becomes depressed!";

    //Firebase
    private DatabaseReference myRef;

    //User
    private User user = new User();

    //Local Database
    FocusDBHelper focusDBHelper;
    AchievementDBHelper achievementDBHelper;

    //Power Manager
    PowerManager pm;

    //BoundService
    BoundService boundService;
    boolean mServiceBound = false;

    //Keyboard
    boolean keyboardShow = false;

    //Focus item selector
    PopupWindow changeStatusPopUp;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        Log.v(TAG, FirebaseAuth.getInstance().getCurrentUser().getUid());
        user.setUID(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Animation translateAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_anims);
        initialization(); //Process of data

        //ImageButton
        imageButton = findViewById(R.id.history);
        focusButton = findViewById(R.id.start);
        taskSubmit = findViewById(R.id.taskSubmit);

        //ImageView
        minup = findViewById(R.id.minUp);
        mindown = findViewById(R.id.minDown);
        secup = findViewById(R.id.secUp);
        secdown = findViewById(R.id.secDown);
        hourup = findViewById(R.id.HourUp);
        hourdown = findViewById(R.id.HourDown);
        mface = findViewById(R.id.assistant);

        //TextView
        hour = findViewById(R.id.Hours);
        min = findViewById(R.id.Mins);
        sec = findViewById(R.id.secs);
        semicolon = findViewById(R.id.semicolon);
        semicolon1 = findViewById(R.id.semicolon1);
        textDisplay = findViewById(R.id.displayText);
        taskInput = findViewById(R.id.taskInput);

        //CardView
        cdView = findViewById(R.id.cdView);

        //CustomCircularProgressBar
        circularProgressBar = findViewById(R.id.custom_progressBar);

        //SetOnclickListener
        imageButton.setOnClickListener(this);
        minup.setOnClickListener(this);
        secdown.setOnClickListener(this);
        secup.setOnClickListener(this);
        mindown.setOnClickListener(this);
        hourup.setOnClickListener(this);
        hourdown.setOnClickListener(this);
        focusButton.setOnClickListener(this);
        taskSubmit.setOnClickListener(this);

        //SetLongClickListener
        hourdown.setOnLongClickListener(this);
        hourup.setOnLongClickListener(this);
        minup.setOnLongClickListener(this);
        secdown.setOnLongClickListener(this);
        secup.setOnLongClickListener(this);
        mindown.setOnLongClickListener(this);

        //SetLongClickListener
        hourdown.setOnTouchListener(this);
        hourup.setOnTouchListener(this);
        minup.setOnTouchListener(this);
        secdown.setOnTouchListener(this);
        secup.setOnTouchListener(this);
        mindown.setOnTouchListener(this);

        taskInput.setOnFocusChangeListener(this);

        mface.startAnimation(translateAnimation);
        circularProgressBar.startAnimation(translateAnimation);
        circularProgressBar.setProgress(0);
        circularProgressBar.setColor(Color.GREEN);
        circularProgressBar.setMin(0);
        circularProgressBar.setMax(100);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.v("Permission", "Code: " + requestCode);
        switch (requestCode) {
            case 2909: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Granted");
                    user.getAchievement(this);
                } else {
                    Log.e("Permission", "Denied");
                }
                return;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mServiceConnection != null) {
            unbindService(mServiceConnection);
        }

    }

    /**
     * Initialization the focusActivity
     * <p>
     * Initialize minutes, seconds back to zero
     * Get Firebase Data
     * Get Local Database Data
     * Initialize object
     */
    private void initialization() {

        Intent intent = new Intent(getApplicationContext(), BoundService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        focusDBHelper = new FocusDBHelper(FocusActivity.this);
        user.readFocusFirebase(this, this);
        focusDBHelper.registerDbObserver(this);


        Log.v(TAG, "Focus DB Helper Contains " + focusDBHelper.getAllMainData());


        tmins = 0;
        tsecs = 0;
        thours = 0;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        //Add it to LocalDatabase List
        Log.v(TAG, "Local database: " + focusDBHelper.getAllMainData().toString());
        user.getAchievement(this);

        mInstance = this;
        // addObserver of user
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

        FocusActivity.getInstance().setOnVisibilityChangeListener(value -> {
            Log.d("isAppInBackground", String.valueOf(value));

            if (value) {
                if (BUTTON_STATE.equals("Fail")) {
                    if (Build.VERSION.SDK_INT >= 20) {
                        if (pm.isInteractive()) {
                            userQuitApp();
                        }
                    } else {
                        if (pm.isScreenOn()) {
                            userQuitApp();
                        }
                    }
                }
            }
        });

        //Download image from google storage
        user.setAchievementDBHelper(new AchievementDBHelper(FocusActivity.this));
        user.renewAchievementList();

        Log.v(TAG, "Achievement list " + user.getAchievementArrayList());

    }

    //
    //This function is to track the button state so that it can show its respective view

    /**
     * Type of button state: Running, Fail, Success, Reset
     *
     * @Param BUTTON_STATE Running, Fail, Success Reset
     * <p>
     * This is used to trace the button state to display the respective view
     */
    private void focusTime() {
        switch (BUTTON_STATE) {
            case "EnterTask": //Enter Task view where user can enter its view
                Log.v(TAG, "Button Enter Task is clicked");
                if (tsecs == 0 && tmins == 0 && thours == 00) {
                    textDisplay.setText(R.string.FAIL_TIMER);
                    textDisplay.setTextColor(ContextCompat.getColor(this, R.color.chineseRed));
                } else {
                    textDisplay.setTextColor(ContextCompat.getColor(this, R.color.black));
                    ShowKeyboard();
                    taskInput.setText("");
                }
                break;

            case "Reset": //Reset view where the view will become its initiate state
                Log.v(TAG, "Button Reset Task is clicked");
                circularProgressBar.setProgressWithAnimation(0);
                circularProgressBar.setColor(Color.YELLOW);
                textDisplay.setText(R.string.REST_STATUS);
                timerReset();
                BUTTON_STATE = "EnterTask";
                break;

            case "Running":
                Log.v(TAG, "Button Running Task is clicked");
                textDisplay.setText(R.string.PROCESS_STATUS);
                circularProgressBar.setColor(Color.BLUE);
                timeRunner();
                long totaltime = (thours * 60 * 60) + (tmins * 60) + tsecs;
                millisInput = totaltime * 1000;
                timeTaken = millisInput;
                Log.v(TAG, valueOf(millisInput));
                BUTTON_STATE = "Fail";
                StartTimer(millisInput);
                break;

            case "Success":
                Log.v(TAG, "Button Sucess Task is clicked");
                circularProgressBar.setColor(Color.GREEN);
                circularProgressBar.setProgressWithAnimation(100);
                hour.setText("00");
                min.setText("00");
                sec.setText("00");
                textDisplay.setText(R.string.SUCCESS_STATUS);
                timerSuccess();
                BUTTON_STATE = "Reset";
                mCompletion = "True";
                totalTimeSeconds = (thours * 60 * 60) + (tmins * 60) + tsecs;
                String dateSuccess = new SimpleDateFormat("ddMMyyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                Focus focusViewHolder = new Focus(dateTimeTask, String.valueOf(totalTimeSeconds), currentTask, mCompletion, timeTaken);
                focusViewHolder.setFbID(dateSuccess);
                writeToDatabase(focusViewHolder);
                break;

            case "Fail":
                Log.v(TAG, "Button Fail Task is clicked");
                circularProgressBar.setColor(Color.RED);

                textDisplay.setText(R.string.FAIL_STATUS);
                timerFail();
                timeTaken = timeTaken - mTimeLeftInMillis;
                mCountDownTimer.cancel(); //Pause timer
                BUTTON_STATE = "Reset";
                mCompletion = "False";
                totalTimeSeconds = (thours * 60 * 60) + (tmins * 60) + tsecs;

                String dateFail = new SimpleDateFormat("ddMMyyyy HH:mm:ss", Locale.getDefault()).format(new Date());
                Focus focus = new Focus(dateTimeTask, String.valueOf(totalTimeSeconds), currentTask, mCompletion, timeTaken);
                focus.setFbID(dateFail);
                writeToDatabase(focus);
                break;
        }
    }

    /**
     * Write to local Database
     *
     * @Param Focus passed in the new focus object to local database
     */
    private void writeToDatabase(Focus focus) {
        focusDBHelper.addData(focus); //Add to database
        user.renewFocusList();
        writeDataFirebase(focus);
    }


    /**
     * Used for update button sequencing (timer)
     */
    private void timeRunner() { //Timer running
        BUTTON_STATE = "Running";

        focusButton.setText(R.string.StopTimer);

        hourup.setVisibility(View.INVISIBLE);
        hourdown.setVisibility(View.INVISIBLE);
        minup.setVisibility(View.INVISIBLE);
        mindown.setVisibility(View.INVISIBLE);
        secup.setVisibility(View.INVISIBLE);
        secdown.setVisibility(View.INVISIBLE);

        mface.setImageResource(R.drawable.ic_focus_ast_down);
    }

    /**
     * Update each view to a fail view
     * <p>
     * If timer doesnt hit 0, user clicks on button_state on Give Up
     */
    private void timerFail() { //Give up
        BUTTON_STATE = "Fail";

        sec.setTextColor(Color.RED);
        min.setTextColor(Color.RED);
        hour.setTextColor(Color.RED);
        semicolon1.setTextColor(Color.RED);
        semicolon.setTextColor(Color.RED);
        focusButton.setText(R.string.BUTTON_FAIL);
        mface.setImageResource(R.drawable.ic_focus_ast_sad);
    }

    /**
     * Update each view to a success view
     * <p>
     * If timer hits 0, it will execute
     */
    private void timerSuccess() { //Timer hits 0
        BUTTON_STATE = "Success";

        hour.setTextColor(Color.parseColor("#228B22"));
        sec.setTextColor(Color.parseColor("#228B22"));
        min.setTextColor(Color.parseColor("#228B22"));
        semicolon.setTextColor(Color.parseColor("#228B22"));
        semicolon1.setTextColor(Color.parseColor("#228B22"));

        focusButton.setText(R.string.BUTTON_RESTART);
        mface.setImageResource(R.drawable.ic_focus_ast_happy);
    }

    /**
     * Reset the text of the timer
     * <p>
     * Will be executed when the task has ended
     */
    private void timerReset() { //Resetting to state
        BUTTON_STATE = "Reset";

        sec.setTextColor(Color.BLACK);
        min.setTextColor(Color.BLACK);
        hour.setTextColor(Color.BLACK);

        semicolon.setTextColor(Color.BLACK);
        semicolon1.setTextColor(Color.BLACK);


        focusButton.setText(R.string.startTimer);
        mface.setImageResource(R.drawable.focus_ast);

        minup.setVisibility(View.VISIBLE);
        mindown.setVisibility(View.VISIBLE);
        secup.setVisibility(View.VISIBLE);
        secdown.setVisibility(View.VISIBLE);
        hourup.setVisibility(View.VISIBLE);
        hourdown.setVisibility(View.VISIBLE);

        min.setText(R.string.timer_ground);
        sec.setText(R.string.timer_ground);
        hour.setText("00");
        tsecs = 0;
        tmins = 0;
        thours = 0;
        focusButton.setText(R.string.BUTTON_START);
    }


    /**
     * Update the text of the timer
     * <p>
     * Will be executed every click on the increment or decrement button
     */
    private void updateCountDownText() {

        int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
        int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        Log.v(TAG, "Counting down");
        Log.v(TAG, String.valueOf(mTimeLeftInMillis));
        Log.v(TAG, String.valueOf(millisInput));
        long percentage = (long) ((float) mTimeLeftInMillis / millisInput * 100);

        hour.setText(format(Locale.US, "%02d", hours));
        min.setText(format(Locale.US, "%02d", minutes));
        sec.setText(format(Locale.US, "%02d", seconds));

        circularProgressBar.setProgress(100 - percentage);


    }

    /***
     *
     * OnClick event listener for each button on the Focus activity
     *
     * @param v Passing the current view to this content
     */
    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history: //Open history page
                Log.v(TAG, "Open menu");
                int[] location = new int[2];

                // Get the x, y location and store it in the location[] array
                // location[0] = x, location[1] = y.
                v.getLocationOnScreen(location);

                //Initialize the Point with x, and y positions
                Point point = new Point();
                point.x = location[0];
                point.y = location[1];

                showStatusPopup(FocusActivity.this, point);


                break;

            case R.id.HourUp: //Increment hours
                thours = increment(thours, "hours");
                hour.setText(format(Locale.US, "%02d", thours));
                Log.v(TAG, "Increase Hour: " + thours);
                break;
            case R.id.HourDown:  //Decrement hours
                thours = decrement(thours, "hours");
                hour.setText(format(Locale.US, "%02d", thours));
                Log.v(TAG, "Decrease Hour: " + thours);
                break;
            case R.id.minUp: //Increment Minutes
                tmins = increment(tmins, "min");
                min.setText(format(Locale.US, "%02d", tmins));
                Log.v(TAG, "Increase Minutes: " + tmins);
                break;
            case R.id.minDown:  //Decrement Minutes
                tmins = decrement(tmins, "min");
                min.setText(format(Locale.US, "%02d", tmins));
                Log.v(TAG, "Decrease Minutes: " + tmins);
                break;
            case R.id.secUp:    //Increment seconds
                tsecs = increment(tsecs, "sec");
                sec.setText(format(Locale.US, "%02d", tsecs));
                Log.v(TAG, "Increase Seconds:" + tsecs);
                break;
            case R.id.secDown:  //Decrement seconds
                tsecs = decrement(tsecs, "sec");
                sec.setText(format(Locale.US, "%02d", tsecs));
                Log.v(TAG, "Decrease Seconds:" + tsecs);
                break;
            case R.id.start:
                //this button has 4 types: Start, Give up, Try Again, Restart, Enter Task
                focusTime();
                break;
            case R.id.taskSubmit:
                if (taskInput.getText().toString().isEmpty()) {
                    textDisplay.setText("Please enter a task");
                } else {
                    HideKeyboard();
                    currentTask = taskInput.getText().toString();
                    dateTimeTask = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault()).format(new Date());
                    Log.v(TAG, "Task: " + currentTask + " Date: " + dateTimeTask);
                    BUTTON_STATE = "Running";
                    taskSubmit.setVisibility(View.INVISIBLE);
                    focusTime();
                    break;
                }
        }
    }

    /**
     * Event State onFocusChange
     * <p>
     * Used detect if user click on outside of keyboard so it can automatically hide keyboard
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.taskInput:
                if (!hasFocus) {
                    HideKeyboard();
                }
                break;
        }
    }

    /**
     * Event State onLongClick
     */
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
            case R.id.HourUp:
                buphour = true;
                break;
            case R.id.HourDown:
                bdownhour = true;
                break;
        }
        repeatUpdateHandler.post(new RptUpdater());
        return false;
    }

    /**
     * Event State touchRelease
     * <p>
     * The event onTouch used to stop increment/decrement timer continuously when the button is onRelease
     *
     * @Param
     */
    public boolean touchRelease(boolean btimer, MotionEvent event) { //On release hold of timer
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL && btimer) {
            return false;
        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onDatabaseChanged() {
        try {
            user.setmFocusList(focusDBHelper.getAllMainData());
            user.setaFocusList(focusDBHelper.getAllArchiveData());
        }catch (Exception e){

        }
    }

    class RptUpdater implements Runnable {
        public void run() {
            if (bupmin) {
                tmins = increment(tmins, "min");
                min.setText(format(Locale.US, "%02d", tmins));
            } else if (bdownmin) {
                tmins = decrement(tmins, "min");
                min.setText(format(Locale.US, "%02d", tmins));
            } else if (bupsec) {
                tsecs = increment(tsecs, "sec");
                sec.setText(format(Locale.US, "%02d", tsecs));
            } else if (bdownsec) {
                tsecs = decrement(tsecs, "sec");
                sec.setText(format(Locale.US, "%02d", tsecs));
            } else if (buphour) {
                thours = increment(thours, "hour");
                hour.setText(format(Locale.US, "%02d", thours));
            } else if (bdownhour) {
                thours = decrement(thours, "hour");
                hour.setText(format(Locale.US, "%02d", thours));
            }
            repeatUpdateHandler.postDelayed(new RptUpdater(), 150);
        }
    }

    /**
     * Event State onTouch
     * <p>
     * The event onTouch used to increment/decrement timer continuously when the button is onhold
     */
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
            case R.id.HourUp:
                buphour = touchRelease(buphour, event);
                break;
            case R.id.HourDown:
                bdownhour = touchRelease(bdownhour, event);
                break;
        }
        return false;
    }

    /**
     * Event State onPause
     * If user exited the app, notification is pushed
     * Within 10 seconds, it will automatically count as fail
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Event State onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        user.setUID(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Log.v(TAG, "On Resume Activity  " + BUTTON_STATE);
        if (eCountDownTimer != null) {
            Log.v(TAG, "On CountDown");
            eCountDownTimer.cancel();
            eCountDownTimer = null;
            if (!BUTTON_STATE.equals("Reset")) {
                StartTimer(mTimeLeftInMillis);
            }
            Log.v(TAG, "Resume");
        }

        //Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        bottomNavInit(bottomNavigationView);
    }

    /**
     * Open History Fragment
     */
    public void openHistory() { //Open history tab

        fragmentFocus = HistoryFragment.newInstance(user, focusDBHelper);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragmentA = getSupportFragmentManager().findFragmentByTag("HISTORY FRAGMENT");

        if (fragmentA == null) {
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.add(R.id.fragment_container, fragmentFocus, "HISTORY FRAGMENT").commit();
        }

    }

    /**
     * Open Achievement Fragment
     */
    public void openAchievement() {

        fragmentAchievements = AchievementFragment.newInstance(user, focusDBHelper, achievementDBHelper);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragmentA = getSupportFragmentManager().findFragmentByTag("ACHIEVEMENT FRAGMENT");

        if (fragmentA == null) {
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_bottom, R.anim.enter_from_bottom, R.anim.exit_to_bottom);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.add(R.id.fragment_container, fragmentAchievements, "ACHIEVEMENT FRAGMENT").commit();
        }

    }

    @Override
    public void onFragmentInteraction() {
        onBackPressed();
    }

    /**
     * Decrement time of user preferences
     *
     * @param tChill used to passed in the current value of the time
     * @param type   used to passed to the type of the time (Minutes, Hours, Seconds)
     * @return int return back the decremented time
     */
    public int decrement(int tChill, String type) { //Increment method for timer
        if (type.equals("min")) {
            if (tChill != 0) {
                tChill--;
            }
        } else if (type.equals("hours")) {
            if (tChill != 0) {
                tChill--;
            }
        } else {
            if (tChill != 0) {
                tChill -= 5;
            } else {
                tChill = 55;
            }
        }
        return tChill;
    }

    /**
     * Increment time of user preferences
     *
     * @param tChill used to passed in the current value of the time
     * @param type   used to passed to the type of the time (Minutes, Hours, Seconds)
     * @return int return back the decremented time
     */
    public int increment(int tChill, String type) { //Decrement method for timer
        if (type.equals("min")) {
            if (tChill < 60) {
                tChill++;
            } else {
                tChill = 0;
            }
        } else if (type.equals("hours")) {
            if (tChill < 10) {
                tChill++;
            } else {
                tChill = 0;
            }
        } else {
            if (tChill != 55) {
                tChill += 5;
            } else {
                tChill = 0;
            }
        }
        return tChill;
    }

    /**
     * Start Timer for Focus task activity
     *
     * @param TimeLeftInMillis used to passed in the time set on the activity to count down the timer
     */
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

    /***
     * Event Handles when user quit the app
     */

    public void userQuitApp() {
        mCountDownTimer.cancel();
        boundService.setmContext(this);
        boundService.createNotification(); //Notification pushed
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

    /**
     * Set Reference Data from firebase based on the UID
     */
    private void FirebaseDatabase() { //Firebase Reference
//        user.setUID("V30jZctVgSPh00CVskSYiXNRezC2");
        Log.i(TAG, "Getting firebase for User ID " + user.getUID());
        myRef = FirebaseDatabase.getInstance().getReference().child("focusData").child(user.getUID());
        Log.i(TAG, "checks for myRef: " + myRef);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

    }


    /**
     * Writing new Focus activity data to Google Firebase
     * <p>
     * The OneTimeWorkRequest is used to set condition if there is network connectivity
     */
    private void writeDataFirebase(Focus focus) {
        Log.i(TAG, "Uploading to Database");

        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data firebaseUserData = new Data.Builder()
                .putString("ID", user.getUID())
                .putString("focusData", serializeToJson(focus))
                .putBoolean("deletion", false)
                .build();
        OneTimeWorkRequest mywork =
                new OneTimeWorkRequest.Builder(FocusWorker.class)
                        .setConstraints(myConstraints)
                        .setInputData(firebaseUserData)
                        .build();

        WorkManager.getInstance(this).enqueue(mywork);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, BoundService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }


    /**
     * Show soft keyboard for user input task
     */
    private void ShowKeyboard() {
        taskInput.setVisibility(View.VISIBLE);

        final InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        taskSubmit.setVisibility(View.VISIBLE);
        cdView.setVisibility(View.VISIBLE);
        keyboardShow = true;
        assert mgr != null;
        taskInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                taskInput.requestFocus();
                mgr.showSoftInput(taskInput, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);

        /***
         * If User clicks on done, it will simulate the task submit button as well
         */
        taskInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if ((i & EditorInfo.IME_MASK_ACTION) != 0) {
                    taskSubmit.callOnClick();
                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    /**
     * Hide soft keyboard for user input task
     */
    private void HideKeyboard() {
        taskInput.setVisibility(View.INVISIBLE);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        taskSubmit.setVisibility(View.INVISIBLE);
        cdView.setVisibility(View.INVISIBLE);
        keyboardShow = false;
        assert mgr != null;
        mgr.hideSoftInputFromWindow(taskInput.getWindowToken(), 0);
        Log.i(TAG, "Hide Keyboard");
    }

    /**
     * Serialize a single object.
     *
     * @return String this returns the custom object class as a string
     */
    public String serializeToJson(Focus myClass) {
        Gson gson = new Gson();
        Log.i(TAG, "Object serialize");
        return gson.toJson(myClass);
    }

    /**
     * To set the bottom nav to listen to item changes
     * and chose the item that have been selected
     *
     * @param bottomNavigationView The botomNav that needs to be set to listen
     */
    private void bottomNavInit(BottomNavigationView bottomNavigationView) {

        //To have the highlight
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        //To set setOnNavigationItemSelectedListener
        NavBarHelper navBarHelper = new NavBarHelper(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(navBarHelper);
    }

    /***
     * Application is put into a life cycle event to track the application service
     *
     */

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        Log.d("AppController", "Foreground");
        isAppInBackground(false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        Log.d("AppController", "Background");
        isAppInBackground(true);
    }

    // Adding some callbacks for test and log
    public interface ValueChangeListener {
        void onChanged(Boolean value);
    }

    private ValueChangeListener visibilityChangeListener;

    public void setOnVisibilityChangeListener(ValueChangeListener listener) {
        this.visibilityChangeListener = listener;
    }

    private void isAppInBackground(Boolean isBackground) {
        if (null != visibilityChangeListener) {
            visibilityChangeListener.onChanged(isBackground);
        }
    }

    private static FocusActivity mInstance;

    public static FocusActivity getInstance() {
        return mInstance;
    }

    //Bound Services
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BoundService.MyBinder myBinder = (BoundService.MyBinder) service;
            boundService = myBinder.getService();
            boundService.setmContext(FocusActivity.this);
            mServiceBound = true;
        }

    };

    //Popup layout to select
    private void showStatusPopup(final Activity context, Point p) {
        imageButton.setRotation(90);

        ImageView history_item, achievement_item;

        // Inflate the popup_layout.xml
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.layout_popup, null);

        //Setting onclick listener
        history_item = layout.findViewById(R.id.item_history);
        achievement_item = layout.findViewById(R.id.item_achievements);
        history_item.setOnClickListener(view -> {
            openHistory();
            changeStatusPopUp.dismiss();
        });

        achievement_item.setOnClickListener(view -> {
            if(user.getPermission(this)) {
                openAchievement();
                changeStatusPopUp.dismiss();
            }else{
                user.showPermissionDescription(this);
            }

        });

        // Creating the PopupWindow
        changeStatusPopUp = new PopupWindow(context);
        changeStatusPopUp.setContentView(layout);
        changeStatusPopUp.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        changeStatusPopUp.setFocusable(true);

        // Some offset to align the popup a bit to the left, and a bit down, relative to button's position.
        int OFFSET_X = 14;
        int OFFSET_Y = 130;

        changeStatusPopUp.setBackgroundDrawable(context.getResources().getDrawable(R.color.fui_transparent));

        // Displaying the popup at the specified location, + offsets.
        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        changeStatusPopUp.setOnDismissListener(() -> {
            imageButton.setRotation(0);
        });
    }
}

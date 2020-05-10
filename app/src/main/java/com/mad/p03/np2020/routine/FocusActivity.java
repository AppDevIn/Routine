package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FocusActivity extends AppCompatActivity implements View.OnClickListener, historyfocus.OnFragmentInteractionListener, View.OnLongClickListener, View.OnTouchListener {
    private FrameLayout fragmentContainer;

    private ImageButton imageButton;
    private Button startTimer;

    private TextView min, sec;
    private ImageView minup, mindown, secup, secdown, mface;

    private int tmins, tsecs = 0;
    private long totaltime = 0;
    boolean bupmin, bdownmin, bupsec, bdownsec;

    private Handler repeatUpdateHandler = new Handler();
    private CountDownTimer mCountDownTimer;

    private boolean mAutoIncrement = false;
    private boolean mAutoDecrement = false;
    private boolean mTimerRunning = false;

    private long mStartTimeInMillis = 0;
    private long mTimeLeftInMillis = 0;
    private long mEndTime;


    private static final String TAG = "Focus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Animation translateAnimation = AnimationUtils.loadAnimation(this, R.anim.translate_anims);
        fragmentContainer = findViewById(R.id.fragment_container);

        intializtion(); //Process of data

        //ImageButton
        imageButton = findViewById(R.id.history);
        startTimer = findViewById(R.id.start);

        //ImageView
        minup = findViewById(R.id.minUp);
        mindown = findViewById(R.id.minDown);
        secup = findViewById(R.id.secUp);
        secdown = findViewById(R.id.secDown);
        mface = findViewById(R.id.assistant);

        //TextView
        min = findViewById(R.id.mins);
        sec = findViewById(R.id.secs);

        //SetOnclickListener
        imageButton.setOnClickListener(this);
        minup.setOnClickListener(this);
        secdown.setOnClickListener(this);
        secup.setOnClickListener(this);
        mindown.setOnClickListener(this);
        startTimer.setOnClickListener(this);

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
            case R.id.history:
                openHistory("History");
                Log.v(TAG, "Open History Page");
                break;
            case R.id.minUp:
                tmins = increment(tmins, "min");
                min.setText(String.format("%02d", tmins));
                Log.v(TAG, "Increase Minutes: " + tmins);
                break;
            case R.id.minDown:
                tmins = decrement(tmins, "min");
                min.setText(String.format("%02d", tmins));
                Log.v(TAG, "Decrease Minutes: " + tmins);
                break;
            case R.id.secUp:
                tsecs = increment(tsecs, "sec");
                sec.setText(String.format("%02d", tsecs));
                Log.v(TAG, "Increase Seconds:" + tsecs);
                break;
            case R.id.secDown:
                tsecs = decrement(tsecs, "sec");
                sec.setText(String.format("%02d", tsecs));
                Log.v(TAG, "Decrease Seconds:" + tsecs);
                break;
            case R.id.start:
                focusTime();
                break;
        }
    }

    private void focusTime(){
        if (mTimerRunning){
            mTimerRunning = false;
            mCountDownTimer.cancel();
            mCountDownTimer.onFinish();
        }else {
            mTimerRunning = true;
            totaltime = (tmins) + tsecs / 60;
            long millisInput = totaltime * 60000;
            Log.v(TAG, String.valueOf(millisInput));
            setTime(millisInput);
        }
    }

    private void updateButtons() {
        if (mTimerRunning) {
            startTimer.setText(R.string.StopTimer);

            minup.setVisibility(View.INVISIBLE);
            mindown.setVisibility(View.INVISIBLE);
            secup.setVisibility(View.INVISIBLE);
            secdown.setVisibility(View.INVISIBLE);
        } else {
            startTimer.setText(R.string.startTimer);

            minup.setVisibility(View.VISIBLE);
            mindown.setVisibility(View.VISIBLE);
            secup.setVisibility(View.VISIBLE);
            secdown.setVisibility(View.VISIBLE);
            min.setText("00");
            sec.setText("00");
        }
    }

    private void setTime(long milliseconds) {
        mStartTimeInMillis = milliseconds;
        StartTimer(mStartTimeInMillis);
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60 ;
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
    protected void onStop(){
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();

    }

    public boolean touchRelease(boolean btimer, MotionEvent event) { //On release hold of timer
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL && btimer) {
            return false;
        }
        return false;
    }

    public void openHistory(String text) { //Open history tab
        historyfocus fragmentfocus = historyfocus.newInstance(text);
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
        Log.v(TAG,"Timer Start");

        mEndTime = System.currentTimeMillis() + TimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(TimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.v(TAG,"OnTick");
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                Log.v(TAG,"Completed");
                mTimerRunning = false;
                tsecs = 0;
                tmins = 0;
                updateButtons();
            }
        }.start();

        mTimerRunning = true;
        updateButtons();
    }

    private void intializtion(){
        tmins = 0;
        tsecs = 0;
    }
}

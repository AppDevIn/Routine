package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class FocusActivity extends AppCompatActivity implements View.OnClickListener, historyfocus.OnFragmentInteractionListener, View.OnLongClickListener, View.OnTouchListener {
    private FrameLayout fragmentContainer;
    private ImageButton imageButton;
    private TextView min, sec;
    private ImageView minup, mindown, secup, secdown;
    private int tmins, tsecs = 0;
    boolean bupmin, bdownmin, bupsec, bdownsec;
    private Handler repeatUpdateHandler = new Handler();
    private boolean mAutoIncrement = false;
    private boolean mAutoDecrement = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        fragmentContainer = findViewById(R.id.fragment_container);

        //ImageButton
        imageButton = findViewById(R.id.history);

        //ImageView
        minup = findViewById(R.id.minUp);
        mindown = findViewById(R.id.minDown);
        secup = findViewById(R.id.secUp);
        secdown = findViewById(R.id.secDown);

        //TextView
        min = findViewById(R.id.mins);
        sec = findViewById(R.id.secs);

        //SetOnclickListener
        imageButton.setOnClickListener(this);
        minup.setOnClickListener(this);
        secdown.setOnClickListener(this);
        secup.setOnClickListener(this);
        mindown.setOnClickListener(this);

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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId() /*to get clicked view id**/) {
            case R.id.history:
                openHistory("History");
                break;
            case R.id.minUp:
                tmins = increment(tmins, "min");
                min.setText(String.format("%02d",tmins));
                break;
            case R.id.minDown:
                tmins = decrement(tmins, "min");
                min.setText(String.format("%02d",tmins));
                break;
            case R.id.secUp:
                tsecs = increment(tsecs, "sec");
                sec.setText(String.format("%02d",tsecs));
                break;
            case R.id.secDown:
                tsecs = decrement(tsecs, "sec");
                sec.setText(String.format("%02d",tsecs));
                break;
        }
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
                min.setText(String.format("%02d",tmins));
            } else if (bdownmin) {
                tmins = decrement(tmins, "min");
                min.setText(String.format("%02d",tmins));
            } else if (bupsec) {
                tsecs = increment(tsecs, "sec");
                sec.setText(String.format("%02d",tsecs));
            } else if (bdownsec) {
                tsecs = decrement(tsecs, "sec");
                sec.setText(String.format("%02d",tsecs));
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
}

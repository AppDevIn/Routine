package com.mad.p03.np2020.routine.Class;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mad.p03.np2020.routine.R;

public class PopUp extends Activity {

    ImageButton UpArrowLeft;
    ImageButton UpArrowRight;
    ImageButton DownArrowLeft;
    ImageButton DownArrowRight;

    TextView TimerLeft;
    TextView TimerRight;

    public int hours = 0;
    public int minutes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow);

        UpArrowLeft = findViewById(R.id.LeftTop);
        UpArrowRight = findViewById(R.id.RightTop);
        DownArrowLeft = findViewById(R.id.LeftDown);
        DownArrowRight = findViewById(R.id.RightDown);

        TimerLeft = findViewById(R.id.timerLeft);
        TimerRight = findViewById(R.id.timerRight);

        UpArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hours += 1;
                TimerLeft.setText(timeToText(hours, 24));
            }
        });

        DownArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hours -= 1;
                TimerLeft.setText(timeToText(hours, 24));
            }
        });

        UpArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minutes += 1;
                TimerRight.setText(timeToText(minutes, 60));
            }
        });

        DownArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minutes -= 1;
                TimerRight.setText(timeToText(minutes, 60));
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int height = dm.heightPixels;
        int width = dm.widthPixels;

        getWindow().setLayout((int) (width*.6), (int) (height*.4));

    }

    public String timeToText(int time, int limit)
    {
        String timer = "";
        if (time < 10 && time >= 0)
        {
            timer = "0" + String.valueOf(time);
            return timer;
        }
        else if (time < 0)
        {
            timer = "00";
            return timer;
        }

        if (time >= limit)
        {
            timer = String.valueOf(limit-1);
            return timer;
        }

        timer = String.valueOf(time);
        return timer;
    }

}

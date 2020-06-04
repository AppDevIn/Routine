package com.mad.p03.np2020.routine.Class;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
    TextView TimerColon;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow);

        UpArrowLeft = findViewById(R.id.LeftTop);
        UpArrowRight = findViewById(R.id.RightTop);
        DownArrowLeft = findViewById(R.id.LeftDown);
        DownArrowRight = findViewById(R.id.RightDown);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int height = dm.heightPixels;
        int width = dm.widthPixels;

        getWindow().setLayout((int) (width*.8), (int) (height*.6));

    }

}

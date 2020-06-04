package com.mad.p03.np2020.routine.Class;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.mad.p03.np2020.routine.R;

public class PopUp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int height = dm.heightPixels;
        int width = dm.widthPixels;

    }
}

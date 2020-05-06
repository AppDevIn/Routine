package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;

public class Home extends AppCompatActivity {

    //Declare Constants


    //Declare member variables
    GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Find view by ID
        mGridView = findViewById(R.id.section_grid_view);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //TODO: Initialize any value
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void hardCodeData(){
        //TODO: Add in fake data

    }
}

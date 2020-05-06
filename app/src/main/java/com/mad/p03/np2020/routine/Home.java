package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;

import com.mad.p03.np2020.routine.Adapter.HomePageAdapter;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.Task;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    //Declare Constants


    //Declare member variables
    GridView mGridView;
    HomePageAdapter mHomePageAdapter;

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
        mHomePageAdapter = new HomePageAdapter(this,R.layout.home_grid_view_items, hardCodedList());
        mGridView.setAdapter(mHomePageAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    List<Section> hardCodedList(){
        List<Section> sectionList = new ArrayList<>();
        List<Task> taskList = new ArrayList<>();

        //TODO: Add in fake data
        taskList.add(new Task("Kill myself"));
        taskList.add(new Task("Kill myself"));
        taskList.add(new Task("Kill myself"));
        taskList.add(new Task("Kill myself"));
        taskList.add(new Task("Kill myself"));
        taskList.add(new Task("Kill myself"));


        sectionList.add(new Section("MAD", taskList));
        sectionList.add(new Section("Home", taskList));
        sectionList.add(new Section("Girlfriend", taskList));
        sectionList.add(new Section("School", taskList));


        return sectionList;

    }
}

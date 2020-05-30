package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Adapter.TaskAdapter;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.Task;

import java.util.ArrayList;
import java.util.Arrays;

public class TaskActivity extends AppCompatActivity {

    private final String TAG = "TASK";

    //Member variables
    RecyclerView mRecyclerView;
    TaskAdapter mTaskAdapter;
    Section mSection;
    ConstraintLayout mConstraintLayout;
    TextView mTxtListName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Log.d(TAG, "Creating GUI");


        //Get the Section Object
        mSection = (Section) getIntent().getSerializableExtra("section");
        Log.d(TAG, "onCreate(): " + mSection.toString());

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mTaskAdapter = new TaskAdapter(mSection.getTaskList());
        mRecyclerView.setAdapter(mTaskAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        //Find the id
        mTxtListName = findViewById(R.id.edSectioName);
        mConstraintLayout = findViewById(R.id.taskLayout);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "GUI ready");

        mTxtListName.setText(mSection.getName());
        mConstraintLayout.setBackgroundColor(mSection.getBackgroundColor());

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "GUI in the foreground and interactive");
    }
}

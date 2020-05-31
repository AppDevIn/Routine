package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Adapter.MyHomeItemTouchHelper;
import com.mad.p03.np2020.routine.Adapter.MyTaskTouchHelper;
import com.mad.p03.np2020.routine.Adapter.TaskAdapter;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.Task;

import java.util.ArrayList;
import java.util.Arrays;

public class TaskActivity extends AppCompatActivity {

    private final String TAG = "Task";

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

        //Find all the date from SQLite
        mSection.getTaskDatabase(this);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mTaskAdapter = new TaskAdapter(mSection);
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


        //Setting up touchhelper
        ItemTouchHelper.Callback callback = new MyTaskTouchHelper(mTaskAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        mTaskAdapter.setMyTaskTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "GUI in the foreground and interactive");
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}

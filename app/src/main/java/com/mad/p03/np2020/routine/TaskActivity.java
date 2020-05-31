package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Adapter.MyHomeItemTouchHelper;
import com.mad.p03.np2020.routine.Adapter.MyTaskTouchHelper;
import com.mad.p03.np2020.routine.Adapter.TaskAdapter;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.Task;
import com.mad.p03.np2020.routine.database.MyDatabaseListener;
import com.mad.p03.np2020.routine.database.TaskDBHelper;

import java.util.ArrayList;
import java.util.Arrays;

public class TaskActivity extends AppCompatActivity implements TextView.OnEditorActionListener, MyDatabaseListener {

    private final String TAG = "Task";

    //Member variables
    RecyclerView mRecyclerView;
    TaskAdapter mTaskAdapter;
    Section mSection;
    ConstraintLayout mConstraintLayout;
    TextView mTxtListName;
    EditText mEdTask;


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

        mTaskAdapter = new TaskAdapter(mSection, this);
        mRecyclerView.setAdapter(mTaskAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        //Find the id
        mTxtListName = findViewById(R.id.edSectioName);
        mConstraintLayout = findViewById(R.id.taskLayout);
        mEdTask = findViewById(R.id.edTask);


        //Set to listen for the editor
        //To see the typing and when enter is clicked than add the details
        mEdTask.setOnEditorActionListener(this);


        mEdTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showKeyboard(view);
            }
        });


        TaskDBHelper.setMyDatabaseListener(this);

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

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

            Log.d(TAG, "onEditorAction(): User eneted \"ENTER\" in keyboard ");

            //Create a task object
            Task task = new Task(textView.getText().toString(), mSection.getID());

            //Add this object to the list
            mTaskAdapter.addItem(task, this);


            //Hide and scroll the last task
            showNewEntry();


            Log.d(TAG, "onEditorAction: ");

            return true;
        }
        return false;
    }


    @Override
    public void onDataAdd(Object object) {

        Task task = (Task) object;

        Log.d(TAG, "onDataAdd(): A new data added into SQL updating local list with: " + task );

        //Adding into the local list
        mSection.getTaskList().add(task);

        //Informing the adapter and view of the new item
        mTaskAdapter.notifyItemInserted(mSection.getTaskList().size());
    }

    @Override
    public void onDataDelete(String ID) {

        Log.d(TAG, "onDataDelete(): Checking if " + ID + " exists");

        for (int position = 0; position < mSection.getTaskList().size(); position++) {

            if(mSection.getTaskList().get(position).getTaskID().equals(ID)){

                //Remove the list
                mSection.getTaskList().remove(position);

                //Informing the adapter and view after removing
                mTaskAdapter.notifyItemRemoved(position);
                mTaskAdapter.notifyItemRangeChanged(position, mSection.getTaskList().size());
                break;
            }
        }
    }


    /**
     * Upon calling this method, the keyboard will retract
     * and the recyclerview will scroll to the last item
     */
    private void showNewEntry(){
        //scroll to the last item of the recyclerview
        mRecyclerView.smoothScrollToPosition(mSection.getTaskList().size());

        //auto hide keyboard after entry
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mRecyclerView.getWindowToken(), 0);

        //Clear the text from the view
        mEdTask.setText("");
    }


    //Soft Keyboard methods
    private void showKeyboard(View view) {
        Log.i(TAG, "Show soft keyboard");
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert mgr != null;
        mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
}

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
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.mad.p03.np2020.routine.Adapter.MyTaskTouchHelper;
import com.mad.p03.np2020.routine.Adapter.TaskAdapter;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.Task;
import com.mad.p03.np2020.routine.database.MyDatabaseListener;
import com.mad.p03.np2020.routine.database.SectionDBHelper;
import com.mad.p03.np2020.routine.database.TaskDBHelper;


import java.util.List;

/**
 *
 * The controller class for xml layout activity_task
 * This will manage things that are on the task activity
 *
 * @author Jeyavishnu
 * @since 04-06-2020
 *
 */
public class TaskActivity extends AppCompatActivity implements TextView.OnEditorActionListener, MyDatabaseListener {

    private final String TAG = "Task";

    //Member variables
    RecyclerView mRecyclerView;
    TaskAdapter mTaskAdapter;
    Section mSection;
    ConstraintLayout mConstraintLayout;
    TextView mTxtListName;
    EditText mEdTask;
    List<Task> mTaskList;

    /**
     *
     * This is used to get the ID of for the view and initialize the recycler
     * view for the tasks. Setting the onclick lister too
     *
     * @param savedInstanceState will be null at first as
     *                           the orientation changes it will get
     *                           in use
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        Log.d(TAG, "Creating GUI");

        //To set to Full screen
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //Get the Section Object
        mSection = (Section) getIntent().getSerializableExtra("section");
        Log.d(TAG, "onCreate(): " + mSection.toString());

        //Find all the date from SQLite
        mTaskList = mSection.getTaskList(this);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mTaskAdapter = new TaskAdapter(mTaskList, this);
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
                showKeyboard(findViewById(R.id.cdAdd));
            }
        });


        TaskDBHelper.setMyDatabaseListener(this);


    }

    /**
     *
     * Set the color for layout based
     * on the users preference and set the name of the section
     *
     */
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

    /**
     * Not implemented yet
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "GUI in the foreground and interactive");
    }

    /**
     * Not implemented yet
     * The order of the task is
     * saved
     */
    @Override
    protected void onStop() {
        super.onStop();
        TaskDBHelper taskDBHelper = new TaskDBHelper(this);
        for (int i = 0; i < mTaskList.size(); i++) {
            mTaskList.get(i).setPosition(i);
            mTaskList.get(i).executeFirebaseUpload(this);
            taskDBHelper.updatePosition(mTaskList.get(i));

        }


    }

    /**
     *
     * The action is being performed on the keyboard
     * when the the Enter key is pressed add the task into
     * the adapter and hide the keyboard
     *
     * @param textView The view that was clicked.
     * @param actionId  Identifier of the action. This will be either the identifier you supplied, or
     *                  EditorInfo#IME_NULL if being called due to the enter key being pressed.
     * @param event  If triggered by an enter key, this is the event; otherwise, this is null.
     * @return Return true if you have consumed the action, else false.
     */
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


            return true;
        }
        return false;
    }

    /**
     *
     * Triggered to add to the current adapter list
     * when it is added to the sql
     *
     * @param object given from the SQL when triggered
     *               for this the object is task
     */
    @Override
    public void onDataAdd(Object object) {

        Task task = (Task) object;

        Log.d(TAG, "onDataAdd(): A new data added into SQL updating local list with: " + task );

        if(mSection.getID().equals(task.getSectionID())){
            //Adding into the local list
            mTaskList.add(task);

        }

        //Informing the adapter and view of the new item
        mTaskAdapter.notifyItemInserted(mTaskList.size());
    }

    /**
     *
     * When the onDataDelete is triggered it will
     * check the array of task that manages the task ID
     *
     * @param ID The ID of the task that need to be deleted
     */
    @Override
    public void onDataDelete(String ID) {

        Log.d(TAG, "onDataDelete(): Checking if " + ID + " exists");

        for (int position = 0; position < mTaskList.size(); position++) {

            if(mTaskList.get(position).getTaskID().equals(ID)){

                //Remove the list
                mTaskList.remove(position);

                //Informing the adapter and view after removing
                mTaskAdapter.notifyItemRemoved(position);
                mTaskAdapter.notifyItemRangeChanged(position, mTaskList.size());
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
        mRecyclerView.smoothScrollToPosition(mTaskList.size());

        //auto hide keyboard after entry
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mRecyclerView.getWindowToken(), 0);

        //Clear the text from the view
        mEdTask.setText("");
    }


    /**
     *
     * Show the keyboard the the focused view
     *
     * @param view The view that wants to receive the soft keyboard input
     */
    private void showKeyboard(View view) {
        Log.i(TAG, "Show soft keyboard");

        view.requestFocus();

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert mgr != null;
        mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }
}

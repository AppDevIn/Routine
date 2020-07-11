package com.mad.p03.np2020.routine.Card;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mad.p03.np2020.routine.Card.Fragments.CheckListFragment;
import com.mad.p03.np2020.routine.Card.Fragments.NotesFragment;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.models.Task;

/**
*
* CardActivity class used to manage card activities
*
* @author Jeyavishnu & Pritheev
 *@since 10-07-2020
*
 */

public class CardActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "CardActivity";

    //Member Variable
    Task mTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_layout);

        //Get task object from extra
        mTask = (Task) getIntent().getSerializableExtra("task");

        //IDs
        EditText edTitle = findViewById(R.id.title);
        LinearLayout check = findViewById(R.id.ll_check);
        LinearLayout focus = findViewById(R.id.ll_focus);
        LinearLayout schedule = findViewById(R.id.ll_schedule);
        LinearLayout notes = findViewById(R.id.ll_notes);

        //Set onclick listeners
        check.setOnClickListener(this);
        focus.setOnClickListener(this);
        schedule.setOnClickListener(this);
        notes.setOnClickListener(this);

        //Set the title text the task name
        edTitle.setText(mTask.getName());

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {

        //Doesn't have to be visible if a tool has been selected
        findViewById(R.id.toolMessage).setVisibility(View.GONE);

        //Since there is tool selected the fragment can be visible
        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE);

        switch (view.getId()){
            case R.id.ll_check:
                Log.i(TAG, "onClick: Check List is clicked");
                checkList();
                break;            
            case R.id.ll_focus:
                Log.i(TAG, "onClick: Focus is clicked");
                focus();
                break; 
            case R.id.ll_schedule:
                Log.i(TAG, "onClick: Schedule is clicked");
                schedule();
                break;
            case R.id.ll_notes:
                Log.i(TAG, "onClick: Notes is clicked");
                notes();
                break;
                
        }
    }

    private void schedule() {
    }

    private void notes() {
        //Initializing fragment manager and fragment transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //Creating an instance of the notes fragment class
        NotesFragment noteFrag = new NotesFragment(mTask);

        //Replacing FragmentContainer in CardLayout with notes fragment view
        fragmentTransaction.replace(R.id.fragmentContainer, noteFrag);

        //Committing to enable fragment view
        fragmentTransaction.commit();
    }

    private void focus() {
    }

    private void checkList() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        CheckListFragment fragment = new CheckListFragment(mTask.getTaskID());
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }



}

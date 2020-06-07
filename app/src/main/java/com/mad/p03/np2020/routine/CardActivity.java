package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Class.PopUp;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.Task;
import com.mad.p03.np2020.routine.Fragment.NotesFragment;
import com.mad.p03.np2020.routine.Fragment.StepsFragment;
import com.mad.p03.np2020.routine.database.TaskDBHelper;

/**
*
* CardActivity class used to manage card activities
*
* @author Pritheev
* @since 02-06-2020
*
 */

public class CardActivity extends AppCompatActivity {
    //Initialising variables

    //TAG used for logging
    final String TAG = "Card Layout";

    //Used to identify step adding button
    Button stepAddButton;

    //Used to identify notification button
    ImageButton notifyButton;

    //Used to identify Notes Fragment
    Button notesFragment;

    //Used to identify Steps Fragment
    Button stepFragment;

    //Used to set if Steps Fragment visible or not
    boolean stepStatus = false;

    //Used to set if Notes Fragment visible or not
    boolean noteStatus = false;

    Task mTask;

    TextView cardName;

    TaskDBHelper mTaskDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_layout);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //Get the Section Object
        mTask = (Task) getIntent().getSerializableExtra("task");
        Log.d(TAG, "onCreate(): " + mTask.toString());



        cardName = findViewById(R.id.id_edit_text);

        //Used to initialize stepAddButton with an id from view
        stepAddButton = findViewById(R.id.stepAdd);

        //Used to initialize notificationButton with an id from view
        notifyButton = findViewById(R.id.notifyButton);

        //Used to initialize stepFragment with an id from view
        stepFragment = findViewById(R.id.stepFragment);

        //Used to initialize notesFragment with an id from view
        notesFragment = findViewById(R.id.notesFragment);


        cardName.setText(String.valueOf(mTask.getName()));

        mTaskDBHelper = new TaskDBHelper(this);


        mTask = mTaskDBHelper.getTask(mTask.getTaskID());


        cardName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {


                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    Log.d(TAG, "onEditorAction: " + textView.getText());
                    mTask.setName(textView.getText().toString());
                    mTaskDBHelper.update(mTask.getTaskID(), mTask.getName(), null);
                    showNewEntry(textView);
                }

                return false;

            }
        });

        //To open steps fragment when steps button clicked
        stepFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Assign false to false to allow it to meet condition of if statement
                stepStatus = false;

                //Assign true to disable notes Fragment from appearing
                noteStatus = true;

                //Initializing fragment manager and fragment transaction
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                if (!stepStatus)
                {
                    //Creating an instance of the steps fragment class
                    StepsFragment stepFrag = new StepsFragment();

                    //Replacing FragmentContainer in CardLayout with step fragment view
                    fragmentTransaction.replace(R.id.fragmentContainer, stepFrag);

                    //Committing to enable fragment view
                    fragmentTransaction.commit();
                }
            }
        });

        // To open notes fragment when notes button clicked
        notesFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Assign false to false to allow it to meet condition of if statement
                noteStatus = false;

                //Assign true to disable steps Fragment from appearing
                stepStatus = true;

                //Initializing fragment manager and fragment transaction
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                if (!noteStatus)
                {
                    //Creating an instance of the notes fragment class
                    NotesFragment noteFrag = new NotesFragment(mTask);

                    //Replacing FragmentContainer in CardLayout with notes fragment view
                    fragmentTransaction.replace(R.id.fragmentContainer, noteFrag);

                    //Committing to enable fragment view
                    fragmentTransaction.commit();
                }
            }
        });

        //Notification Button On Click Listener
        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardActivity.this, PopUp.class);
                intent.putExtra("task", mTask);
                startActivity(intent);
            }
        });

    }

    /**
     * Upon calling this method, the keyboard will retract
     * and the recyclerview will scroll to the last item
     */
    private void showNewEntry(View view){

        //auto hide keyboard after entry
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Updating");
       mTask.executeUpdateFirebase(this);
    }
}

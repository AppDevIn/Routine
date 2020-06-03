package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Class.Steps;

import java.util.ArrayList;
import java.util.List;

/*
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

    //Used to identify Notes Fragment
    Button notesFragment;

    //Used to identify Steps Fragment
    Button stepFragment;

    //Used to set if Steps Fragment visible or not
    boolean stepStatus = false;

    //Used to set if Notes Fragment visible or not
    boolean noteStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_layout);

        //Used to initialize stepAddButton with an id from view
        stepAddButton = findViewById(R.id.stepAdd);

        //Used to initialize stepFragment with an id from view
        stepFragment = findViewById(R.id.stepFragment);

        //Used to initialize notesFragment with an id from view
        notesFragment = findViewById(R.id.notesFragment);

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
                    NotesFragment noteFrag = new NotesFragment();

                    //Replacing FragmentContainer in CardLayout with notes fragment view
                    fragmentTransaction.replace(R.id.fragmentContainer, noteFrag);

                    //Committing to enable fragment view
                    fragmentTransaction.commit();
                }
            }
        });

    }

}

package com.mad.p03.np2020.routine.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.mad.p03.np2020.routine.Adapter.CardAdapter;
import com.mad.p03.np2020.routine.R;

import java.util.ArrayList;

/**
 *
 * StepsFragment class for checklist
 *
 * @author Pritheev
 * @since 02-06-2020
 *
 */

public class StepsFragment extends Fragment{
    // Initializing variables

    //TAG used for logging
    final String TAG = "StepFragment";

    //Recyclerview for checklist
    RecyclerView recyclerView;

    //List adapter for list of checklist objects
    CardAdapter cardAdapter;

    //List for storing checklist items
    ArrayList<String> stepList = new ArrayList<>();

    //EditText for storing step name input
    EditText stepName;

    //Button for when adding step to the list
    Button stepAddButton;

    //View used in onCreateView function
    View view;

    //Empty Constructor for StepsFragment
    public StepsFragment()
    {
        // Required empty public constructor
    }

    //Initializing variables for StepsFragment when new instance created
    public static StepsFragment newInstance()
    {
        //Creating an instance of StepsFragment
        StepsFragment fragment = new StepsFragment();

        //Initializing Bundle
        Bundle args = new Bundle();

        //Setting Bundle for fragment
        fragment.setArguments(args);

        //Returning fragment
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_steps_layout, container, false);

        //Get RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);

        //Get user input from EditText of StepsFragment
        stepName = view.findViewById(R.id.stepInput);

        // Button click listeners for when button clicked to call addItemToList
        stepAddButton = view.findViewById(R.id.stepAdd);

        //LinearLayout manager for linear layout of recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        cardAdapter = new CardAdapter(stepList);

        recyclerView.setAdapter(cardAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //onClickListener for stepAddButton
        stepAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Log Message
                Log.v(TAG, "Step added!");

                String task = stepName.getText().toString();
                stepList.add(task);
                cardAdapter.notifyDataSetChanged();
            }
        });
        return view;

    }


}

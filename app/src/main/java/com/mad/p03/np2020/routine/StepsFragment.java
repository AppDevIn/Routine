package com.mad.p03.np2020.routine;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/*
 *
 * CardActivity class used to manage card activities
 *
 * @author Pritheev
 * @since 02-06-2020
 *
 */

public class StepsFragment extends Fragment{
    // Initializing variables

    //TAG used for logging
    final String TAG = "StepFragment";

    //Array adapter for list of checklist objects
    ArrayAdapter<String> arrayAdapter;

    //List for storing checklist items
    List<String> stepList;

    //ListView for FragmentContainer
    ListView listView;

    //EditText for storing step name input
    EditText stepName;

    //Button for when adding step to the list
    Button stepAddButton;

    //View used in onCreateView function
    View view;

    //Constructor for StepsFragment
    public StepsFragment() {
        // Required empty public constructor
    }

    // Instantiate Steps Fragment
    public static StepsFragment newInstance() {
        StepsFragment fragment = new StepsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            // Get user input
            stepName = getView().findViewById(R.id.stepInput);

            // Use list view for checklist
            listView = getView().findViewById(R.id.listView);

            // Store checklist items in array list
            stepList = new ArrayList<>();
            arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_view_layout, stepList);

        }
    }

    // Function to add items to checklist when add button clicked
    public void addItemToList(){
        stepList.add(stepName.getText().toString());
        arrayAdapter.notifyDataSetChanged();

        stepName.setText("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_steps_layout, container, false);

        // Button click listeners for when button clicked to call addItemToList
        stepAddButton = (Button) view.findViewById(R.id.stepAdd);

        stepAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Step added!");
                addItemToList();
            }
        });
        return view;

    }


}

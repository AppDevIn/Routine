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


public class StepsFragment extends Fragment implements View.OnClickListener{

    // Initializing variables
    final String TAG = "StepFragment";
    private String mParam1;
    private String mParam2;
    ArrayAdapter<String> arrayAdapter;
    List<String> stepList;
    ListView listView;
    EditText stepName;
    Button stepAddButton;
    View view;

    // Constructor
    public StepsFragment() {
        // Required empty public constructor
    }

    // Instantiate Steps Fragment
    public static StepsFragment newInstance(String param1, String param2) {
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

            // Use list view for cehcklist
            listView = getView().findViewById(R.id.listView);

            // Store checklist items in array lsit
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
        stepAddButton = (Button) view.findViewById(R.id.addStep);

        stepAddButton.setOnClickListener(this);
        return view;

    }

    @Override
    public void onClick(View v) {
        Log.v(TAG, "Step added!");

    }
}

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


public class StepsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    final String TAG = "StepFragment";
    private String mParam1;
    private String mParam2;
    ArrayAdapter<String> arrayAdapter;
    List<String> stepList;
    ListView listView;
    EditText stepName;
    Button stepAddButton;

    public StepsFragment() {
        // Required empty public constructor
    }


    public static StepsFragment newInstance(String param1, String param2) {
        StepsFragment fragment = new StepsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            stepName = getView().findViewById(R.id.stepInput);
            listView = getView().findViewById(R.id.listView);

            stepList = new ArrayList<>();
            arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_view_layout, stepList);

            stepAddButton = (Button) getView().findViewById(R.id.addStep);

            stepAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Step added!");
                    stepAddButton.setText("LOL");
                    addItemToList(v);
                }
            });

        }
    }

    public void addItemToList(View view){
        stepList.add(stepName.getText().toString());
        arrayAdapter.notifyDataSetChanged();

        stepName.setText("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_steps_layout, container, false);
    }
}

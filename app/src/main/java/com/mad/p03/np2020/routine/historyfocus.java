package com.mad.p03.np2020.routine;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Class.Focus;
import com.mad.p03.np2020.routine.Class.FocusAdapter;
import com.mad.p03.np2020.routine.Class.VerticalSpaceItemDecoration;

import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;


public class historyfocus extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TEXT = "What you want?";

    // TODO: Rename and change types of parameters
    private String mText;

    private ImageButton buttonFragment;
    private TextView textFragment;
    private OnFragmentInteractionListener mListener;
    private static final int VERTICAL_ITEM_SPACE = 30;
    private final String TAG = "Focus";
    private TextView completion;

    //Recycler View
    private RecyclerView recyclerView;
    private FocusAdapter focusAdapter;
    private List<Focus> historyfocusList = new ArrayList<>();

    //Test Fake Data - To be removed after firebase implementation
    String[] TaskList = {"Sleeping", "Exercise"};
    String[] DateTimeList = {"2/5/2020, 2:03pm", "1/5/2020, 2:05pm"};
    String[] durationList = {"5 Hours", "4 Hours", "2 Hours"};

    public historyfocus() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static historyfocus newInstance(String Text) {
        historyfocus fragment = new historyfocus();
        Bundle args = new Bundle();
        args.putString(TEXT, Text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mText = getArguments().getString(TEXT);
            Log.v(TAG, "Created fragment");
        }


    }

    public void sendBack() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
            Log.v(TAG, "Sending back");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historyfocus, container, false);
        buttonFragment = view.findViewById(R.id.closeFragment);
        textFragment = view.findViewById(R.id.text_cool);
        completion = view.findViewById(R.id.NumberOfCompletion);

        //RecyclerView for display history
        recyclerView = view.findViewById(R.id.recyclerHistory);

        //recycler adapter
        focusAdapter = new FocusAdapter(historyfocusList, getActivity());
        Drawable dividerDrawable = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.divider);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false); //Declare layoutManager

        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE, dividerDrawable)); //Add Custom Spacing
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(focusAdapter);

        completion.setText("You have completed\n" + TaskList.length + " Task");

        //Add data
        for (int i = 0; i < TaskList.length; i++) {
            String Task = TaskList[i];
            String date = DateTimeList[i];
            String duration = durationList[i];

            Focus focus = new Focus(date, duration, Task, false);
            historyfocusList.add(focus);
        }
        textFragment.setText(mText);

        buttonFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBack();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

}

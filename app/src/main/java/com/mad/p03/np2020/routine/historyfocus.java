package com.mad.p03.np2020.routine;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Class.Focus;
import com.mad.p03.np2020.routine.Class.FocusAdapter;
import com.mad.p03.np2020.routine.Class.VerticalSpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;


public class historyfocus extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String LIST_GET = "List Get";

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

    public historyfocus() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static historyfocus newInstance(ArrayList<Focus> historyfocusList) {
        historyfocus fragment = new historyfocus();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LIST_GET, historyfocusList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            historyfocusList = getArguments().getParcelableArrayList(LIST_GET);
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

        completion.setText("You have completed\n" + historyfocusList.size() + " Task");

        textFragment.setText("History");

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

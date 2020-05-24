package com.mad.p03.np2020.routine;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Class.FocusHolder;
import com.mad.p03.np2020.routine.Class.FocusAdapter;
import com.mad.p03.np2020.routine.Class.ItemDecoration;
import com.mad.p03.np2020.routine.database.FocusDatabase;

import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment {

    private static final String LIST_GET = "List Get";

    private ImageButton buttonFragment;
    private TextView textFragment;
    private OnFragmentInteractionListener mListener;

    private static final int VERTICAL_ITEM_SPACE = 30; //Spacing length
    private final String TAG = "Focus";
    private TextView completion;

    private List<FocusHolder> historyFocusList = new ArrayList<>();
    private FocusDatabase focusDatabase;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(ArrayList<FocusHolder> historicList, FocusDatabase focusDatabase) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LIST_GET, historicList);
        args.putParcelable("FocusDatabase", focusDatabase);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            historyFocusList = getArguments().getParcelableArrayList(LIST_GET);
            focusDatabase = getArguments().getParcelable("FocusDatabase");
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
        //Recycler View
        RecyclerView recyclerView = view.findViewById(R.id.recyclerHistory);

        //recycler adapter
        FocusAdapter focusAdapter = new FocusAdapter(historyFocusList, getActivity(), focusDatabase);
        Drawable dividerDrawable = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.divider);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false); //Declare layoutManager

        recyclerView.addItemDecoration(new ItemDecoration(VERTICAL_ITEM_SPACE, dividerDrawable)); //Add Custom Spacing
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(focusAdapter);

        initialisation();
        buttonFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBack();
            }
        });
        return view;
    }

    public void initialisation() {
        textFragment.setText("History");
        updateTask();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    public void updateTask() {
        completion.setText("You have completed\n" + historyFocusList.size() + " Task");
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

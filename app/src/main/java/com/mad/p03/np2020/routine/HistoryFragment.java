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
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Adapter.FocusAdapter;
import com.mad.p03.np2020.routine.Class.ItemDecoration;
import com.mad.p03.np2020.routine.Class.User;
import com.mad.p03.np2020.routine.database.FocusDatabase;

import java.util.Locale;


public class HistoryFragment extends Fragment {

    private static final String USER_GET = "User Get";
    private static final String FOCUS_DATABASE = "FocusDatabase";

    private ImageButton buttonFragment;
    private ImageView nothing;
    private TextView textFragment;
    private OnFragmentInteractionListener mListener;

    private static final int VERTICAL_ITEM_SPACE = 30; //Spacing length
    private final String TAG = "Focus";
    private TextView completion;

    private FocusDatabase focusDatabase;
    private User user;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(User user, FocusDatabase focusDatabase) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putParcelable(USER_GET, user);
        args.putParcelable(FOCUS_DATABASE, focusDatabase);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(USER_GET);
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
        nothing = view.findViewById(R.id.nothing);

        //RecyclerView for display history
        //Recycler View
        RecyclerView recyclerView = view.findViewById(R.id.recyclerHistory);

        //recycler adapter
        FocusAdapter focusAdapter = new FocusAdapter(user, getActivity(), focusDatabase);
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
        completion.setText(String.format(Locale.getDefault(),"You have completed\n%d Task", user.getmFocusList().size()));
        if(user.getmFocusList().size() == 0){
            nothing.setVisibility(View.VISIBLE);
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

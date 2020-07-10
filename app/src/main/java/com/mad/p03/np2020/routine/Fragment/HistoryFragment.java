package com.mad.p03.np2020.routine.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Adapter.FocusAdapter;
<<<<<<< HEAD:app/src/main/java/com/mad/p03/np2020/routine/HistoryFragment.java
import com.mad.p03.np2020.routine.models.ItemDecoration;
import com.mad.p03.np2020.routine.models.User;
=======
import com.mad.p03.np2020.routine.models.User;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.helpers.DividerItemDecoration;
>>>>>>> master:app/src/main/java/com/mad/p03/np2020/routine/Fragment/HistoryFragment.java
import com.mad.p03.np2020.routine.DAL.FocusDBHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 *
 * History fragment used to manage the fragment of Focus layout section
 *
 * @author Lee Quan Sheng
 * @since 02-06-2020
 */


public class HistoryFragment extends Fragment {

    private static final String USER_GET = "User Get";
    private static final String FOCUS_DATABASE = "FocusDatabase";

    private ImageButton buttonFragment;
    private ImageView nothing;
    private TextView textFragment, sIndicator, usIndicator, totalHours;
    private OnFragmentInteractionListener mListener;

    private static final int VERTICAL_ITEM_SPACE = 30; //Spacing length
    private final String TAG = "Focus";

    private FocusDBHelper focusDBHelper;
    private User user;

    /**
    * Empty Constructor for Fragment History
    *
     **/
    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     *
     * Default Constructor for Fragment History
     * NewInstance() method of Constructor class invoke of 3 arguments
     * It creates a Bundle for custom object and database to be returned
     *
     * @return HistoryFragment return history fragment with object given
     * @param user Set user in this context
     * @param focusDBHelper Set Local database in this content
     **/
    public static HistoryFragment newInstance(User user, FocusDBHelper focusDBHelper) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putParcelable(USER_GET, user);
        args.putParcelable(FOCUS_DATABASE, focusDBHelper);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *
     * OnCreate the history fragment
     * Retrieve USER_GET data passed in to parceable format
     *
     * @param savedInstanceState Set the bundle data to this content
     **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(USER_GET);
            focusDBHelper = getArguments().getParcelable("FocusDatabase");
            Log.v(TAG, "Created fragment");
        }
    }


    private void sendBack() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
            Log.v(TAG, "Sending back");
        }
    }

    /**
     *
     * OnCreate the history fragment
     * Retrieve USER_GET data passed in to parceable format
     *
     * @param inflater Set the layoutInflater  to this content
     * @param container Set the viewGroup to this content
     * @param savedInstanceState Set the bundle to this content
     **/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historyfocus, container, false);
        buttonFragment = view.findViewById(R.id.closeFragment);
        textFragment = view.findViewById(R.id.title);
        usIndicator = view.findViewById(R.id.unSuccessFocusIndicator);
        sIndicator = view.findViewById(R.id.SuccessFocusIndicator);
        nothing = view.findViewById(R.id.nothing);
        totalHours = view.findViewById(R.id.totalHours);

        //RecyclerView for display history
        //Recycler View
        RecyclerView recyclerView = view.findViewById(R.id.recyclerHistory);

        //recycler adapter
        FocusAdapter focusAdapter = new FocusAdapter(user, getActivity(), focusDBHelper);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 2); //Declare layoutManager

        recyclerView.addItemDecoration(new DividerItemDecoration(15)); //Add Custom Spacing
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

    /**
     *
     * Used for initialization of history Fragment
     **/
    private void initialisation() {
        textFragment.setText("My Focus History");
        updateTask();
    }

    /**
    *
    * Method to be called once the fragment is associated with its activity.
     * @param context set the context to this content
    * */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     *
     * Method to update task number on the fragment
     * */
    private void updateTask() {
        usIndicator.setText(String.valueOf(user.getmUnsuccessFocusList().size()));
        sIndicator.setText(String.valueOf(user.getmSuccessFocusList().size()));
        int totalSeconds = user.getTotalHours();
        List<Integer> hrMinSec = ConvertSecondsToTime(totalSeconds);
        totalHours.setText(String.format(Locale.US, "Total: %dHr %dMin %dSec", hrMinSec.get(0), hrMinSec.get(1), hrMinSec.get(2)));

        if(user.getmFocusList().size() == 0){
            nothing.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     * Called when the fragment is no longer attached to its activity to detach the fragment
     * */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     *
     * Callback interface
     * */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

    public List<Integer> ConvertSecondsToTime(int TotalInSeconds){
        if(TotalInSeconds != 0) {
            int hours = (TotalInSeconds) / 3600;
            int minutes = ((TotalInSeconds) % 3600) / 60;
            int seconds = (TotalInSeconds) % 60;
            return Arrays.asList(hours, minutes, seconds);

        }
        return Arrays.asList(0, 0, 0);
    }


}

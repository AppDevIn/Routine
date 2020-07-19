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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Adapter.FocusAdapter;
import com.mad.p03.np2020.routine.R.color;
import com.mad.p03.np2020.routine.models.Focus;
import com.mad.p03.np2020.routine.models.ResizeableButton;
import com.mad.p03.np2020.routine.models.User;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.helpers.DividerItemDecoration;
import com.mad.p03.np2020.routine.DAL.FocusDBHelper;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.time.temporal.TemporalAdjusters.previousOrSame;

/**
 * History fragment used to manage the fragment of Focus layout section
 *
 * @author Lee Quan Sheng
 * @since 02-06-2020
 */


public class HistoryFragment extends Fragment implements View.OnClickListener {

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

    //Date Range Selector
    private ResizeableButton sun, mon, tue, wed, thr, fri, sat, prevWeek, nextWeek;
    private TextView dateRangeView;
    private RadioButton week, day;
    private RadioGroup toggle;

    //StartDate and EndDate
    private HashMap<Integer, Date> mappingDate;

    private FocusAdapter focusAdapter;

    private ResizeableButton previousButton;
    /**
     * Empty Constructor for Fragment History
     **/
    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Default Constructor for Fragment History
     * NewInstance() method of Constructor class invoke of 3 arguments
     * It creates a Bundle for custom object and database to be returned
     *
     * @param user          Set user in this context
     * @param focusDBHelper Set Local database in this content
     * @return HistoryFragment return history fragment with object given
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
     * OnCreate the history fragment
     * Retrieve USER_GET data passed in to parceable format
     *
     * @param inflater           Set the layoutInflater  to this content
     * @param container          Set the viewGroup to this content
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

        //Date Selector
        mon = view.findViewById(R.id.mondayButton);
        tue = view.findViewById(R.id.tuesdayButton);
        wed = view.findViewById(R.id.wednesdayButton);
        thr = view.findViewById(R.id.thursdayButton);
        fri = view.findViewById(R.id.fridayButton);
        sat = view.findViewById(R.id.saturdayButton);
        sun = view.findViewById(R.id.sundayButton);

        prevWeek = view.findViewById(R.id.weekSelectLeft);
        nextWeek = view.findViewById(R.id.weekSelectRight);

        toggle = view.findViewById(R.id.toggle);

        week = view.findViewById(R.id.week);
        day = view.findViewById(R.id.day);

        //RecyclerView for display history
        //Recycler View
        RecyclerView recyclerView = view.findViewById(R.id.recyclerHistory);

        //recycler adapter
        focusAdapter = new FocusAdapter(user, getActivity(), focusDBHelper, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 2); //Declare layoutManager

        recyclerView.addItemDecoration(new DividerItemDecoration(15)); //Add Custom Spacing
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(focusAdapter);

        //Date Range Selector
        mon.setOnClickListener(this);
        tue.setOnClickListener(this);
        wed.setOnClickListener(this);
        thr.setOnClickListener(this);
        fri.setOnClickListener(this);
        sat.setOnClickListener(this);
        sun.setOnClickListener(this);

        week.setOnClickListener(this);
        day.setOnClickListener(this);

        toggle.setOnClickListener(this);

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
     * Used for initialization of history Fragment
     **/
    private void initialisation() {
        textFragment.setText("My Focus History");
        Date date = new Date();
        mappingDate = getMapWeek(date);

        updateTask(this.user);
    }

    /**
     * Method to be called once the fragment is associated with its activity.
     *
     * @param context set the context to this content
     */
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
     * Method to update task number on the fragment
     */
    public void updateTask(User user) {
        usIndicator.setText(String.valueOf(user.getmUnsuccessFocusList().size()));
        sIndicator.setText(String.valueOf(user.getmSuccessFocusList().size()));
        int totalSeconds = user.getTotalHours();
        List<Integer> hrMinSec = ConvertSecondsToTime(totalSeconds);
        totalHours.setText(String.format(Locale.US, "Total: %dHr %dMin %dSec", hrMinSec.get(0), hrMinSec.get(1), hrMinSec.get(2)));

        if (user.getmFocusList().size() == 0) {
            nothing.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when the fragment is no longer attached to its activity to detach the fragment
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.week:
                break;
            case R.id.day:
                break;
            case R.id.mondayButton:
                try {
                    displayResult(1, mon);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.tuesdayButton:
                try {
                    displayResult(2, tue);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.wednesdayButton:
                try {
                    displayResult(3, wed);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.thursdayButton:
                try {
                    displayResult(4, thr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.fridayButton:
                try {
                    displayResult(5, fri);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.saturdayButton:
                try {
                    displayResult(6, sat);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.sundayButton:
                try {
                    displayResult(7, sun);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void displayResult(int i, ResizeableButton id) throws ParseException {
        if(previousButton != null){
            previousButton.setTextColor(getResources().getColor(color.black));
        }
        previousButton = id;
        Log.v(TAG, "Button Pressed " + i + " on " + id);
        id.setTextColor(getResources().getColor(color.pastelBlue));
        toggle.check(R.id.day);
        Date selectedDate = mappingDate.get(i);
        ArrayList<Focus> listUpdate = user.getmFocusList(selectedDate);
        focusAdapter.updateList(listUpdate);
    }

    //Get Current week
    public HashMap<Integer, Date> getMapWeek(Date date) {

        Log.v(TAG, "Receive date: " + date.toString());
        HashMap<Integer, Date> mapDateDay = new HashMap<>();

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
        c.add(Calendar.DATE, -i - 7);
        Date start = c.getTime();
        mapDateDay.put(1, start);
        Log.v(TAG, "Start week date: " + start.toString());

        //Uses for loop to map each days to a date for current week selected
        for (int days = 1; days < 7; days++) {
            c.add(Calendar.DATE, 1);
            Date end = c.getTime();
            mapDateDay.put(days, end);
            Log.v(TAG, "Adding hashmap date: " + days + " "+ end.toString());

        }

        return mapDateDay;
    }

    /**
     * Callback interface
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

    public List<Integer> ConvertSecondsToTime(int TotalInSeconds) {
        if (TotalInSeconds != 0) {
            int hours = (TotalInSeconds) / 3600;
            int minutes = ((TotalInSeconds) % 3600) / 60;
            int seconds = (TotalInSeconds) % 60;
            return Arrays.asList(hours, minutes, seconds);

        }
        return Arrays.asList(0, 0, 0);
    }


}

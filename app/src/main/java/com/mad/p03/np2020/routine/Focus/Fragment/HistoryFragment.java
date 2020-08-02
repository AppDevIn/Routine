package com.mad.p03.np2020.routine.Focus.Fragment;

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

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.mad.p03.np2020.routine.Focus.Adapter.FocusAdapter;
import com.mad.p03.np2020.routine.R.color;
import com.mad.p03.np2020.routine.Focus.Interface.FocusDBObserver;
import com.mad.p03.np2020.routine.Focus.Model.Focus;
import com.mad.p03.np2020.routine.Focus.Model.ResizeableButton;
import com.mad.p03.np2020.routine.models.User;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.helpers.DividerItemDecoration;
import com.mad.p03.np2020.routine.Focus.DAL.FocusDBHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.threeten.bp.DateTimeUtils;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * History fragment used to manage the fragment of Focus layout section
 *
 * @author Lee Quan Sheng
 * @since 02-06-2020
 */


public class HistoryFragment extends Fragment implements View.OnClickListener, FocusDBObserver {

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
    private int selectedDate = 1;

    //used to keep track the selected value, so that when database changed. The value won't be affected
    private Integer trackSelectValue = null;

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

    @Override
    public void onPause() {
        super.onPause();
        focusDBHelper.removeDbObserver(this);

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
            focusDBHelper = getArguments().getParcelable("FocusDatabase"); //Not in used
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
        AndroidThreeTen.init(getContext());

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

        dateRangeView = view.findViewById(R.id.dateRange);

        prevWeek = view.findViewById(R.id.weekSelectLeft);
        nextWeek = view.findViewById(R.id.weekSelectRight);

        toggle = view.findViewById(R.id.toggle);

        week = view.findViewById(R.id.week);
        day = view.findViewById(R.id.day);

        //RecyclerView for display history
        //Recycler View
        RecyclerView recyclerView = view.findViewById(R.id.recyclerHistory);


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
        nextWeek.setOnClickListener(this);
        prevWeek.setOnClickListener(this);
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
        //This is used to keep track of changes to database
        user.readFocusFirebase(getContext(), this);

        textFragment.setText("My Focus History");
        Date date = new Date();
        Log.v(TAG, "Start before Change Date init: " + date);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        date = cal.getTime();
        Log.v(TAG, "Start Date init: " + date);

        LocalDateTime ldt = LocalDateTime.ofInstant(DateTimeUtils.toInstant(date), org.threeten.bp.ZoneId.systemDefault());

        mappingDate = getMapWeek(ldt);

        setArrow();
        displayWeek();

    }

    /**
     * The setArrow() method is automatically set the view of the button based on the condition
     *
     * How it works:
     * The Arrow will be visible when the selected day/week is within the current date and first use of the focus
     * The Arrow will be invisible when the week has reached where there isn't any existing data
     */
    public void setArrow(){
        Date min = user.getMinFocus();
        if(min.before(mappingDate.get(1))){
            prevWeek.setVisibility(View.VISIBLE);
        }else{
            Log.v(TAG, "Change Color");
            prevWeek.setVisibility(View.INVISIBLE);
        }

        Date max = new Date();
        if(max.after(mappingDate.get(7))){
            nextWeek.setVisibility(View.VISIBLE);
        }else{
            Log.v(TAG, "Change Color");
            nextWeek.setVisibility(View.INVISIBLE);
        }
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
    public void updateTask(ArrayList<Focus> focusArrayList) {
        Log.v(TAG, "Update task: " + focusArrayList);

        usIndicator.setText(String.valueOf(user.getmUnsuccessFocusList(focusArrayList).size()));
        sIndicator.setText(String.valueOf(user.getmSuccessFocusList(focusArrayList).size()));

        int totalSeconds = user.getTotalHours();
        List<Integer> hrMinSec = ConvertSecondsToTime(totalSeconds);
        totalHours.setText(String.format(Locale.US, "Total: %dHr %dMin %dSec", hrMinSec.get(0), hrMinSec.get(1), hrMinSec.get(2)));

        if (focusArrayList.size() == 0) {
            nothing.setVisibility(View.VISIBLE);
        }else{
            nothing.setVisibility(View.INVISIBLE);
        }
        focusAdapter.updateList(focusArrayList);
    }

    /**
     * Called when the fragment is no longer attached to its activity to detach the fragment
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /***
     * The onclick method consist of buttons where user select their preferred date/week to filter the data
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.weekSelectLeft:
                try {
                    setPrevWeek();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.weekSelectRight:
                try {
                    setNextWeek();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.week:
                displayWeek();
                break;
            case R.id.day:
                if(previousButton == null) previousButton = mon;
                displayResult(selectedDate, previousButton);
                break;
            case R.id.mondayButton:
                displayResult(1, mon);
                break;

            case R.id.tuesdayButton:
                displayResult(2, tue);
                break;

            case R.id.wednesdayButton:
                displayResult(3, wed);
                break;

            case R.id.thursdayButton:
                displayResult(4, thr);
                break;

            case R.id.fridayButton:
                displayResult(5, fri);
                break;

            case R.id.saturdayButton:
                displayResult(6, sat);
                break;

            case R.id.sundayButton:
                displayResult(7, sun);
                break;
        }
    }

    /****
     * This function is used to get the previous week of the current week
     */

    public void setPrevWeek() throws ParseException {
        trackSelectValue = null;

        Log.v(TAG, "Setting prev week");
        Date min = user.getMinFocus();
        if(min.before(mappingDate.get(1))){
            Calendar cal = Calendar.getInstance();
            cal.setTime(mappingDate.get(1));
            cal.add(Calendar.DATE, -1);
            Date newDate = cal.getTime();

            LocalDateTime ldt = LocalDateTime.ofInstant(DateTimeUtils.toInstant(newDate), org.threeten.bp.ZoneId.systemDefault());

            mappingDate = getMapWeek(ldt);
            displayWeek();

        }
        setArrow();

    }

    /****
     * This function is used to get the next week of the current week
     */

    public void setNextWeek() throws ParseException {
        Log.v(TAG, "Setting next week");
        trackSelectValue = null;

        Date max = new Date();
        if(max.after(mappingDate.get(7))){
            Calendar cal = Calendar.getInstance();
            cal.setTime(mappingDate.get(7));
            cal.add(Calendar.DATE, 1);
            Date newDate = cal.getTime();
            LocalDateTime ldt = LocalDateTime.ofInstant(DateTimeUtils.toInstant(newDate), org.threeten.bp.ZoneId.systemDefault());

            mappingDate = getMapWeek(ldt);
            displayWeek();

        }
        setArrow();

    }


    /***
     * This is a method which the button is used to get which button is clicked to know what to filter
     *
     * @param i
     * @param id
     */
    public void displayResult(int i, ResizeableButton id) {

        try {

            trackSelectValue = i; // this is used to track the current selected value for LiveUpdateChange filter

            selectedDate = i; //Select filter day

            previousButton = id; // this is used to track the current selected button id so that it can be reset when a new button is clicked
            Log.v(TAG, "Button Pressed " + i + " on " + id);
            Log.v(TAG, "Date Hashmap " + mappingDate);

            //Setting the selected color
            id.setTextColor(getResources().getColor(color.pastelBlue));
            toggle.check(R.id.day);
            Date selectedDate = mappingDate.get(i);
            ArrayList<Focus> listUpdate = user.getmFocusList(selectedDate);
            focusAdapter.updateList(listUpdate);

            updateTask(listUpdate);
        }catch (Exception e){
            Log.v(TAG, e.getLocalizedMessage());
        }
    }

    /***
     * This is used to display the current week
     */
    public void displayWeek() {
        try {
            trackSelectValue = null;

            if (previousButton != null) {
                previousButton.setTextColor(getResources().getColor(color.black));
            }

            Log.v(TAG, "Date Hashmap " + mappingDate);

            toggle.check(R.id.week);
            Date startDate = mappingDate.get(1);
            Date endDate = mappingDate.get(7);

            //Calling the getmFocusList method where you can filter the date range
            ArrayList<Focus> listUpdate = user.getmFocusList(startDate, endDate);

            String pattern = "dd MMM";
            DateFormat df = new SimpleDateFormat(pattern);

            String dateAsStart = df.format(startDate);
            String dateAsEnd = df.format(endDate);


            dateRangeView.setText(dateAsStart + " - " + dateAsEnd);

            updateTask(listUpdate);
        }catch (Exception e){
            Log.v(TAG, e.getLocalizedMessage());
        }


    }

    /***
     * Get the current list of date of the current week and store in a list
     * @param date The parameter is used to pass in the date so that the method can retrieve the first and last day of the stated date week
     * @return
     */
    public HashMap<Integer, Date> getMapWeek(LocalDateTime date) {

        Log.v(TAG, "Receive date: " + date.toString());
        HashMap<Integer, Date> mapDateDay = new HashMap<>();

        Calendar c = Calendar.getInstance(Locale.UK);
        c.set(Calendar.MONTH, date.getMonthValue()-1);
        c.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth()-1);
        c.set(Calendar.YEAR, date.getYear());

        int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();
        Log.v(TAG, "Minus date: " + i);

        c.add(Calendar.DATE, -i);
        Date start = c.getTime();
        mapDateDay.put(1, start);
        Log.v(TAG, "Start week date: " + start.toString());

        //Uses for loop to map each days to a date for current week selected
        for (int days = 2; days < 8; days++) {
            c.add(Calendar.DATE, 1);
            Date end = c.getTime();
            mapDateDay.put(days, end);
            Log.v(TAG, "Adding hashmap date: " + days + " "+ end.toString());

        }

        return mapDateDay;
    }

    /***
     * SQL Database onChange Listener
     */
    @Override
    public void onDatabaseChanged() {
        Log.v(TAG, "DATABASE UPDATED TO OTHER SERVER");
        getLocalFocus();
        focusAdapter.notifyDataSetChanged();
    }

    /***
     * This is used to get the reset the history due to onDatabaseChange
     */
    private void getLocalFocus() {
        try {
            user.renewFocusList();
        }catch (Exception e){
            Log.v(TAG, "Error Exception " +  e.getLocalizedMessage());
        }
        if(trackSelectValue == null){
            //get current week
            displayWeek();
        }else{
            //get current day

            displayResult(trackSelectValue, previousButton);
        }
    }

    /**
     * Callback interface
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

    /***
     * Convert seconds to hour min seconds
     * @param TotalInSeconds This parameter is to pass in the milliseconds so that it can convert accordingly
     * @return
     */
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

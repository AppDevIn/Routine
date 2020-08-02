package com.mad.p03.np2020.routine.Calender.CustomCalender;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Calender.CustomCalender.GridViewAdapter;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.models.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;

/**
 *
 * This is a custom view for calender
 *
 *
 * @author Jeyavishnu
 * @since 02-08-2020
 *
 */
public class CustomCalenderView extends LinearLayout implements View.OnClickListener {


    final String TAG = "CalenderLayout";

    TextView txtCalender;
    GridView mGridView;
    private static final int MAX_CALENDER_DAYS = 42;
    Calendar mCalender = Calendar.getInstance(Locale.ENGLISH);
    Context mContext;

    List<Date> dates = new ArrayList<>();
    List<Task> taskList = new ArrayList<>();

    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.ENGLISH);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);

    GridViewAdapter mGridViewAdapter;

    DateChangeListener mDateChangeListener;


    public CustomCalenderView(Context context) {
        super(context);
    }

    /**
     * To init the custom view
     * @param context The context where the object is used
     * @param attrs
     */
    public CustomCalenderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        IntializeLayout();
        SetUpCalender();


    }

    public CustomCalenderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    /**
     * Click listener for all the clicks in this
     * custom calender view
     * @param view The view that is being clicked
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.calender_picker:
                //Call the datePicker Method
                ChooseDate();
                break;
        }

    }


    /**
     * The init the layout and find id from that layout
     * and set click listener
     */
    private void IntializeLayout(){

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calender_layout, this);
        mGridView = view.findViewById(R.id.calendarView);
        txtCalender = view.findViewById(R.id.calender_picker);

        txtCalender.setOnClickListener(this);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //Will be used as the parameter
                Calendar tempCalender = (Calendar) mCalender.clone();


                mCalender.setTime(dates.get(position));
                //If not null trigger the listener
                if(mDateChangeListener != null){
                    mDateChangeListener.onDateChange(mCalender.getTime());
                }

                UpdateCalender(tempCalender);


            }
        });


    }

    /**
     * Set up the grid view with 48 values
     */
    private void SetUpCalender(){
        String currentDate = dateFormat.format(mCalender.getTime());
        txtCalender.setText(currentDate);
        dates.clear();;
        Calendar monthCalendar = (Calendar) mCalender.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int FirstDayofMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayofMonth);


        while (dates.size() < MAX_CALENDER_DAYS){
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        mGridViewAdapter = new GridViewAdapter(mContext, dates, mCalender );
        mGridView.setAdapter(mGridViewAdapter);

    }


    /**
     * This is to trigger the datetime picker
     */
    private void ChooseDate() {
        Log.v(TAG, "Date Button Pressed!");

        Calendar calendar = Calendar.getInstance();

        calendar.set(2020,6, 15);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);



        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog_MinWidth, null, year, month, day);
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "SET", (dialog, which) -> {

            //Will be used as the parameter
            Calendar tempCalender = (Calendar) mCalender.clone();


            //Set the date to the calender
            mCalender.set(Calendar.YEAR, datePickerDialog.getDatePicker().getYear());
            mCalender.set(Calendar.MONTH, datePickerDialog.getDatePicker().getMonth());
            mCalender.set(Calendar.DAY_OF_MONTH, datePickerDialog.getDatePicker().getDayOfMonth());


            //If not null trigger the listener
            if(mDateChangeListener != null){
                mDateChangeListener.onDateChange(mCalender.getTime());
            }

            //Re create the calender
            UpdateCalender(tempCalender);
        });


    }

    /**
     * Calculate all the values and than
     * re set the values in the gridview and notify
     * the change
     * @param calendar
     */
    private void UpdateCalender(Calendar calendar) {


        int displayMonth = calendar.get(Calendar.MONTH) + 1;
        int currentMonth = mCalender.get(Calendar.MONTH) + 1;

        if(displayMonth != currentMonth) {
         SetUpCalender();
        }else{
            //Update the calender date
            String currentDate = dateFormat.format(mCalender.getTime());
            txtCalender.setText(currentDate);

            //Update the color
            mGridViewAdapter.setCurrentDate(mCalender);
            mGridViewAdapter.notifyDataSetChanged();
        }




    }


    public Date getDate(){
        return mCalender.getTime();
    }

    public void setDateListener(DateChangeListener dateChangeListener){
        this.mDateChangeListener = dateChangeListener;
    }

    public void notifyData(){
        SetUpCalender();
    }

}

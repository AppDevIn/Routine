package com.mad.p03.np2020.routine.Calender;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.models.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;

public class CustomCalenderView extends LinearLayout {


    TextView txtCalender;
    GridView mGridView;
    private static final int MAX_CALENDER_DAYS = 42;
    Calendar mCalender = Calendar.getInstance(Locale.ENGLISH);
    Context mContext;

    List<Date> dates = new ArrayList<>();
    List<Task> taskList = new ArrayList<>();

    SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM YYYY", Locale.ENGLISH);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);

    GridViewAdapter mGridViewAdapter;

    public CustomCalenderView(Context context) {
        super(context);
    }

    public CustomCalenderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;



        IntializeLayout();
        SetUpCalender();


    }

    public CustomCalenderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void IntializeLayout(){

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calender_layout, this);
        mGridView = view.findViewById(R.id.calendarView);
        txtCalender = view.findViewById(R.id.calender_picker);


    }

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

        mGridViewAdapter = new GridViewAdapter(mContext, dates, mCalender, taskList );
        mGridView.setAdapter(mGridViewAdapter);

    }
}

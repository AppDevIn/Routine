package com.mad.p03.np2020.routine.Calender.CustomCalender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mad.p03.np2020.routine.DAL.TaskDBHelper;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.helpers.MyDatabaseListener;
import com.mad.p03.np2020.routine.models.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

/**
 *
 * This is part of the custom calender to show all the dates
 *
 * @author Jeyavishnu
 * @since 02-08-2020
 *
 */
class GridViewAdapter extends ArrayAdapter {

    List<Date> dates;
    Calendar selectedDSate, currentDate;
    List<Task> taskList;
    LayoutInflater mInflater;
    public View view;

    final private String TAG = "Calender";


    /**
     * The constructor
     * @param context The context this view is used
     * @param dates The dates list
     * @param selectedDSate The date that is currently selected
     */
    public GridViewAdapter(@NonNull Context context, List<Date> dates, Calendar selectedDSate) {
        super(context, R.layout.single_cell_layout);
        this.dates = dates;
        this.selectedDSate = selectedDSate;
        this.taskList = new TaskDBHelper(getContext()).getAllTask();;

        mInflater = LayoutInflater.from(context);


    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        Date monthDate = dates.get(position);
        Calendar dateCalender = Calendar.getInstance();
        dateCalender.setTime(monthDate);
        int dayNo = dateCalender.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalender.get(Calendar.MONTH) + 1;
        int displayYear = dateCalender.get(Calendar.YEAR);
        int currentMonth = selectedDSate.get(Calendar.MONTH) + 1;
        int currentYear = selectedDSate.get(Calendar.YEAR);

        view = convertView;

        if(view == null){
            view = mInflater.inflate(R.layout.single_cell_layout, null);
        }

        TextView txtDay = view.findViewById(R.id.calendar_day);
        LinearLayout linearLayout = view.findViewById(R.id.llDates);
        ImageView imageView = view.findViewById(R.id.alertEvent);


        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Task task:
             taskList) {

            if(task.getRemindDate() != null && (dateFormat.format(task.getRemindDate()).equals(dateFormat.format(monthDate))))
                imageView.setVisibility(View.VISIBLE);


        }





        linearLayout.setBackground(null);
        txtDay.setTextColor(view.getContext().getResources().getColor(R.color.black));
        if(displayMonth == currentMonth && displayYear == currentYear){


            if(dayNo == selectedDSate.get(Calendar.DAY_OF_MONTH)){
                txtDay.setTextColor(view.getContext().getResources().getColor(R.color.white));
                linearLayout.setBackground(view.getContext().getResources().getDrawable(R.drawable.calender_img_dot_normal));
            }else {
                txtDay.setTextColor(view.getContext().getResources().getColor(R.color.black));

                if( dateFormat.format(dateCalender.getTime()).equals(dateFormat.format(Calendar.getInstance().getTime())) ){
                    txtDay.setTextColor(view.getContext().getResources().getColor(R.color.red));
                }


            }

        }
        else
        {
            txtDay.setTextColor(getContext().getResources().getColor(R.color.grey));
        }

        txtDay.setText(String.valueOf(dayNo));

        return view;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public int getPosition(@Nullable Object item) {
        return dates.indexOf(item);
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    /**
     * To set the current date
     * @param selectedDSate
     */
    public void setCurrentDate(Calendar selectedDSate) {
        this.selectedDSate = selectedDSate;
    }



}

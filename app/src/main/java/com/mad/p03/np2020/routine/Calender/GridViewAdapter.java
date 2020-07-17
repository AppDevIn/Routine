package com.mad.p03.np2020.routine.Calender;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.models.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class GridViewAdapter extends ArrayAdapter {

    List<Date> dates;
    Calendar currentDate;
    List<Task> taskList;
    LayoutInflater mInflater;


    public GridViewAdapter(@NonNull Context context, List<Date> dates, Calendar currentDate, List<Task> taskList) {
        super(context, R.layout.single_cell_layout);
        this.dates = dates;
        this.currentDate = currentDate;
        this.taskList = taskList;

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
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);

        View view = convertView;

        if(view == null){
            view = mInflater.inflate(R.layout.single_cell_layout, null);
        }

        TextView txtDay = view.findViewById(R.id.calendar_day);

        if(displayMonth == currentMonth && displayYear == currentYear){


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
}

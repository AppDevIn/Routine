package com.mad.p03.np2020.routine.Class;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mad.p03.np2020.routine.R;

public class habitListAdapter extends ArrayAdapter<HabitTracker> {

    private static final String TAG = "habitListAdapter";

    private Context mContext;
    private int mResource;

    private TextView _title;
    private TextView _count1;
    private TextView _count2;
    private TextView _occurrence;

    public habitListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<HabitTracker> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String title = getItem(position).getTitle();
        int occurrence = getItem(position).getOccurrence();
        int count = getItem(position).getCount();

        HabitTracker habit = new HabitTracker(title,occurrence,count);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent,false);

        _title = (TextView) convertView.findViewById(R.id.habitTitle);
        _occurrence = (TextView) convertView.findViewById(R.id.habitOccurence);
        _count1 = (TextView) convertView.findViewById(R.id.habitCount);
        _count2 = (TextView) convertView.findViewById(R.id.habitCount2);

        _title.setText(title);
        _occurrence.setText(String.valueOf(occurrence));
        _count1.setText(String.valueOf(count));
        _count2.setText(String.valueOf(count));

        return convertView;
    }
}

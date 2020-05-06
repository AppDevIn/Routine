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

    private static class ViewHolder {
        TextView title;
        TextView count1;
        TextView count2;
        TextView occurrence;
    }

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

        ViewHolder holder;

        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent,false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.habitTitle);
            holder.occurrence = (TextView) convertView.findViewById(R.id.habitOccurence);
            holder.count1 = (TextView) convertView.findViewById(R.id.habitCount);
            holder.count2 = (TextView) convertView.findViewById(R.id.habitCount2);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(habit.getTitle());
        holder.occurrence.setText(String.valueOf(habit.getOccurrence()));
        holder.count1.setText(String.valueOf(habit.getCount()));
        holder.count2.setText(String.valueOf(habit.getCount()));

        return convertView;
    }
}

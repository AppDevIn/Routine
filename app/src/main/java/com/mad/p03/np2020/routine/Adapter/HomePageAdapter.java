package com.mad.p03.np2020.routine.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HomePageAdapter extends ArrayAdapter {

    List<Section> mSectionList = new ArrayList<>();

    public HomePageAdapter(Context context, int textViewResourceId, List objects) {
        super(context, textViewResourceId, objects);

        mSectionList = objects;
    }


    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Local variables
        TextView textView;
        Button btnBackground;


        View v = convertView;

        //Creating a new layout
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.home_grid_view_items, null);

        //Initialize variables
        textView = v.findViewById(R.id.listName);
        btnBackground = v.findViewById(R.id.backgroud);

        //***************** Set values into view *****************//

        //For the TextView
        textView.setText(mSectionList.get(position).getName());

        //For button background
        btnBackground.setBackgroundColor(mSectionList.get(position).getBackgroundColor());

        return  v;

    }
}

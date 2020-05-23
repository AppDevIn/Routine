package com.mad.p03.np2020.routine.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.p03.np2020.routine.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MySpinnerApater extends BaseAdapter {

    Integer[] mColors;
    public MySpinnerApater(Integer[] colors) {
        mColors = colors;
    }

    @Override
    public int getCount() {
        return mColors.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Inflating the layout for the custom Spinner
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View layout = inflater.inflate(R.layout.custom_spinner_color_item, parent, false);


        // Declaring button in the inflated layout
        CardView btnColor =  layout.findViewById(R.id.cardView);
        btnColor.setBackgroundColor(mColors[position]);


        return layout;
    }
}

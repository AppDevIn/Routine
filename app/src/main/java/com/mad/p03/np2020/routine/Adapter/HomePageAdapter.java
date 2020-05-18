package com.mad.p03.np2020.routine.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

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
        ImageView background;



        View v = convertView;

        //Creating a new layout
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.home_grid_view_items, null);

        //Initialize variables
        textView = v.findViewById(R.id.listName);
        background = v.findViewById(R.id.backgroud);

        //***************** Set values into view *****************//

        //For the TextView
        textView.setText(mSectionList.get(position).getName());

        //For background

        //Setup drawable
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(mSectionList.get(position).getBackgroundColor());
        shape.setCornerRadius(30);


        //Set the drawable
        background.setBackground(shape);


        return  v;

    }
}

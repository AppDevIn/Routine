package com.mad.p03.np2020.routine.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class HomePageAdapter extends ArrayAdapter {

    public HomePageAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }


    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}

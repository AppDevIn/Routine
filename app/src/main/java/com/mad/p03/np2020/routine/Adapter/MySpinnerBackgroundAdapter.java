package com.mad.p03.np2020.routine.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mad.p03.np2020.routine.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Not implemented
 */
public class MySpinnerBackgroundAdapter extends BaseAdapter {

    private Integer[] mBackground;

    public MySpinnerBackgroundAdapter(Integer[] background) {
        mBackground = background;
    }

    @Override
    public int getCount() {
        return mBackground.length;
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
        View layout = inflater.inflate(R.layout.spinner_background_items, parent, false);

        ImageView imageView = layout.findViewById(R.id.imgIcon);
        imageView.setImageResource(mBackground[position]);


        return layout;
    }
}

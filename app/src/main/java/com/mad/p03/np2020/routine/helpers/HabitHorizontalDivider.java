package com.mad.p03.np2020.routine.helpers;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class HabitHorizontalDivider extends RecyclerView.ItemDecoration {
    private int space;

    public HabitHorizontalDivider(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
    }
}
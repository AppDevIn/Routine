package com.mad.p03.np2020.routine.Habit.helpers;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 *
 * This created to add spacing between recycler
 *
 * @author Hou Man
 * @since 02-08-2020
 */

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
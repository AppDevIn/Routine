package com.mad.p03.np2020.routine.ViewHolder;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 *
 * Add spacing between recycler
 *
 * @author Jeyavishnu
 * @since 03-06-2020
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public DividerItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;


        //for vertical scrolling
        outRect.bottom = space;
        outRect.top = space;}
}
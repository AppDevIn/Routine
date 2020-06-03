package com.mad.p03.np2020.routine.Class;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 *
 * Model used to manage the section
 *
 * @author Lee Quan Sheng
 * @since 02-06-2020
 */

public class ItemDecoration extends RecyclerView.ItemDecoration {
    private int verticalSpaceHeight;
    private Drawable mDivider;

    /**
     * Method used to add a special drawing and layout offset to specific item views from the adapter's data set.
     * used for drawing dividers between items, highlights, visual grouping boundaries.
     *
     * @param verticalSpaceHeight set Vertical Space height in this context
     * @param mDivider set Divider in this context
     */
    public ItemDecoration(int verticalSpaceHeight, Drawable mDivider) {
        this.verticalSpaceHeight = verticalSpaceHeight;
        this.mDivider = mDivider;
    }

    /**
     *
     * Retrieve any offsets for the given item.
     * Each field of outRect specifies the number of pixels that the item view should be inset by, similar to padding or margin
     *
     * @param outRect set outRect in this context
     * @param view set view in this context
     * @param parent set parent in this context
     * @param state set state in this context
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = verticalSpaceHeight;
    }

    /**
     *
     * Draw appropriate decorations into the Canvas supplied to the RecyclerView.
     * Content drawn by this method will be drawn before the item views are drawn, and will thus appear underneath the views.
     *
     * @param canvas set Canvas to this content
     * @param parent set parent to this content
     * @param state set State to this content
     */
    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int dividerLeft = parent.getPaddingLeft();
        int dividerRight = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int dividerTop = child.getBottom() + params.bottomMargin;
            int dividerBottom = dividerTop + mDivider.getIntrinsicHeight();

            mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
            mDivider.draw(canvas);
        }
    }

}

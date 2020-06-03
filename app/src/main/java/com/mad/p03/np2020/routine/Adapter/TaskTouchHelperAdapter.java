package com.mad.p03.np2020.routine.Adapter;

import com.mad.p03.np2020.routine.ViewHolder.TaskViewHolder;

/**
 *
 * This is used to be a trigger when it
 * is move or swiped or clicked
 *
 * @author Jeyavishnu
 * @since 02-06-2020
 */
public interface TaskTouchHelperAdapter {
    /**
     * @param fromPosition int where the item was
     * @param toPosition int where the item is currently at
     */
    void onItemMove(int fromPosition, int toPosition);
    /**
     * @param position This is the position it was swiped from
     */
    void onItemSwiped(int position);
    /**
     * @param position the position of item that was clicked
     */
    void onItemClicked(int position);
}

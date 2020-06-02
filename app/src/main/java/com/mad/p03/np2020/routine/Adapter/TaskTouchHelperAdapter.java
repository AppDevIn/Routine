package com.mad.p03.np2020.routine.Adapter;

import com.mad.p03.np2020.routine.ViewHolder.TaskViewHolder;

public interface TaskTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);
    void onItemSwiped(int position);
    void onItemClicked(int position);
}

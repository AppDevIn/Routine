package com.mad.p03.np2020.routine.Interface;

/**
 *
 * This is used to set onClickListener on each holder by parsing position from the adapter.
 *
 * @author Hou Man
 * @since 02-06-2020
 */


public interface HabitItemClickListener {
    /**
     *
     * This method is an "abstract" method used to set onClickListener on each holder by parsing position from the adapter.
     *  This method will be override later on.
     *
     * @param position This parameter is used to parse the position.
     *
     * */
    void onHabitItemClick(int position);
}

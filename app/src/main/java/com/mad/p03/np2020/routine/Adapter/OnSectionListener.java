package com.mad.p03.np2020.routine.Adapter;


/**
 *
 * This can be implemented to be a receiver
 * of a trigger when the section is clicked
 *
 * @author Jeyavishnu
 * @since 02-06-2020
 */
public interface OnSectionListener {
    /**
     * @param position The item position it was clicked from
     */
    void onSectionClick(int position);
}

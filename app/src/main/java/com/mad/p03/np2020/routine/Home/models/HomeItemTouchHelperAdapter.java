package com.mad.p03.np2020.routine.Home.models;


/**
 *
 * This is used to be a trigger when it
 * is move or swiped
 *
 * @author Jeyavishnu
 * @since 02-06-2020
 */
public interface HomeItemTouchHelperAdapter {

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

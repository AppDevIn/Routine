package com.mad.p03.np2020.routine.Adapter;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 * Creating a customer Item Touch which lets us control
 * the touch behaviours are enabled in each view holder
 * and receive callback when they are user perform those actions
 *
 * @author Jeyavishnu
 * @since 02-06-2020
 */

public class MyHomeItemTouchHelper extends ItemTouchHelper.Callback{

    //Member variable
    private final HomeItemTouchHelperAdapter mAdapter;

    /**
     *
     * Set the adapter to a member variable
     *
     * @param adapter The interface that allows see the trigger
     */
    public MyHomeItemTouchHelper(HomeItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    /**
     * @return Whether ItemTouchHelper should start a swipe operation if a pointer is swiped over the View.
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * @return Whether ItemTouchHelper should start a swipe operation if a pointer is swiped over the View.
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    /**
     *
     *
     * Called by the ItemTouchHelper when the user interaction with an
     * element is over and it also completed its animation.
     *
     * This is where I will change the background color back to default
     *
     * @param recyclerView  The RecyclerView which is controlled by the ItemTouchHelper
     * @param viewHolder  The View that was interacted by the user.
     */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        //TODO: Reset the color
    }

    /**
     *
     * When the the state of the view holder
     * is drag i will change the background color
     *
     * @param viewHolder The new ViewHolder that is being swiped or dragged. Might be null if it is cleared.
     * @param actionState Tell your the state of the view holder
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(actionState == ItemTouchHelper.ACTION_STATE_DRAG){
            //TODO: Color when dragged
        }
    }


    /**
     *
     * This will return the flags enabled
     * which is able to drag up, down, left and right
     * able to swipe left if the position is a even
     * and swipe right if the position is an odd
     * number
     *
     * @param recyclerView  The RecyclerView to which ItemTouchHelper is attached.
     * @param viewHolder The ViewHolder for which the movement information is necessary.
     * @return The flags specifying which movements are allowed on this ViewHolder.
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = ItemTouchHelper.END;

        //When the item is in the left side
        if(viewHolder.getAdapterPosition()%2 == 0)
             swipeFlags = ItemTouchHelper.START;

        return makeMovementFlags(dragFlags, swipeFlags);
    }


    /**
     *
     * This is called when the itemTouchHelper wants to move or drag the item
     * from its old position to the new position. This is where the i will trigger
     * the {@code onItemMove(int,int)}
     *
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to
     * @param viewHolder The ViewHolder which is being dragged by the user.
     * @param target The ViewHolder over which the currently active item is being dragged.
     * @return True if the viewHolder has been moved to the adapter position of target
     */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    /**
     *
     * Called when a ViewHolder is swiped by the user. This is where we will
     * trigger the {@code onItemSwiped(int)}
     *
     * @param viewHolder The ViewHolder which has been swiped by the user.
     * @param direction The direction to which the ViewHolder is swiped.
     */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemSwiped(viewHolder.getAdapterPosition());
    }


}

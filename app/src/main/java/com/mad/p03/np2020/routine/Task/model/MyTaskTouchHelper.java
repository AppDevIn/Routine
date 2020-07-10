package com.mad.p03.np2020.routine.Task.model;
<<<<<<< HEAD:app/src/main/java/com/mad/p03/np2020/routine/Task/model/MyTaskTouchHelper.java
=======

import com.mad.p03.np2020.routine.Task.model.TaskTouchHelperAdapter;
>>>>>>> master:app/src/main/java/com/mad/p03/np2020/routine/Task/model/MyTaskTouchHelper.java

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
public class MyTaskTouchHelper extends ItemTouchHelper.Callback {

    //Member variable
    private final TaskTouchHelperAdapter mTaskTouchHelperAdapter;

    /**
     *
     * Set the adapter to a member variable
     *
     * @param taskTouchHelperAdapter The interface that allows see the trigger the callback
     */
    public MyTaskTouchHelper(TaskTouchHelperAdapter taskTouchHelperAdapter) {
        mTaskTouchHelperAdapter = taskTouchHelperAdapter;
    }

    /**
     * Tell task if is able start a drag and drop operation if an item is on long pressed
     * @return if it is allowed to start dragging
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     * Tells task if is able to start a swipe operation if a pointer is swiped over the View.
     * @return if it is allowed to start swiping
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
     * which is able to drag up and down
     *
     * Is able to swipe right only
     *
     * @param recyclerView  The RecyclerView to which ItemTouchHelper is attached.
     * @param viewHolder The ViewHolder for which the movement information is necessary.
     * @return The flags specifying which movements are allowed on this ViewHolder.
     */
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.END;

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
        mTaskTouchHelperAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return false;
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
        mTaskTouchHelperAdapter.onItemSwiped(viewHolder.getAdapterPosition());
    }
}

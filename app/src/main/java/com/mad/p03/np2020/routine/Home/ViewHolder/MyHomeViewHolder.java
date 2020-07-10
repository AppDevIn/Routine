package com.mad.p03.np2020.routine.Home.ViewHolder;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Card.CardActivity;
import com.mad.p03.np2020.routine.Home.models.HomeItemTouchHelperAdapter;
import com.mad.p03.np2020.routine.R;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;



/**
 *
 *
 * To be used with the adapter HomePageAdapter. This
 * holds reference to the id of the view resource. This
 * is where we implement onTouchListener and onGestureListener
 * to listen to swipes, clicks and drag.
 *
 * @author Jeyavishnu
 * @since 04-06-2020
 */
public class MyHomeViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener {

    public TextView mTextViewListName;
    public LinearLayout mBackGround;
    public ImageView mimgIcon;

    //Listener
    private HomeItemTouchHelperAdapter mHomeItemTouchHelperAdapter;
    private GestureDetector mGestureDetector;
    private ItemTouchHelper mTouchHelper;

    private static  String TAG = "HomeAdapter";

    /**
     *
     * To get the the custom Item touch helper and and the trigger and
     * set it to the local variable. initialize the gesture detector
     *
     * @param v The view that has been inflated
     * @param touchHelper My custom touch listener
     * @param homeItemTouchHelperAdapter Used to trigger the listener and pass the data along
     */
    public MyHomeViewHolder(@NonNull View v, ItemTouchHelper touchHelper, HomeItemTouchHelperAdapter homeItemTouchHelperAdapter) {
        super(v);
        //Initialize variables
        mTextViewListName = v.findViewById(R.id.listName);
        mBackGround = v.findViewById(R.id.backgroud);
        mimgIcon = v.findViewById(R.id.todoIcon);


        this.mHomeItemTouchHelperAdapter = homeItemTouchHelperAdapter;
        this.mTouchHelper = touchHelper;

        //Initialize the gesture detector
        mGestureDetector = new GestureDetector(v.getContext(), this);
        itemView.setOnTouchListener(this);
    }

    /**
     *
     * Not implemented
     *
     * Notify when the a tap occurs on the view. This will
     * trigger immediately for every down event
     *
     * @param motionEvent The down motion event.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        Log.d(TAG, "onDown: Clicked Down");
        return false;
    }

    /**
     * Not implemented
     *
     * The user has performed a down MotionEvent and not performed a move or up yet.
     * This event is commonly used to provide visual feedback to the user to let them
     * know that their action has been recognized i.e. highlight an element.
     *
     * @param motionEvent The down motion event
     */
    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    /**
     *
     * This to notify when the tap occurs with the up motion triggered.
     * This is where we trigger {@code onItemClicked(int position) } and pass the position
     *
     * @param motionEvent The up motion event that completed the first tap
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        Log.d(TAG, "onSingleTapUp: Clicked and the motion is up");
        mHomeItemTouchHelperAdapter.onItemClicked(getAdapterPosition());
        return false;
    }

    /**
     *
     * Not Implemented
     *
     * Notified when a scroll occurs with the initial on down MotionEvent and the current move MotionEvent.
     * The distance in x and y is also supplied for convenience.
     *
     * @param motionEvent The first down motion event that started the scrolling.
     * @param motionEvent1 The move motion event that triggered the current onScroll.
     * @param v The distance along the X axis that has been scrolled since the last call
     *          to onScroll. This is NOT the distance between e1 and e2.
     * @param v1  The distance along the Y axis that has been scrolled since the last call to onScroll.
     *            This is NOT the distance between e1 and e2.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Log.d(TAG, "onScroll(): Scrolling ");
        return false;
    }

    /**
     *
     * Notified when a long press occurs with the initial on down MotionEvent that trigged it.
     * Use this enable dragging on custom view holder (this)
     *
     * @param motionEvent The initial on down motion event that started the long press.
     */
    @Override
    public void onLongPress(MotionEvent motionEvent) {
        Log.d(TAG, "onLongPress(): Drag enabled for this custom view holder ");
        mTouchHelper.startDrag(this);
    }

    /**
     *
     * Not implemented
     *
     * Notified of a fling event when it occurs with the initial on down MotionEvent and the matching up MotionEvent.
     * The calculated velocity is supplied along the x and y axis in pixels per second.
     *
     * @param motionEvent  The first down motion event that started the fling.
     * @param motionEvent1 The move motion event that triggered the current onFling.
     * @param v The velocity of this fling measured in pixels per second along the x axis.
     * @param v1 The velocity of this fling measured in pixels per second along the y axis.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }


    /**
     *
     * Called when a touch event is dispatched to a view. This allows listeners to get a chance to respond
     * before the target view. This is used to handle touch screen motion events {@code onTouchEvent(motionEvent)}
     *
     * @param view The view the touch event has been dispatched to.
     * @param motionEvent The MotionEvent object containing full information about the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
}

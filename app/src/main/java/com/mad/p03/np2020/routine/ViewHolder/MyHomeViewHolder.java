package com.mad.p03.np2020.routine.ViewHolder;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Adapter.OnSectionListener;
import com.mad.p03.np2020.routine.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MyHomeViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener {

    public TextView mTextViewListName;
    public ImageView mImgBackGround;
    public ImageView mimgIcon;

    //Listener
    private OnSectionListener mOnSectionListener;
    GestureDetector mGestureDetector;
    ItemTouchHelper mTouchHelper;

    public MyHomeViewHolder(@NonNull View v, OnSectionListener onSectionListener, ItemTouchHelper touchHelper) {
        super(v);
        //Initialize variables
        mTextViewListName = v.findViewById(R.id.listName);
        mImgBackGround = v.findViewById(R.id.backgroud);
        mimgIcon = v.findViewById(R.id.todoIcon);

        this.mOnSectionListener = onSectionListener;
        this.mTouchHelper = touchHelper;

        mGestureDetector = new GestureDetector(v.getContext(), this);
        itemView.setOnTouchListener(this);

    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        mOnSectionListener.onSectionClick(getAdapterPosition());
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        mTouchHelper.startDrag(this);
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        mGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
}

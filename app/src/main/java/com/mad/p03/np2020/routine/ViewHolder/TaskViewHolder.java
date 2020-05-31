package com.mad.p03.np2020.routine.ViewHolder;

import android.content.DialogInterface;
import android.text.method.Touch;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.mad.p03.np2020.routine.Adapter.TaskTouchHelperAdapter;
import com.mad.p03.np2020.routine.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener {

    public TextView mListName;
    public CheckBox mCheckBox;
    public ViewSwitcher mViewSwitcher, mViewSwitcherTaskName;
    public Button mBtnAdd;
    public EditText mEdTask;

    ItemTouchHelper mItemTouchHelper;
    GestureDetector mGestureDetector;
    private final TaskTouchHelperAdapter mTaskTouchHelperAdapter;

    public TaskViewHolder(@NonNull View itemView, ItemTouchHelper itemTouchHelper, TaskTouchHelperAdapter taskTouchHelperAdapter) {
        super(itemView);

        //Find the id
        mListName = itemView.findViewById(R.id.txtListName);
        mCheckBox = itemView.findViewById(R.id.checkbox);
        mViewSwitcher = itemView.findViewById(R.id.viewswitcher);
        mBtnAdd = itemView.findViewById(R.id.btnAdd);
        mViewSwitcherTaskName = itemView.findViewById(R.id.viewswitcherTaskName);
        mEdTask = itemView.findViewById(R.id.edTask);

        this.mItemTouchHelper = itemTouchHelper;

        mGestureDetector = new GestureDetector(itemView.getContext(), this);
        itemView.setOnTouchListener(this);

        this.mTaskTouchHelperAdapter = taskTouchHelperAdapter;
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
        mTaskTouchHelperAdapter.onItemClicked(getAdapterPosition());
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        mItemTouchHelper.startDrag(this);
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

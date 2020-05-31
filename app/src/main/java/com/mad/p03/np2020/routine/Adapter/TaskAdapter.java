package com.mad.p03.np2020.routine.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.p03.np2020.routine.Class.Task;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.ViewHolder.TaskViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> implements TaskTouchHelperAdapter {

    private final String TAG = "TaskAaapter";

    //Member variables
    List<Task> mTaskList;

    //Listener
    private ItemTouchHelper mItemTouchHelper;


    public TaskAdapter(List<Task> taskList) {
        this.mTaskList = taskList;
    }

    public void setMyTaskTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.mItemTouchHelper = itemTouchHelper;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_items, parent, false);

        return new TaskViewHolder(view, mItemTouchHelper, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final TaskViewHolder holder, int position) {

        holder.mListName.setText(String.valueOf(position));


        //Change to the add icon for the last position
        if(position == 9){
            //Move the next view in the switcher which is a button
            holder.mViewSwitcher.showNext();

            //Set that button a click listener
            holder.mBtnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick(): User is clicking on the add button in task ");

                    //TODO: Change to the edit text
                    holder.mViewSwitcherTaskName.showNext();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Log.d(TAG, "onItemMove(): From: " + fromPosition + " To: " + toPosition);
    }

    @Override
    public void onItemSwiped(int position) {
        Log.d(TAG, "onItemSwiped(): Item swiped on position " + position);
    }

    @Override
    public void onItemClicked(int position) {
        Log.d(TAG, "onClick(): You have clicked on " + "{Number}" + " task");
    }
}

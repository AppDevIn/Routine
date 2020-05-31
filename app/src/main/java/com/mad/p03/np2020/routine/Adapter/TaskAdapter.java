package com.mad.p03.np2020.routine.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.Task;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.ViewHolder.TaskViewHolder;
import com.mad.p03.np2020.routine.database.MyDatabaseListener;
import com.mad.p03.np2020.routine.database.TaskDBHelper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> implements TaskTouchHelperAdapter {

    private final String TAG = "TaskAaapter";

    //Member variables
    Section mSection;
    Context mContext;
    TaskViewHolder mTaskViewHolder;
    List<Task> mTaskList;

    //Listener
    private ItemTouchHelper mItemTouchHelper;

    LifecycleOwner mOwner;


    public TaskAdapter(Section section, LifecycleOwner owner) {
        this.mSection = section;

        this.mOwner = owner;

        //Add into the list
        mTaskList = section.getTaskList();
        Log.d(TAG, "TaskAdapter: " + section.getTaskList());
    }

    public void setMyTaskTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.mItemTouchHelper = itemTouchHelper;

    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_items, parent, false);

        mContext = parent.getContext();

        return new TaskViewHolder(view, mItemTouchHelper, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final TaskViewHolder holder, int position) {

        Log.d(TAG, "onBindViewHolder: Running");

        mTaskViewHolder = holder;


        holder.mListName.setText(mTaskList.get(position).getName());



    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Log.d(TAG, "onItemMove(): From: " + fromPosition + " To: " + toPosition);

        Task fromTask = mTaskList.get(fromPosition);
        mTaskList.remove(fromTask);
        mTaskList.add(toPosition, fromTask);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemSwiped(int position) {
        Log.d(TAG, "onItemSwiped(): Item swiped on position " + position);

        //Delete from the local list
        removeTask(position);

    }

    @Override
    public void onItemClicked(int position) {
        Log.d(TAG, "onClick(): You have clicked on " + position + " task");


        //TODO: Move to the card layout
    }

    /**
     *
     * Triggered to add to the current adapter list
     * when it is added to the sql
     *
     * @param object given from the SQL when triggered
     *               for this the object is task
     */


    /**
     * Adding the task to
     * the list
     * @param task task that will be added to list
     */
    public void addItem(Task task, Context context){

        //Add to the SQLite
        task.addTask(context);

        //Add from firebase
        task.executeFirebaseUpload(mOwner);


        Log.d(TAG, "New Task added, " + task.toString());
    }

    /**
     * The place to delete the task in the list
     * @param position The position from the data will be removed from
     */
    public void removeTask(int position){
        Log.d(TAG, "Removing " + mTaskList.get(position));

        Task task = mTaskList.get(position);

        //Delete from firebase
        task.executeFirebaseDelete(mOwner);

        //Delete from SQL
        task.deleteTask(mContext);




    }




}

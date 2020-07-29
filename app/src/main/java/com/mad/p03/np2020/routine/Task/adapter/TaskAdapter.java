package com.mad.p03.np2020.routine.Task.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;


import com.mad.p03.np2020.routine.Card.CardActivity;
import com.mad.p03.np2020.routine.Task.model.TaskTouchHelperAdapter;
import com.mad.p03.np2020.routine.models.Check;
import com.mad.p03.np2020.routine.models.Task;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Task.ViewHolder.TaskViewHolder;
import com.mad.p03.np2020.routine.DAL.TaskDBHelper;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 *
 * This will be the controller glue between the viewholder and the model
 * This will inflate the the items for the sections to which will give us
 * the view from will be passed to the view holder TaskViewHolder
 *
 * In here you should be able to move, swipe click, add and delete the section
 *
 * @author Jeyavishnu
 * @since 03-06-2020
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> implements TaskTouchHelperAdapter {

    private final String TAG = "TaskAdapter";

    //Member variables
    private Context mContext;
    private List<Task> mTaskList;
    private LifecycleOwner mOwner;
    private TaskDBHelper mTaskDBHelper;


    //Listener
    private ItemTouchHelper mItemTouchHelper;


    /**
     *
     * Will set the object section and the lifecycle owner, give access to the
     * adapter's methods. Gets the list from section and save it to the member
     * variable and set owner into the member variable
     *
     * @param taskList the list of tasks
     * @param owner Owner of the lifecycle to be able to see
     *             lifecycle changes
     */
    public TaskAdapter(List<Task> taskList, LifecycleOwner owner) {

        this.mOwner = owner;
        mTaskList = taskList;

        Log.d(TAG, "TaskAdapter: " + mTaskList);
    }

    /**
     * This method is used to set the custom itemTouchHelper
     * to the member variable
     *
     * @param itemTouchHelper The custom touche helper that will be used
     *                        to controller to movie of the viewholder
     */
    public void setMyTaskTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.mItemTouchHelper = itemTouchHelper;

    }

    /**
     *
     * Called when RecyclerView needs a new View Holder of the given type to represent the task
     *
     * This ViewHolder will be constructed with a new view that will represent the task which consist
     * of name. The view will be inflated from XML file
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return MyHomeViewHolder with the view inflated
     */
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_items, parent, false);

        mContext = parent.getContext();
        mTaskDBHelper = new TaskDBHelper(parent.getContext());
        return new TaskViewHolder(view, mItemTouchHelper, this);
    }

    /**
     *
     * This will be called to display the task data at the specific position. This will update
     * the contents of the itemView to which will reflect at the given position
     *
     * @param holder The ViewHolder which should be updated to represent the contents of
     *               the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final TaskViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: Running");


        Task task = mTaskList.get(position);

        holder.mListName.setText(mTaskList.get(position).getName());
        holder.mCheckBox.setChecked(mTaskList.get(position).isChecked());

        //Get the date formatted
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy, hh:mma");
            String dateString = dateFormat.format(task.getRemindDate());
            //Set the time
            holder.mTxtTime.setText(dateString);

        }
        catch (Exception e)
        {
            holder.mTxtTime.setText(R.string.noTime);
        }


        //Check if the box has been changed
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = !mTaskList.get(position).isChecked();
                mTaskList.get(position).setChecked(b);
                mTaskDBHelper.update(task.getTaskID(), b);

                holder.mCheckBox.setChecked(b);

                task.executeUpdateFirebase(null);
            }
        });



    }
    /**
     * Returns the total number of items in the list set and held by the adapter.
     *
     * @return The total number of items in list
     */
    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    /**
     *
     * This is when the item is moved it will
     * move the items in the array by removing
     * the object from that position and adding
     * it into the new position
     *
     *
     * @param fromPosition int where the item was
     * @param toPosition int where the item is currently at
     */
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Log.d(TAG, "onItemMove(): From: " + fromPosition + " To: " + toPosition);

        Task fromTask = mTaskList.get(fromPosition);
        mTaskList.remove(fromTask);
        mTaskList.add(toPosition, fromTask);
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     *
     * When is swiped it gets deleted
     *
     * @param position This is the position it was swiped from
     */
    @Override
    public void onItemSwiped(int position) {
        Log.d(TAG, "onItemSwiped(): Item swiped on position " + position);
        Log.d(TAG, "Removing " + mTaskList.get(position));

        Task task = mTaskList.get(position);
        //Delete the task
        removeTask(task);

    }

    /**
     *
     * It will start activity to the task layout and put in
     * the task object as data. which
     * can be retrieved from the other layout
     * by calling {@code getIntent()}.
     *
     * @param position the position of item that was clicked
     */
    @Override
    public void onItemClicked(int position) {
        Log.d(TAG, "onClick(): You have clicked on " + position + " task");


        //Move to the card layout
        Intent intent = new Intent(mContext, CardActivity.class);
        intent.putExtra("task", mTaskList.get(position).getTaskID()); // Add the object
        mContext.startActivity(intent);
    }


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
     * @param task The position from the data will be removed from
     */
    public void removeTask(Task task){


        //Delete from firebase
        task.executeFirebaseDelete(mOwner);

        //Delete from SQL
        task.deleteTask(mContext);

    }

    public void setTaskList(List<Task> taskList) {
        mTaskList = taskList;
    }
}

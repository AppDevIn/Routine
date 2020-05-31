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

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> implements TaskTouchHelperAdapter, TextView.OnEditorActionListener, MyDatabaseListener {

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



        mTaskViewHolder = holder;

        //Change to the add icon for the last position
        if(position == mTaskList.size()) {
            //Move the next view in the switcher which is a button
            mTaskViewHolder.mViewSwitcher.showNext();

            //Set the task to add task
            mTaskViewHolder.mListName.setText(R.string.addMessage);
        }else {
            holder.mListName.setText(mTaskList.get(position).getName());
        }


        //To see the typing and when enter is clicked than add the details
        holder.mEdTask.setOnEditorActionListener(this);


        TaskDBHelper.setMyDatabaseListener(this);

    }

    @Override
    public int getItemCount() {
        return mTaskList.size() + 1;
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

        //When clicked you able to add the task
        if(position == mTaskList.size()){

            Log.d(TAG, "onClick(): User is clicking on the add button in task ");

            //Change to the edit text
            mTaskViewHolder.mViewSwitcherTaskName.showNext();

            //Show the keyboard
            showKeyboard(mTaskViewHolder.mEdTask);
        }

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
    @Override
    public void onDataAdd(Object object) {

        Task task = (Task) object;

        Log.d(TAG, "onDataAdd(): A new data added into SQL updating local list with: " + task );

        //Adding into the local list
        mTaskList.add(task);
    }

    @Override
    public void onDataDelete(String ID) {

        Log.d(TAG, "onDataDelete(): Checking if " + ID + " exists");

        for (int position = 0; position < mTaskList.size(); position++) {

            if(mTaskList.get(position).getTaskID().equals(ID)){

                //Remove the list
                mTaskList.remove(position);
                break;
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

            Log.d(TAG, "onEditorAction(): User eneted \"ENTER\" in keyboard ");

            //Create a task object
            Task task = new Task(textView.getText().toString());

            //Add this object to the list
            addItem(task);

            //Change the design of the task
            mTaskViewHolder.mViewSwitcher.showNext();
            mTaskViewHolder.mViewSwitcherTaskName.showNext();

            mTaskViewHolder.mListName.setText(mTaskViewHolder.mEdTask.getText().toString());


            Log.d(TAG, "onEditorAction: ");

            return true;
        }

        return false;
    }

    /**
     * Adding the task to
     * the list
     * @param task task that will be added to list
     */
    public void addItem(Task task){

        //Add to the SQLite
        task.addTask(mContext,mSection.getID());

        //Add from firebase
        task.executeFirebaseUpload(mOwner);

        //Informing the adapter and view of the new item
        notifyItemInserted(mTaskList.size());
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


        //Informing the adapter and view after removing
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mTaskList.size());

    }


    //Soft Keyboard methods
    private void showKeyboard(View view) {
        Log.i(TAG, "Show soft keyboard");
        InputMethodManager mgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert mgr != null;
        mgr.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

}

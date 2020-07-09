package com.mad.p03.np2020.routine.Card.adapters;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Card.ViewHolder.MyCheckViewHolder;
import com.mad.p03.np2020.routine.DAL.CheckDBHelper;
import com.mad.p03.np2020.routine.DAL.TaskDBHelper;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Task.ViewHolder.TaskViewHolder;
import com.mad.p03.np2020.routine.models.CardViewHolder;
import com.mad.p03.np2020.routine.models.Check;
import com.mad.p03.np2020.routine.models.Task;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CheckAdapter extends RecyclerView.Adapter<MyCheckViewHolder> {

    private final String TAG = "CardAdapter";

    private List<Check> mCheckList;
    private Context mContext;
    private CheckDBHelper mCheckDBHelper;
    private String mTaskID;

    public CheckAdapter(List<Check> checkList, String taskID) {
        mCheckList = checkList;
        mTaskID = taskID;
    }

    @NonNull
    @Override
    public MyCheckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_items, parent, false);

        mContext = parent.getContext();
        mCheckDBHelper = new CheckDBHelper(parent.getContext());
        return new MyCheckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyCheckViewHolder holder, final int position) {

        Log.d(TAG, "onBindViewHolder: Running");

        holder.mListName.setText(mCheckList.get(position).getName());
        holder.mCheckBox.setChecked(mCheckList.get(position).isChecked());

        //Check if the box has been changed
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                mCheckList.get(position).setChecked(b);
                mCheckDBHelper.update(mCheckList.get(position).getID(), b);

            }
        });

        holder.mListName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {


                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    Log.d(TAG, "onEditorAction: " + textView.getText());
                    mCheckList.get(position).setName(holder.mListName.getText().toString());

                    mCheckDBHelper.update(mCheckList.get(position).getID(),mCheckList.get(position).getName());
                    showNewEntry(holder.mListName);
                }

                return false;

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                removeTask(position);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCheckList.size();
    }

    /**
     * Adding the check to
     * the list
     * @param check check that will be added to list
     */
    public void addItem(Check check, Context context){

        //TODO: Add to the SQLite
        check.addCheck(context, mTaskID);

        //TODO: Add from firebase
//        check.executeFirebaseUpload(mOwner);


        Log.d(TAG, "New Task added, " + check.toString());
    }

    /**
     * The place to delete the task in the list
     * @param position The position from the data will be removed from
     */
    public void removeTask(int position){
        Log.d(TAG, "Removing " + mCheckList.get(position));

        Check check = mCheckList.get(position);

        //Delete from firebase
//        check.executeFirebaseDelete(mOwner);

        //Delete from SQL
        check.deleteTask(mContext);

    }

    /**
     * Upon calling this method, the keyboard will retract
     * and the recyclerview will scroll to the last item
     */
    private void showNewEntry(View view){

        //auto hide keyboard after entry
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}

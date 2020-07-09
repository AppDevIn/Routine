package com.mad.p03.np2020.routine.Card.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Card.adapters.CheckAdapter;
import com.mad.p03.np2020.routine.Card.models.MyCardTouchHelper;
import com.mad.p03.np2020.routine.DAL.CheckDBHelper;
import com.mad.p03.np2020.routine.DAL.TaskDBHelper;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Task.adapter.TaskAdapter;
import com.mad.p03.np2020.routine.Task.model.MyTaskTouchHelper;
import com.mad.p03.np2020.routine.helpers.CheckDataListener;
import com.mad.p03.np2020.routine.models.Check;
import com.mad.p03.np2020.routine.models.Section;
import com.mad.p03.np2020.routine.models.Task;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CheckListFragment extends Fragment implements CheckDataListener, TextView.OnEditorActionListener {

    String mTaskID;
    List<Check> mCheckLst;
    CheckAdapter checkAdapter;
    RecyclerView mRecyclerView;
    EditText mEdCheck;

    private final String TAG = "CheckFragment";

    public CheckListFragment(String taskID) {
        mTaskID = taskID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checklist, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initRecyclerView();

        mEdCheck = getView().findViewById(R.id.edTask);
        mEdCheck.setOnEditorActionListener(this);

        CheckDBHelper.setMyDatabaseListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, "onStop()");

        //Update the position of the list
        CheckDBHelper checkDBHelper = new CheckDBHelper(getContext());
        for (int i = 0; i < mCheckLst.size(); i++) {
            mCheckLst.get(i).setPosition(i);
            Check check = mCheckLst.get(i);
            Log.d(TAG, "onStop: Updating " + check.getName());
            checkDBHelper.update(check.getID(),check.getPosition());
//            check.executeUpdateFirebase(this);

        }
    }

    @Override
    public void onDataAdd(Check check) {

        Log.d(TAG, "onDataAdd(): A new data added into SQL updating local list with: " + check );

        mCheckLst.add(check);
        checkAdapter.notifyItemInserted(mCheckLst.size());
    }

    @Override
    public void onDataDelete(String ID) {

        for (int position = 0; position < mCheckLst.size(); position++) {

            if(mCheckLst.get(position).getID().equals(ID)){

                //Remove the list
                mCheckLst.remove(position);

                //Informing the adapter and view after removing
                checkAdapter.notifyItemRemoved(position);
                checkAdapter.notifyItemRangeChanged(position, mCheckLst.size());
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
            Check check = new Check(textView.getText().toString());

            //Add this object to the list
            checkAdapter.addItem(check, getContext());


            //Hide and scroll the last task
            showNewEntry();


            return true;
        }
        return false;
    }

    private void initRecyclerView(){


        CheckDBHelper checkDBHelper = new CheckDBHelper(getContext());

        mCheckLst = checkDBHelper.getSection(mTaskID);

        mRecyclerView = getView().findViewById(R.id.checkRecycler);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        checkAdapter = new CheckAdapter(mCheckLst, mTaskID);
        mRecyclerView.setAdapter(checkAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //Setting up touchhelper
        ItemTouchHelper.Callback callback = new MyCardTouchHelper(checkAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        checkAdapter.setMyTaskTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    /**
     * Upon calling this method, the keyboard will retract
     * and the recyclerview will scroll to the last item
     */
    private void showNewEntry(){
        //scroll to the last item of the recyclerview
        mRecyclerView.smoothScrollToPosition(mCheckLst.size());

        //auto hide keyboard after entry
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mRecyclerView.getWindowToken(), 0);

        //Clear the text from the view
        mEdCheck.setText("");
    }
}

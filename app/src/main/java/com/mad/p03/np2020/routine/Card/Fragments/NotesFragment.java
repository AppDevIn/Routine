package com.mad.p03.np2020.routine.Card.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mad.p03.np2020.routine.DAL.TaskDBHelper;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.models.Task;
/*
 *
 * Notes fragment to take notes for each task
 *
 * @author Jeyavishnu & Pritheev
 * @since 10-07-2020
 *
 */
public class NotesFragment extends Fragment
{

    Task mTask;
    EditText notes;

    public NotesFragment(Task task) {
        mTask = task;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_notes_layout, container, false);

        notes = view.findViewById(R.id.notes);

        notes.setText(mTask.getNotes());


        return view;
    }

    @Override
    public void onStop() {
        super.onStop();

        //Save the notes

        Log.d("NotesFragment", "onStop: " + notes.getText());
        mTask.setNotes(notes.getText().toString());

        TaskDBHelper taskDBHelper = new TaskDBHelper(getView().getContext());
        taskDBHelper.update(mTask.getTaskID(),null, mTask.getNotes());

        mTask.executeUpdateFirebase(null);

    }
}

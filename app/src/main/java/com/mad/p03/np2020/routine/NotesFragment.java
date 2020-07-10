package com.mad.p03.np2020.routine;

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
import com.mad.p03.np2020.routine.models.Task;

/*
 *
 * Notes fragment to take notes for each task
 *
 * @author Jeyavishnu
 * @since 10-07-2020
 *
 */

public class NotesFragment extends Fragment
{

    Task mTask;


    public NotesFragment(Task task) {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_notes_layout, container, false);

        EditText notes = view.findViewById(R.id.notes);

        notes.setText(mTask.getNotes());

        notes.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

//                Log.d("NotesFragment", "onEditorAction: " + textView.getText());
//                mTask.setNotes(textView.getText().toString());
//
//                TaskDBHelper taskDBHelper = new TaskDBHelper(view.getContext());
//                taskDBHelper.update(mTask.getTaskID(),null, mTask.getNotes());
//
//                mTask.executeUpdateFirebase(null);

                return false;
            }
        });

        return view;
    }
}

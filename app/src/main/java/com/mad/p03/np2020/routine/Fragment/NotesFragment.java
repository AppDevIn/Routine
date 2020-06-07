package com.mad.p03.np2020.routine.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.mad.p03.np2020.routine.CardActivity;
import com.mad.p03.np2020.routine.Class.Task;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.database.TaskDBHelper;

/*
 *
 * CardActivity class used to manage card activities
 *
 * @author Pritheev
 * @since 02-06-2020
 *
 */
public class NotesFragment extends Fragment
{

    Task mTask;
    //Empty constructor
    public NotesFragment(Task task) {
        mTask = task;
    }

    //Initializing variables for NotesFragment when new instance created
    public static NotesFragment newInstance(Task task)
    {
        //Initializing a new instance of NotesFragment
        NotesFragment fragment = new NotesFragment(task);

        //Initializing Bundle
        Bundle args = new Bundle();

        //Setting bundle for fragment
        fragment.setArguments(args);

        //Returning fragment
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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

                Log.d("NotesFragment", "onEditorAction: " + textView.getText());
                mTask.setNotes(textView.getText().toString());

                TaskDBHelper taskDBHelper = new TaskDBHelper(view.getContext());
                taskDBHelper.update(mTask.getTaskID(),null, mTask.getNotes());

                mTask.executeUpdateFirebase(null);

                return false;
            }
        });

        // Inflate the layout for this fragment
        return view;
    }
}

package com.mad.p03.np2020.routine.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.p03.np2020.routine.R;

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

    //Empty constructor
    public NotesFragment() {
        // Required empty public constructor
    }

    //Initializing variables for NotesFragment when new instance created
    public static NotesFragment newInstance()
    {
        //Initializing a new instance of NotesFragment
        NotesFragment fragment = new NotesFragment();

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes_layout, container, false);
    }
}

package com.mad.p03.np2020.routine;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes_layout, container, false);
    }
}

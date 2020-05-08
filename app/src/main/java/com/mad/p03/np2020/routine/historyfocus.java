package com.mad.p03.np2020.routine;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.BitSet;


public class historyfocus extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TEXT = "What you want?";

    // TODO: Rename and change types of parameters
    private String mText;

    private ImageButton buttonFragment;
    private TextView textFragment;
    private OnFragmentInteractionListener mListener;
    private final String TAG = "Focus";

    public historyfocus() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static historyfocus newInstance(String Text) {
        historyfocus fragment = new historyfocus();
        Bundle args = new Bundle();
        args.putString(TEXT, Text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mText = getArguments().getString(TEXT);
            Log.v(TAG, "Created fragment");
        }
    }

    public void sendBack(){
        if(mListener != null){
            mListener.onFragmentInteraction();
            Log.v(TAG, "Sending back");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_historyfocus, container, false);
        buttonFragment = view.findViewById(R.id.closeFragment);
        textFragment = view.findViewById(R.id.text_cool);
        textFragment.setText(mText);

        buttonFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBack();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener{
        void onFragmentInteraction();
    }

}

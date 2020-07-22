package com.mad.p03.np2020.routine.Task.Fragment;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.mad.p03.np2020.routine.DAL.SectionDBHelper;
import com.mad.p03.np2020.routine.Fragment.HistoryFragment;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Task.ViewHolder.TeamViewHolder;
import com.mad.p03.np2020.routine.Task.adapter.TaskAdapter;
import com.mad.p03.np2020.routine.Task.adapter.TeamAdapter;
import com.mad.p03.np2020.routine.Task.model.MyTaskTouchHelper;
import com.mad.p03.np2020.routine.models.Section;
import com.mad.p03.np2020.routine.models.Task;
import com.mad.p03.np2020.routine.models.Team;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TaskSettings extends Fragment implements TextView.OnEditorActionListener {



    TeamAdapter mTeamAdapter;
    RecyclerView mRecyclerView;
    List<String> emailList;
    String mSectionID;
    Team team;
    Section mSection;

    public TaskSettings(String id) {
        mSectionID = id;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_task_settings, container, false);

        CardView cardView = view.findViewById(R.id.mainCard);

        //Get the section object
        mSection = new SectionDBHelper(getContext()).getSection(mSectionID);

        //Set the background of the cardview
        float radius[] = {50f,50f,50f,50f,0f,0f,0f,0f};

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(getResources().getColor(R.color.white));
        shape.setCornerRadii(radius);

        cardView.setBackground(shape);


        //Set click listener for the image
        ImageView imageView = view.findViewById(R.id.downArrow);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("Settings");

                if(fragment != null)
                    getActivity().getSupportFragmentManager().
                            beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_in_bottom, R.anim.slide_in_bottom, R.anim.slide_in_bottom)
                            .replace(R.id.fragmentContainer, new Fragment())
                            .commit();
            }
        });

        EditText editText = view.findViewById(R.id.addUser);
        editText.setOnEditorActionListener(this);

        if(!mSection.isAdmin())
            editText.setVisibility(View.GONE);

        return view;



    }


    @Override
    public void onStart() {
        super.onStart();

        Team team = new Team();
        team.setSectionID(mSectionID);




        initRecyclerView(getView(), team);


    }

    /**
     *
     * The action is being performed on the keyboard
     * when the the Enter key is pressed add the task into
     * the adapter and hide the keyboard
     *
     * @param textView The view that was clicked.
     * @param actionId  Identifier of the action. This will be either the identifier you supplied, or
     *                  EditorInfo#IME_NULL if being called due to the enter key being pressed.
     * @param event  If triggered by an enter key, this is the event; otherwise, this is null.
     * @return Return true if you have consumed the action, else false.
     */
    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {

        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {




            //Add this object to the list
            mTeamAdapter.addEmail(textView.getText().toString());



            //Hide and scroll the last task
            showNewEntry();


            return true;
        }
        return false;
    }



    private void initRecyclerView(View view, Team team) {
        mRecyclerView = view.findViewById(R.id.recyclerViewEmail);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mTeamAdapter = new TeamAdapter(team,getContext(), mSection);
        mRecyclerView.setAdapter(mTeamAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());



    }


    /**
     * Upon calling this method, the keyboard will retract
     * and the recyclerview will scroll to the last item
     */
    private void showNewEntry(){

        //auto hide keyboard after entry
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mRecyclerView.getWindowToken(), 0);

    }




}

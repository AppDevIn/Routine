package com.mad.p03.np2020.routine.Task.Fragment;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.mad.p03.np2020.routine.Fragment.HistoryFragment;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Task.ViewHolder.TeamViewHolder;
import com.mad.p03.np2020.routine.Task.adapter.TaskAdapter;
import com.mad.p03.np2020.routine.Task.adapter.TeamAdapter;
import com.mad.p03.np2020.routine.Task.model.MyTaskTouchHelper;

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

public class TaskSettings extends Fragment {





    public TaskSettings() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_task_settings, container, false);

        CardView cardView = view.findViewById(R.id.mainCard);

        //Set the background of the cardview
        float radius[] = {50f,50f,50f,50f,0f,0f,0f,0f};

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(getResources().getColor(R.color.white));
        shape.setCornerRadii(radius);

        cardView.setBackground(shape);


        ImageView imageView = view.findViewById(R.id.downArrow);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag("Settings");

                if(fragment != null)
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.exit_to_bottom, R.anim.slide_in_bottom).remove(fragment).commit();
            }
        });

        return view;



    }


    @Override
    public void onStart() {
        super.onStart();

        //Fake data
        List<String> emailList = new ArrayList<>();

        emailList.add("hhdh@gmail.ocm");
        emailList.add("hhdh@gmail.ocm");
        emailList.add("hhdh@gmail.ocm");
        emailList.add("hhdh@gmail.ocm");
        emailList.add("hhdh@gmail.ocm");
        emailList.add("hhdh@gmail.ocm");
        emailList.add("hhdh@gmail.ocm");

        initRecyclerView(getView(), emailList);

    }

    onBac

    private void initRecyclerView(View view, List<String> emailList) {
        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerViewEmail);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        TeamAdapter mTeamAdapter = new TeamAdapter(emailList);
        mRecyclerView.setAdapter(mTeamAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }



}

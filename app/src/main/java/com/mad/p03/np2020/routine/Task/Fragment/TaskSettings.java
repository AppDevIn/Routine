package com.mad.p03.np2020.routine.Task.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Task.ViewHolder.TeamViewHolder;
import com.mad.p03.np2020.routine.Task.adapter.TaskAdapter;
import com.mad.p03.np2020.routine.Task.adapter.TeamAdapter;
import com.mad.p03.np2020.routine.Task.model.MyTaskTouchHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

        List<String> emailList = new ArrayList<>();

        emailList.add("hhdh@gmail.ocm");
        emailList.add("hhdh@gmail.ocm");
        emailList.add("hhdh@gmail.ocm");
        emailList.add("hhdh@gmail.ocm");
        emailList.add("hhdh@gmail.ocm");
        emailList.add("hhdh@gmail.ocm");
        emailList.add("hhdh@gmail.ocm");

        RecyclerView mRecyclerView = view.findViewById(R.id.recyclerViewEmail);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        TeamAdapter mTeamAdapter = new TeamAdapter(emailList);
        mRecyclerView.setAdapter(mTeamAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void initRecyclerView(){




    }

}

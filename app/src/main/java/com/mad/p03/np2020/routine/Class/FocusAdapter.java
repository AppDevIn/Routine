package com.mad.p03.np2020.routine.Class;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.gson.Gson;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.database.FocusDatabase;

import java.util.List;

public class FocusAdapter extends RecyclerView.Adapter<FocusViewHolder> {

    private List<FocusHolder> focusList; //List of focus
    private Context context; //Current context
    private FocusDatabase focusDatabase;
    private FocusHolder focusViewHolder;
    private User user;

    public FocusAdapter(User user, Context context, FocusDatabase focusDatabase) {
        this.context = context;
        this.focusDatabase = focusDatabase;
        this.user = user;
        this.focusList = user.getmFocusList();
    }

    @NonNull
    @Override
    public FocusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View historyView = LayoutInflater.from(context).inflate(R.layout.layout_itemfocus, parent, false);
        return new FocusViewHolder(historyView, context, this, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull FocusViewHolder holder, int position) {
        holder.duration.setText(focusList.get(position).getmDuration());
        holder.Task.setText(focusList.get(position).getmTask());
        holder.date.setText(focusList.get(position).getmDateTime());
        if (focusList.get(position).getmCompletion().equals("True")) {
            holder.iconComplete.setImageResource(R.drawable.ic_tick);
        } else {
            holder.iconComplete.setImageResource(R.drawable.ic_cross);
        }
        this.focusViewHolder = focusList.get(position);
    }

    //Remove item
    public void remove(int position, FocusHolder focusViewHolder) {
        focusList.remove(position);
        focusDatabase.removeOneData(focusViewHolder);
        deleteDataFirebase(focusViewHolder);
        this.notifyItemRemoved(position);
    }

    //get item count
    @Override
    public int getItemCount() {
        return focusList.size();
    }

    public FocusHolder getItems() {
        return focusViewHolder;
    }


    public void deleteDataFirebase(FocusHolder focusHolder) {
        Log.i("Firebase", "Deleting Database entry");

        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data firebaseUserData = new Data.Builder()
                .putString("ID", user.getUID())
                .putString("focusData", serializeToJson(focusHolder))
                .putBoolean("deletion", true)
                .build();

        OneTimeWorkRequest mywork =
                new OneTimeWorkRequest.Builder(FocusWorker.class)
                        .setConstraints(myConstraints)
                        .setInputData(firebaseUserData)
                        .build();

        WorkManager.getInstance(context.getApplicationContext()).enqueue(mywork);
    }

    // Serialize a single object.
    public String serializeToJson(FocusHolder myClass) {
        Gson gson = new Gson();
        return gson.toJson(myClass);
    }

}

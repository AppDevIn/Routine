package com.mad.p03.np2020.routine.Adapter;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.mad.p03.np2020.routine.Class.Focus;
import com.mad.p03.np2020.routine.ViewHolder.FocusViewHolder;
import com.mad.p03.np2020.routine.background.FocusWorker;
import com.mad.p03.np2020.routine.Class.User;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.database.FocusDatabase;

import java.util.List;

public class FocusAdapter extends RecyclerView.Adapter<FocusViewHolder> {

    private List<Focus> focusList; //List of focus
    private Context context; //Current context
    private FocusDatabase focusDatabase;
    private Focus focusViewHolder;
    private User user;
    private String TAG = "FocusAdapter";

    public FocusAdapter(User user, Context context, FocusDatabase focusDatabase) {
        this.context = context;
        this.focusDatabase = focusDatabase;
        this.user = user;
        this.focusList = user.getmFocusList();
        user.readFocusFirebase(context);
        eventListener();
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
    }

    //Remove item
    public void remove(int position, Focus focusViewHolder) {
        focusList.remove(position);
        focusDatabase.removeOneData(focusViewHolder);
        deleteDataFirebase(focusViewHolder);
        this.notifyItemRemoved(position);
    }

    public void notifiyItemChange(){
        focusList = user.getmFocusList();
        this.notifyDataSetChanged();
        Log.v(TAG, "Data is changed from other server");
    }

    //Trace Firebase Listener
    private void eventListener(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUID());
        myRef.child("FocusData").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifiyItemChange();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    //get item count
    @Override
    public int getItemCount() {
        return focusList.size();
    }

    //get item from position
    public Focus getItems(int position) {
        return focusList.get(position);
    }

    //Delete Date from firebase
    public void deleteDataFirebase(Focus focus) {
        Log.i("Firebase", "Deleting Database entry");

        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data firebaseUserData = new Data.Builder()
                .putString("ID", user.getUID())
                .putString("focusData", serializeToJson(focus))
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
    public String serializeToJson(Focus myClass) {
        Gson gson = new Gson();
        return gson.toJson(myClass);
    }

}

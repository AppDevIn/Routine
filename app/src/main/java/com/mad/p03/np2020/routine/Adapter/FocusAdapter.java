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
    private User user;
    private String TAG = "FocusAdapter";

    /**
     * Focus Adapter
     *
     * @param user This parameter is used to set the user of the section
     * @param context This parameter is used to set the context of the section
     * @param focusDatabase This parameter is used to set the focus database of the section
     */
    public FocusAdapter(User user, Context context, FocusDatabase focusDatabase) {
        this.context = context;
        this.focusDatabase = focusDatabase;
        this.user = user;
        this.focusList = user.getmFocusList();
        user.readFocusFirebase(context);
        eventListener();
    }

    /**
     * Focus View Holder
     *
     * @param parent This parameter is used to set the ViewGroup of this section
     * @param viewType This parameter is used to set the viewType of this section
     */
    @NonNull
    @Override
    public FocusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View historyView = LayoutInflater.from(context).inflate(R.layout.layout_itemfocus, parent, false);
        return new FocusViewHolder(historyView, this, parent);
    }

    /**
     * Notify Item changed if user remove
     *
     * @param positon This parameter is used to set the position of the section
     * @param focusViewHolder This parameter is used to set the FocusViewHolder of the section
     */
    @Override
    public void onBindViewHolder(@NonNull FocusViewHolder holder, int position) {
        holder.duration.setText(focusList.get(position).getmDuration());
        holder.Task.setText(focusList.get(position).getmTask());
        holder.date.setText(focusList.get(position).getmDateTime());
        boolean completed = focusList.get(position).getmCompletion().equals("True");
        Log.v(TAG, focusList.get(position).getmCompletion());
        if (completed) {
            holder.iconComplete.setImageResource(R.drawable.ic_tick);
        } else {
            holder.iconComplete.setImageResource(R.drawable.ic_cross);
        }
    }

    /**
     * Notify Item changed if user remove
     *
     * @param positon This parameter is used to set the position of the section
     * @param focusViewHolder This parameter is used to set the FocusViewHolder of the section
     */
    public void remove(int position, Focus focusViewHolder) {
        focusList.remove(position);
        focusDatabase.removeOneData(focusViewHolder);
        deleteDataFirebase(focusViewHolder);
        this.notifyItemRemoved(position);
    }

    /**
     * Notify Item changed if user delete or add data
     *
     */
    public void notifiyItemChange(){
        focusList = user.getmFocusList();
        this.notifyDataSetChanged();
        Log.v(TAG, "Data is changed from other server");
    }

    /**
     * Listen to firebase data change to update views on the recyclerView
     *
     */
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

    /**
     * Get Item Size
     *
     * @return int returns the size of the list
     */
    @Override
    public int getItemCount() {
        return focusList.size();
    }

    /**
     * Get Item Position
     *
     * @Param int To used to indicate the positon of the item inside the list based on its position
     * @return  Focus To return the Focus Data from the list
     */
    public Focus getItems(int position) {
        return focusList.get(position);
    }

    /**
     * Deletes data from Firebase
     * @Param focus Passed in the object that is needed to be deleted from firebase
     *
     */
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

    /**
     * Serialize a single object.
     * @return String this returns the custom object class as a string
     * @Param myClass passing in the custom object to be converted by Gson to string
     */
    public String serializeToJson(Focus myClass) {
        Gson gson = new Gson();
        return gson.toJson(myClass);
    }

}

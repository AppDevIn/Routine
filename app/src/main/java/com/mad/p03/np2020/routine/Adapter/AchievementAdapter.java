package com.mad.p03.np2020.routine.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.p03.np2020.routine.DAL.FocusDBHelper;
import com.mad.p03.np2020.routine.Fragment.AchievementFragment;
import com.mad.p03.np2020.routine.Fragment.HistoryFragment;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.ViewHolder.AchievementViewHolder;
import com.mad.p03.np2020.routine.ViewHolder.FocusViewHolder;
import com.mad.p03.np2020.routine.models.Focus;
import com.mad.p03.np2020.routine.models.User;

import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementViewHolder> {

    private List<Focus> focusList; //List of focus
    private Context context; //Current context
    private FocusDBHelper focusDBHelper;
    private User user;
    private String TAG = "FocusAdapter";
    private AchievementFragment achievementFragment;

    public AchievementAdapter(User user, Context context, FocusDBHelper focusDBHelper, AchievementFragment achievementFragment) {
        this.context = context;
        this.focusDBHelper = focusDBHelper;
        this.user = user;
        this.achievementFragment = achievementFragment;

        user.readFocusFirebase(context);
        eventListener();
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View historyView = LayoutInflater.from(context).inflate(R.layout.layout_item_achievements, parent, false);
        return new AchievementViewHolder(historyView, this, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * Notify Item changed if user delete or add data
     */
    public void notifiyItemChange() {
        focusList = user.getmFocusList();

        this.notifyDataSetChanged();
        Log.v(TAG, "Data is changed from other server");
    }

    /**
     * Listen to firebase data change to update views on the recyclerView
     */
    private void eventListener() {
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
}

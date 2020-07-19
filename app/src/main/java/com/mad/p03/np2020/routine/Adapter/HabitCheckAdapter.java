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
import com.mad.p03.np2020.routine.helpers.HabitCheckItemClickListener;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.ViewHolder.HabitCheckHolder;
import com.mad.p03.np2020.routine.models.Habit;
import com.mad.p03.np2020.routine.models.User;

import static com.mad.p03.np2020.routine.HabitActivity.remind_text;

public class HabitCheckAdapter extends RecyclerView.Adapter<HabitCheckHolder> {

    final static String TAG = "HabitCheckAdapter";
    private Habit.HabitList habitList;
    Context c;
    private HabitCheckItemClickListener mListener;
    private static View view;
    private User user;

    public void setOnItemClickListener(HabitCheckItemClickListener listener){
        this.mListener = listener;
    }

    public HabitCheckAdapter(Context c, Habit.HabitList habitList, User user) {
        this.c = c;
        this.habitList = habitList;
        this.user = user;

        user.readHabit_Firebase(c);
        eventListener();
    }


    @NonNull
    @Override
    public HabitCheckHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_check_row,null);
        return new HabitCheckHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitCheckHolder holder, int position) {
        final Habit habit = habitList.getItemAt(position);

        if (habit.getTitle().toLowerCase().equals("dummy")){
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            return;
        }else{
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        }

        holder.title.setText(capitalise(habit.getTitle().trim()));
        holder.habit_count.setText(String.valueOf(habit.getCount()));
        holder.habit_occurrence.setText(String.valueOf(habit.getOccurrence()));

        int progress = habit.calculateProgress();
        holder.habit_progressBar.setProgress(progress);
        if (progress == 100){
            holder.habit_progressBar.setVisibility(View.INVISIBLE);
            holder.habit_finished.setVisibility(View.VISIBLE);
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }else{
            holder.habit_progressBar.setVisibility(View.VISIBLE);
            holder.habit_finished.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: "+habitList.size());
        return habitList.size();
    }

    /**
     *
     * This method is used to format text by capitalising the first text of each split text
     *
     * @param text This parameter is used to get the text
     *
     * @return String This returns the formatted text
     * */
    public String capitalise(String text){
        String txt = "";
        String[] splited = text.split("\\s+");
        for (String s: splited){
            txt += s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase() + " ";
        }
        return txt;

//        return text.substring(0,1).toUpperCase() + text.substring(1).toLowerCase();
    }

    /**
     * Listen to firebase data change to update views on the recyclerView
     */
    private void eventListener() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUID());
        myRef.child("habit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifyItemChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    /**
     * Notify Item changed if user delete or add data
     */
    public void notifyItemChanged() {
        habitList = initDummyList(user.getHabitList());
        this.notifyDataSetChanged();

        int n = checkIncompleteHabits(habitList);

        if (n == 0){
            remind_text.setText("You have completed all habits today!");
        }else if (n == 1){
            remind_text.setText("You still have 1 habit to do today");
        }else{
            remind_text.setText(String.format("You still have %d habits to do today",n));
        }

        Log.v(TAG, "Data is changed from other server");
    }

    private Habit.HabitList initDummyList (Habit.HabitList habitList){

        if (habitList.size() == 0) {return habitList;}
        int size = habitList.size();

        int dummy_size = 4-(size % 4);
        if (dummy_size == 4) {return habitList;}

        for (int i = 0; i<dummy_size; i++){
            habitList.addItem(new Habit("dummy",0,0,"cyangreen"));
        }

        return habitList;
    }

    public int checkIncompleteHabits(Habit.HabitList habitList){
        int n = 0;
        for (int i = 0; i < habitList.size(); i++){
            Habit habit = habitList.getItemAt(i);
            if (!habit.getTitle().toLowerCase().equals("dummy") && habit.getOccurrence() > habit.getCount() ){
                n++;
            }
        }
        return n;
    }
}

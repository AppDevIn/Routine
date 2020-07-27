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
import com.mad.p03.np2020.routine.helpers.HabitItemClickListener;
import com.mad.p03.np2020.routine.models.Habit;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.ViewHolder.HabitHolder;
import com.mad.p03.np2020.routine.background.HabitWorker;
import com.mad.p03.np2020.routine.DAL.HabitDBHelper;
import com.mad.p03.np2020.routine.models.User;

import static com.mad.p03.np2020.routine.HabitActivity.remind_text;

/**
 *
 * This will be the controller glue between the viewHolder and the model.
 * This will inflate the the items for the habits to which will give us
 * the view from will be passed to the view holder HabitViewHolder
 *
 * @author Hou Man
 * @since 02-06-2020
 */

public class HabitAdapter extends RecyclerView.Adapter<HabitHolder> {


    final static String TAG = "HabitAdapter";
    private Context c;
    private HabitItemClickListener mListener;
    private static View view;
    private HabitDBHelper dbHandler;
    private User user;

    /**Used as the adapter habitList*/
    public Habit.HabitList _habitList;
    private HabitCheckAdapter habitCheckAdapter;

    /**This method is a constructor for habitAdapter*/

    public HabitAdapter(Context c, Habit.HabitList habitList, User user,HabitCheckAdapter habitCheckAdapter) {
        this.c = c;
        this._habitList = habitList;
        dbHandler = new HabitDBHelper(c);
        this.user = user;
        this.habitCheckAdapter = habitCheckAdapter;

        user.readHabit_Firebase(c, true);
        user.readHabitRepetition_Firebase(c);
        habitEventListener();
        habitRepetitionEventListener();
    }

    /**
     *
     * This method is used to bind the listener
     *
     * @param listener This parameter reference the local Listener
     * */
    public void setOnItemClickListener(HabitItemClickListener listener){
        this.mListener = listener;
    }

    /**
     *
     * This method is used to
     *  create new views (invoked by the layout manager)
     *
     * @param parent This parameter is used to get the viewGroup.
     *
     * @param viewType This parameter is used to get the viewType.
     *
     * @return HabitHolder This returns the habitHolder with view created
     * */
    @NonNull
    @Override
    public HabitHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_grid_view_items, parent, false);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) (parent.getWidth() * 0.464);
        Log.d(TAG, "onCreateViewHolder: width" + layoutParams.width);
        view.setLayoutParams(layoutParams);

        return new HabitHolder(view, mListener);
    }


    /**
     *
     * This method is used to
     *  replace the contents of a view (invoked by the layout manager)
     *
     * @param holder This parameter is used to get the holder
     *
     * @param position This parameter is used to get the position
     *
     * */

    @Override
    public void onBindViewHolder(@NonNull HabitHolder holder, final int position) {
        // retrieve the habit object
        final Habit habit = _habitList.getItemAt(position);

        if (habit.getTitle().toLowerCase().equals("dummy")){
            holder.itemView.setVisibility(View.INVISIBLE);
            return;
        }else{
            holder.itemView.setVisibility(View.VISIBLE);
        }

        // set the background color of the holder based on its holder color value
        switch (habit.getHolder_color()) {

            case ("cyangreen"):
                holder.habit_card.setBackgroundResource(R.drawable.habit_holder_cyangreen);
                break;

            case ("lightcoral"):
                holder.habit_card.setBackgroundResource(R.drawable.habit_holder_lightcoral);
                break;

            case ("fadepurple"):
                holder.habit_card.setBackgroundResource(R.drawable.habit_holder_fadepurple);
                break;

            case ("slightdesblue"):
                holder.habit_card.setBackgroundResource(R.drawable.habit_holder_slightdesblue);
                break;
        }

        // set text on TextView based on the object
        holder.mTitle.setText(capitalise(habit.getTitle()));
        holder.mCount.setText(String.valueOf(habit.getCount()));
        holder.mOccurrence.setText(String.valueOf(habit.getOccurrence()));

        int progress = habit.calculateProgress();
        holder.habit_progressBar.setProgress(progress);
        holder.habit_progress.setText(String.valueOf(progress));
        if (progress == 100){
            holder.habit_progressBar.setVisibility(View.INVISIBLE);
            holder.habit_finished.setVisibility(View.VISIBLE);
        }else{
            holder.habit_progressBar.setVisibility(View.VISIBLE);
            holder.habit_finished.setVisibility(View.INVISIBLE);
        }

    }

    /**@return int This return the size of the data set, habitList*/
    @Override
    public int getItemCount() {
        return _habitList.size();
    }

    /**
     *
     * This method is used to send the work request
     *  to the habitWorker(WorkManager) to do the writing firebase action
     *  when the network is connected.
     *  (This will be invoked when the count increases on the page of HabitTracker)
     *
     * @param habit This parameter is used to get the habit object
     *
     * @param UID This parameter is used to get the userID
     *
     * @param isDeletion This parameter is used to indicate whether it is deletion of habit to firebase
     *
     * */
    public void writeHabit_Firebase(Habit habit, String UID, boolean isDeletion){
        Log.i(TAG, "Uploading to Firebase");

        // set constraint that the network must be connected
        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // put data in a data builder
        Data firebaseUserData = new Data.Builder()
                .putString("ID", UID)
                .putString("habitData", habit_serializeToJson(habit))
                .putBoolean("deletion", isDeletion)
                .build();

        // wrap the work request
        OneTimeWorkRequest mywork =
                new OneTimeWorkRequest.Builder(HabitWorker.class)
                        .setConstraints(myConstraints)
                        .setInputData(firebaseUserData)
                        .build();

        // send the work request to the work manager
        WorkManager.getInstance(c).enqueue(mywork);
    }


    /**
     *
     * This method is used to serialize a single object. (into Json String)
     *
     * @param habit This parameter is used to get the habit object
     *
     * @return String This returns the serialized object.
     *
     * */
    public String habit_serializeToJson(Habit habit) {
        Gson gson = new Gson();
        Log.i(TAG,"Object serialize");
        return gson.toJson(habit);
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
    private void habitEventListener() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUID());
        myRef.child("habit").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifyHabitChanged();
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
    public void notifyHabitChanged() {
        Habit.HabitList habitList = initDummyList(user.getHabitList());
        _habitList = habitList;
        habitCheckAdapter.habitList = habitList;
        this.notifyDataSetChanged();
        habitCheckAdapter.notifyDataSetChanged();

        int n = checkIncompleteHabits(_habitList);

        if (n == 0){
            remind_text.setText("You have completed all habits today!");
        }else if (n == 1){
            remind_text.setText("You still have 1 habit to do");
        }else{
            remind_text.setText(String.format("You still have %d habits to do",n));
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

    /**
     * Listen to firebase data change to update views on the recyclerView
     */
    private void habitRepetitionEventListener() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUID());
        myRef.child("habitRepetition").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifyHabitRepetitionChanged();
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
    public void notifyHabitRepetitionChanged() {
        Habit.HabitList habitList = initDummyList(dbHandler.getAllHabits());
        _habitList = habitList;
        habitCheckAdapter.habitList = habitList;
        this.notifyDataSetChanged();
        habitCheckAdapter.notifyDataSetChanged();

        int n = checkIncompleteHabits(_habitList);

        if (n == 0){
            remind_text.setText("You have completed all habits today!");
        }else if (n == 1){
            remind_text.setText("You still have 1 habit to do");
        }else{
            remind_text.setText(String.format("You still have %d habits to do",n));
        }
        Log.v(TAG, "Data is changed from other server");
    }
}


package com.mad.p03.np2020.routine;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.Class.Habit;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.MyHolder> {


    final static String TAG = "HabitAdapter";
    Context c;
    Habit.HabitList _habitList;
    private OnItemClickListener mListener;
    static View view;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public HabitAdapter(Context c, Habit.HabitList habitList) {
        this.c = c;
        this._habitList = habitList;
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        public TextView mTitle,mCount,mCount2,mOccurrence, mPeriod;
        public ImageButton addBtn;
//        public LinearLayout habit_row;

        public MyHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            this.mTitle = itemView.findViewById(R.id.habitTitle);
            this.mCount = itemView.findViewById(R.id.habitCount);
            this.mCount2 = itemView.findViewById(R.id.habitCount2);
            this.mOccurrence = itemView.findViewById(R.id.habitOccurence);
            this.addBtn = itemView.findViewById(R.id.addCnt);
            this.mPeriod = itemView.findViewById(R.id.habit_period);
//            this.habit_row = itemView.findViewById(R.id.habit_row);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

    }

    @NonNull
    @Override
    public HabitAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_row,null);

        return new MyHolder(view, mListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {
        final Habit habit = _habitList.getItemAt(position);

//        holder.habit_row.setBackgroundColor(Color.GRAY);
        holder.mTitle.setText(habit.getTitle());
        holder.mCount.setText(String.valueOf(habit.getCount()));
        holder.mCount2.setText(String.valueOf(habit.getCount()));
        holder.mOccurrence.setText(String.valueOf(habit.getOccurrence()));
        holder.addBtn.setBackgroundColor(Color.TRANSPARENT);
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                habit.addCount();
                notifyDataSetChanged();
            }
        });

        switch (habit.getPeriod()){
            case 7:
                holder.mPeriod.setText("THIS WEEK:");
                break;
            case 30:
                holder.mPeriod.setText("THIS MONTH:");
                break;
            case 365:
                holder.mPeriod.setText("THIS YEAR:");
                break;
        }

        if (habit.getCount() >= habit.getOccurrence()){
            holder.addBtn.setImageResource(R.drawable.habit_tick);
        }else{
            holder.addBtn.setImageResource(R.drawable.habit_add);
        }

    }

    @Override
    public int getItemCount() {
        return _habitList.size();
    }

}


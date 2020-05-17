package com.mad.p03.np2020.routine.Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.R;

import java.util.List;

public class FocusAdapter extends RecyclerView.Adapter<FocusAdapter.FocusViewHolder> {

    private List<Focus> focusList;
    private Context context;

    public FocusAdapter(List<Focus> focusList, Context context){
        this.context = context;
        this.focusList = focusList;
    }

    @NonNull
    @Override
    public FocusAdapter.FocusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View historyView = LayoutInflater.from(context).inflate(R.layout.layout_itemfocus, parent, false);
        return new FocusAdapter.FocusViewHolder(historyView);
    }

    public class FocusViewHolder extends RecyclerView.ViewHolder{
        ImageView iconComplete;
        TextView Task, date, duration;

        public FocusViewHolder(@NonNull View itemView) {
            super(itemView);
            iconComplete = itemView.findViewById(R.id.icon);
            Task = itemView.findViewById(R.id.taskView);
            date = itemView.findViewById(R.id.dateView);
            duration = itemView.findViewById(R.id.duration);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FocusAdapter.FocusViewHolder holder, int position) {
        holder.duration.setText(focusList.get(position).getmDuration());
        holder.Task.setText(focusList.get(position).getmTask());
        holder.date.setText(focusList.get(position).getmDateTime());
        if(focusList.get(position).ismCompletion()) {
            holder.iconComplete.setImageResource(R.drawable.ic_tick);
        }
        else{
            holder.iconComplete.setImageResource(R.drawable.ic_cross);
        }
    }

    @Override
    public int getItemCount() {
        return focusList.size();
    }
}

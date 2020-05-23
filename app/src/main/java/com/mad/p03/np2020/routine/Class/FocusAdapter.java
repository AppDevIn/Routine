package com.mad.p03.np2020.routine.Class;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.mad.p03.np2020.routine.R;

import java.util.List;

public class FocusAdapter extends RecyclerView.Adapter<FocusViewHolder> {

    private List<FocusHolder> focusList; //List of focus
    private Context context; //Current context

    public FocusAdapter(List<FocusHolder> focusList, Context context) {
        this.context = context;
        this.focusList = focusList;
    }

    @NonNull
    @Override
    public FocusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View historyView = LayoutInflater.from(context).inflate(R.layout.layout_itemfocus, parent, false);
        return new FocusViewHolder(historyView, context, this);
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
    public void remove(int position) {
        focusList.remove(position);
        this.notifyItemRemoved(position);
    }

    //get item count
    @Override
    public int getItemCount() {
        return focusList.size();
    }


}

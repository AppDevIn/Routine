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

import com.mad.p03.np2020.routine.Adapter.FocusAdapter;
import com.mad.p03.np2020.routine.R;

public class FocusViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public ImageView iconComplete;
    public TextView Task, date, duration;
    public FocusAdapter adapter;
    public ViewGroup parent;

    public FocusViewHolder(@NonNull View itemView, Context context, FocusAdapter adapter, ViewGroup parent) {
        super(itemView);
        iconComplete = itemView.findViewById(R.id.icon);
        Task = itemView.findViewById(R.id.taskView);
        date = itemView.findViewById(R.id.dateView);
        duration = itemView.findViewById(R.id.duration);

        this.adapter = adapter;
        this.parent = parent;

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) { //If item on click
        showAlertDialogButtonClicked(this.getLayoutPosition(), adapter.getItems(getLayoutPosition()));
        Log.v("item", "Item on click");
    }

    //Show custom alert dialog builder
    public void showAlertDialogButtonClicked(final int position, final Focus focus) {
        final String task = Task.getText().toString();

        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
        builder.setTitle("Delete");

        LayoutInflater inflater = LayoutInflater.from(itemView.getContext());
        View customLayout = inflater.inflate(R.layout.custom_delete_layout, null);

        //Used for alertDialog subText
        TextView staskName = customLayout.findViewById(R.id.taskName);
        staskName.setText(task);

        builder.setView(customLayout);
        builder.setNegativeButton("No", null);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v("AlertDialog", "Delete Item " + task);
                adapter.remove(position, focus);
            }
        });

        // create and show the alert dialog
        final AlertDialog dialog = builder.create();
        dialog.show();
    }
}
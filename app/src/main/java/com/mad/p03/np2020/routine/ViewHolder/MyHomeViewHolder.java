package com.mad.p03.np2020.routine.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.p03.np2020.routine.Adapter.OnSectionListener;
import com.mad.p03.np2020.routine.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyHomeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public TextView mTextViewListName;
    public ImageView mImgBackGround;
    public ImageView mimgIcon;

    //Listener
    private OnSectionListener mOnSectionListener;


    public MyHomeViewHolder(@NonNull View v, OnSectionListener onSectionListener) {
        super(v);
        //Initialize variables
        mTextViewListName = v.findViewById(R.id.listName);
        mImgBackGround = v.findViewById(R.id.backgroud);
        mimgIcon = v.findViewById(R.id.todoIcon);

        this.mOnSectionListener = onSectionListener;

        v.setOnClickListener(this);
        v.setOnLongClickListener(this);

    }

    @Override
    public void onClick(View view) {
        mOnSectionListener.onSectionClick(getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View view) {
        mOnSectionListener.onSectionLongClick(getAdapterPosition());
        return false;
    }
}

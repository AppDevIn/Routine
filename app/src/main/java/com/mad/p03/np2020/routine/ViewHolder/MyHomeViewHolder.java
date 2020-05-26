package com.mad.p03.np2020.routine.ViewHolder;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mad.p03.np2020.routine.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyHomeViewHolder extends RecyclerView.ViewHolder {

    public TextView mTextViewListName;
    public ImageView mImgBackGround;
    public ImageView mimgIcon;


    public MyHomeViewHolder(@NonNull View v) {
        super(v);
        //Initialize variables
        mTextViewListName = v.findViewById(R.id.listName);
        mImgBackGround = v.findViewById(R.id.backgroud);
        mimgIcon = v.findViewById(R.id.todoIcon);

    }
}

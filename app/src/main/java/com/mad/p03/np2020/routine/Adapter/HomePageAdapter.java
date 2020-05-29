package com.mad.p03.np2020.routine.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.HabitActivity;
import com.mad.p03.np2020.routine.Home;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.ViewHolder.MyHomeViewHolder;
import com.mad.p03.np2020.routine.background.DeleteSectionWorker;
import com.mad.p03.np2020.routine.background.UploadSectionWorker;
import com.mad.p03.np2020.routine.database.SectionDBHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class HomePageAdapter extends RecyclerView.Adapter<MyHomeViewHolder> {

    private final String TAG = "HomeAdapter";

    List<Section> mSectionList = new ArrayList<>();
    Context mContext;

    //Listener
    OnSectionListener mOnSectionListener;



    public HomePageAdapter(Context context,List<Section> sectionList, OnSectionListener onSectionListener) {
        mSectionList = sectionList;
        this.mContext = context;
        this.mOnSectionListener = onSectionListener;

        Log.d(TAG, "Total items: " + mSectionList.size());

    }

    @NonNull
    @Override
    public MyHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Inflate the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_grid_view_items, parent, false);


        return new MyHomeViewHolder(view, mOnSectionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHomeViewHolder holder, final int position) {
        //***************** Set values into view *****************//

        //For the TextView
        holder.mTextViewListName.setText(mSectionList.get(position).getName());

        //For background

        //Setup drawable
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(mSectionList.get(position).getBackgroundColor());
        shape.setCornerRadius(30);


        //Set the drawable
        holder.mImgBackGround.setBackground(shape);

        //Setting the image icon
        holder.mimgIcon.setImageResource(mSectionList.get(position).getBmiIcon());

    }

    @Override
    public int getItemCount() {
        return mSectionList.size();
    }

    /**
     * To be able give back the section for the list in the adapter
     *
     * @param position
     * @return give back the section for the position
     */
    public Section getSection(int position) {
        return mSectionList.get(position);
    }


    /**
     * Adding the section to
     * the list
     * @param section the section that will be added
     */
    public void addItem(Section section){
        mSectionList.add(section);

        //Informing the adapter and view of the new item
        notifyItemInserted(mSectionList.size());
        Log.d(TAG, "New TODO added, " + section.toString());
    }

    /**
     * The place to delete the item in the list
     * @param position The position from the data will be removed from
     */
    public void removeItem(int position){
        Log.d(TAG, "Removing " + mSectionList.get(position));

        mSectionList.remove(position);

        //Informing the adapter and view after removing
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mSectionList.size());

    }







}


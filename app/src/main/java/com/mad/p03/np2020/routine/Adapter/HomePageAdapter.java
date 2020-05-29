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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class HomePageAdapter extends RecyclerView.Adapter<MyHomeViewHolder> implements ItemTouchHelperAdapter, OnSectionListener{

    private final String TAG = "HomeAdapter";

    List<Section> mSectionList = new ArrayList<>();
    Context mContext;
    LifecycleOwner mOwner;

    //Listener
    private ItemTouchHelper mItemTouchHelper;



    public HomePageAdapter(List<Section> sectionList, LifecycleOwner owner) {
        mSectionList = sectionList;
        this.mOwner = owner;

        Log.d(TAG, "Total items: " + mSectionList.size());

    }

    @NonNull
    @Override
    public MyHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Inflate the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_grid_view_items, parent, false);
        mContext = view.getContext();

        return new MyHomeViewHolder(view, this, mItemTouchHelper);
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

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
         Section fromSection = mSectionList.get(fromPosition);
         mSectionList.remove(fromPosition);
         mSectionList.add(toPosition, fromSection);
         notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemSwiped(int position) {
        Log.d(TAG, "onItemSwiped: " + position);

        confirmDelete(position);
    }


    @Override
    public void onSectionClick(int position) {
        Log.d(TAG, "onClick(): You have clicked on " + mSectionList.get(position).getName() + " list");
    }

    public void setTouchHelper(ItemTouchHelper itemTouchHelper){
        this.mItemTouchHelper = itemTouchHelper;
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



    private void confirmDelete(final int position){
        Log.d(TAG, "Deletion prompt is building");

        final Section section = getSection(position);

        //Create a alert builder
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        //Inflate the custom layout
        View dialogLayout = LayoutInflater.from(mContext).inflate(R.layout.alert_dialog_cfm_delete, null);

        //Set the message in the view
        TextView txtMessage = dialogLayout.findViewById(R.id.txtMessage);// Find id in the custom dialog
        //Setting the message using HTML format so I can have a bold and normal text
        txtMessage.setText(Html.fromHtml( "<div>Are you sure you want to delete<br/>"+ "<b>" + section.getName() + "?</b></div>"));

        //Set trash in image view
        ImageView imgTrash = dialogLayout.findViewById(R.id.imgTrash); //Find the image view in the custom dialog
        imgTrash.setImageResource(android.R.drawable.ic_menu_delete); //Set the image from the android library delete


        builder.setTitle(R.string.delete); //Set the title of the dialog

        //Set a positive button: Yes
        //Method should remove the  item
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, section.getName() + " task is going to be deleted");

                //Remove from firebase
                mSectionList.get(position).executeFirebaseSectionDelete(mOwner);

                //Remove from SQL
                section.deleteSection(mContext);


            }
        });

        //Set negative button: No
        //Method should close the dialog
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, section.getName() + " task not getting deleted");
                notifyDataSetChanged();
            }
        });

        builder.setCancelable(false); //To prevent user from existing when clicking outside of the dialog
        builder.setView(dialogLayout);//Set the custom view
        builder.show();//Show the view to the user
        Log.d(TAG, "Deletion prompt is shown");
    }








}


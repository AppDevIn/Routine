package com.mad.p03.np2020.routine.Adapter;

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

import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.HabitActivity;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.ViewHolder.MyHomeViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

public class HomePageAdapter extends RecyclerView.Adapter<MyHomeViewHolder> {

    private final String TAG = "HomeAdapter";

    List<Section> mSectionList = new ArrayList<>();
    Context mContext;



    public HomePageAdapter(Context context,List<Section> sectionList) {
        mSectionList = sectionList;
        this.mContext = context;

        Log.d(TAG, "Total items: " + mSectionList.size());

    }

    @NonNull
    @Override
    public MyHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Inflate the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_grid_view_items, parent, false);

        return new MyHomeViewHolder(view);
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

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick(): You have clicked on " + mSectionList.get(position).getName() + " list");
            }
        });

        //Set a long listener
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "onLongClick(): " + position + " has been longed");
                Log.d(TAG, "onLongClick(): Alert dialog triggered");

                confirmDelete(position);

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSectionList.size();
    }

    public void addItem(Section section){
        mSectionList.add(section);

        //Informing the adapter and view of the new item
        notifyItemInserted(mSectionList.size());
        Log.d(TAG, "New TODO added, " + section.toString());
    }

    public void removeItem(int position){
        Log.d(TAG, "Removing " + mSectionList.get(position));

        mSectionList.remove(position);

        //Informing the adapter and view after removing
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mSectionList.size());

    }

    //Give back the current array list
    public List<Section> getSectionList() {
        return mSectionList;
    }

    public String getSectionName(int position){
        return mSectionList.get(position).getName();
    }




    /*************** HELPER FUNCTIONS ***************************/
    private void confirmDelete(final int position){
        Log.d(TAG, "Deletion prompt is building");

        //Create a alert builder
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        //Inflate the custom layout
        View dialogLayout = LayoutInflater.from(mContext).inflate(R.layout.alert_dialog_cfm_delete, null);

        //Set the message in the view
        TextView txtMessage = dialogLayout.findViewById(R.id.txtMessage);// Find id in the custom dialog
        //Setting the message using HTML format so I can have a bold and normal text
        txtMessage.setText(Html.fromHtml( "<div>Are you sure you want to delete<br/>"+ "<b>" + getSectionName(position) + "?</b></div>"));

        //Set trash in image view
        ImageView imgTrash = dialogLayout.findViewById(R.id.imgTrash); //Find the image view in the custom dialog
        imgTrash.setImageResource(android.R.drawable.ic_menu_delete); //Set the image from the android library delete


        builder.setTitle(R.string.delete); //Set the title of the dialog

        //Set a positive button: Yes
        //Method should remove the  item
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, getSectionName(position) + "task is going to be deleted");
                removeItem(position); //Remove item from the data in adapter
            }
        });

        //Set negative button: No
        //Method should close the dialog
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, getSectionName(position) + " task not getting deleted");
            }
        });

        builder.setCancelable(false); //To prevent user from existing when clicking outside of the dialog
        builder.setView(dialogLayout);//Set the custom view
        builder.show();//Show the view to the user
        Log.d(TAG, "Deletion prompt is shown");
    }
}


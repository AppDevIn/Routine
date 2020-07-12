package com.mad.p03.np2020.routine.Home.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mad.p03.np2020.routine.DAL.TaskDBHelper;
import com.mad.p03.np2020.routine.Home.Home;
import com.mad.p03.np2020.routine.Home.models.HomeItemTouchHelperAdapter;
import com.mad.p03.np2020.routine.helpers.HomeIcon;
import com.mad.p03.np2020.routine.models.Section;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Task.TaskActivity;
import com.mad.p03.np2020.routine.Home.ViewHolder.MyHomeViewHolder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;


/**
 *
 * This will be the controller glue between the viewholder and the model
 * This will inflate the the items for the sections to which will give us
 * the view from will be passed to the view holder MyHomeViewHolder.
 *
 * In here you should be able to move, swipe click, add and delete the section
 *
 *
 * @author Jeyavishnu
 * @since 02-06-2020
 */
public class HomePageAdapter extends RecyclerView.Adapter<MyHomeViewHolder> implements HomeItemTouchHelperAdapter {

    private final String TAG = "HomeAdapter";
    private List<Section> mSectionList;
    private Context mContext;
    private LifecycleOwner mOwner;

    //Listener
    private ItemTouchHelper mItemTouchHelper;


    /**
     *
     * Will set the data and the lifecycle owner, give access to
     * the adapter's methods. Sets the list into the member variable
     * and set the owner into the member variable
     *
     * @param sectionList This will be the list of section
     * @param owner Owner of the lifecycle to be able to see
     *             lifecycle changes
     */
    public HomePageAdapter(List<Section> sectionList, LifecycleOwner owner) {
        mSectionList = sectionList;
        this.mOwner = owner;

        Log.d(TAG, "Total items: " + mSectionList.size());

    }

    /**
     *
     * Called when RecyclerView needs a new View Holder of the given type to represent the section
     *
     * This ViewHolder will be constructed with a new view that will represent the Section which consist
     * of name, color of the section and the icon that represent it. The view will be inflated from XML file
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return MyHomeViewHolder with the view inflated
     */
    @NonNull
    @Override
    public MyHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Inflate the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_grid_view_items, parent, false);
        mContext = view.getContext();

        return new MyHomeViewHolder(view, mItemTouchHelper, this);
    }

    /**
     *
     * This will be called to display the section data at the specific position. This will update
     * the contents of the itemView to which will reflect at the given position
     *
     * @param holder The ViewHolder which should be updated to represent the contents of
     *               the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MyHomeViewHolder holder, final int position) {
        //***************** Set values into view *****************//


        //For the TextView
        holder.mTextViewListName.setText(mSectionList.get(position).getName());


        //Setup drawable
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(mSectionList.get(position).getBackgroundColor());
        shape.setCornerRadius(30);


        //Set the drawable
        holder.mBackGround.setBackground(shape);

        //Setting the image icon
        holder.mimgIcon.setImageResource(HomeIcon.getBackground(mSectionList.get(position).getBmiIcon()));

    }

    /**
     * Returns the total number of items in the list set and held by the adapter.
     *
     * @return The total number of items in list
     */
    @Override
    public int getItemCount() {
        return mSectionList.size();
    }

    /**
     *
     * This is when the item is moved it will
     * move the items in the array by removing
     * the object from that position and adding
     * it into the new position
     *
     * @param fromPosition int where the item was
     * @param toPosition int where the item is currently at
     */
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        //Create the object
         Section fromSection = mSectionList.get(fromPosition);

         mSectionList.remove(fromPosition); // Remove the object from the list
         mSectionList.add(toPosition, fromSection); // Add the the object into the new position

         //Notify the adapter the change
         notifyItemMoved(fromPosition, toPosition);

        Log.d(TAG, "onItemMove(): From: " + fromPosition + " To: " + toPosition);
    }


    /**
     * When it is swiped it will call the a alert dialog to confirm the delete
     *
     * @param position This is the position it was swiped from
     */
    @Override
    public void onItemSwiped(int position) {
        Log.d(TAG, "onItemSwiped(): Item swiped on position " + position);

        confirmDelete(position);
    }


    /**
     *
     * This used to intent to another layout
     * and adding data object section in it which
     * can be retrieved from the other layout
     * by calling {@code getIntent()}.
     *
     * @param position The item position it was clicked from
     */
    @Override
    public void onItemClicked(int position) {
        Log.d(TAG, "onClick(): You have clicked on " + mSectionList.get(position).getName() + " list");

        //Move to new activity
        Intent intent = new Intent(mContext, TaskActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtra("section", mSectionList.get(position)); // Add the object
        mContext.startActivity(intent); //Start the activity
    }


    /**
     * This is method is used to set the custom item touch helper
     * @param itemTouchHelper The custom touch helper that will be used
     *                        to controller to movie of the viewholder
     */
    public void setTouchHelper(ItemTouchHelper itemTouchHelper){
        this.mItemTouchHelper = itemTouchHelper;
    }


    /**
     * To be able give back the section from the list in the adapter
     *
     * @param position which position of the list
     *                 do you want the section from
     * @return give back the section for the position
     */
    public Section getSection(int position) {
        return mSectionList.get(position);
    }


    /**
     * Adding the section to
     * the list and notifying the adapter
     * of the change
     * @param section the section that will be added
     */
    public void addItem(Section section){
        mSectionList.add(section);

        //Informing the adapter and view of the new item
        notifyItemInserted(mSectionList.size());
        Log.d(TAG, "New TODO added, " + section.toString());
    }

    /**
     * The place to delete the item in the list and notify the change
     * to the adapter
     *
     * @param position The position from the data will be removed from
     */
    public void removeItem(int position){
        Log.d(TAG, "Removing " + mSectionList.get(position));

        mSectionList.remove(position);

        //Informing the adapter and view after removing
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mSectionList.size());

    }


    /**
     *
     * This method builds the adapter and inflates a view for a image
     * with positive and negative button (yes and no). When the positive button
     * is clicked the it will remove from firebase followed by removing it from SQL
     *
     * @param position which position in the list will the data be deleted
     */
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


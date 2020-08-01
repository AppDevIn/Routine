package com.mad.p03.np2020.routine.Focus.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.signature.ObjectKey;
import com.mad.p03.np2020.routine.BuildConfig;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Focus.ViewHolder.ItemAchievementViewHolder;
import com.mad.p03.np2020.routine.Focus.Model.Achievement;
import com.mad.p03.np2020.routine.models.User;

import java.io.File;

/***
 *
 * This adapter is used to display the gridviewAdpater Achievments bind to ItemAchievmentViewholder
 * user is allowed to click on the item to show the details of the badge and share to Instagram
 *
 *  @author Lee Quan Sheng
 *  @since 01-08-2020
 */
public class GridViewAdapterAchievements extends RecyclerView.Adapter<ItemAchievementViewHolder> {

    private final Context mContext;
    private final User.achievementView achievements;
    private String TAG = "Achievement";

    AlertDialog.Builder builder;
    AlertDialog dialog;

    /**
     *
     * @param context
     * @param achievements The parameter that is passed is the user achievements to be loaded in the gridView
     */
    public GridViewAdapterAchievements(Context context, User.achievementView achievements) {
        this.mContext = context;
        this.achievements = achievements;
    }


    /***
     *
     * @param parent    The ViewGroup is the parent view that will hold your cell that you are about to create. So, the ViewGroup parent is the RecyclerView here (it will hold your cell). The parent is used during the layout inflation process so you can see it passed in to the inflate call.
     * @param viewType The viewType is useful if you have different types of cells in your list. For example, if you have a header cell and a detail cell. You can use the viewType to make sure that you inflate the correct layout file for each of those two types of cells.
     * @return return the Viewholder that is created within the onCreateViewHolder method
     */
    @NonNull
    @Override
    public ItemAchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View historyView = LayoutInflater.from(mContext).inflate(R.layout.layout_item_achievement, parent, false); //Display the achievement
        return new ItemAchievementViewHolder(historyView, this, parent);

    }


    /***
     *
     * @param holder The viewholder of the badges recyclerview
     * @param position The position of the badges recyclerview
     */
    @Override
    public void onBindViewHolder(@NonNull ItemAchievementViewHolder holder, int position) {
        // 1
        Achievement achievement = achievements.getArrayList().get(position);
        Glide.get(mContext).clearMemory();

        if (achievement != null) {
            // 4
            //Setting the title of achievements
            holder.requirement.setText(String.valueOf(achievement.getRequirement()));

            File imgFile = achievement.getPathImg();
            holder.badgeImage.setImageDrawable(null);
            Log.v(TAG, "File from " + achievement.getPathImg());
            if (imgFile.exists()) {
                Glide.with(mContext).load(new File(imgFile.getAbsolutePath())).signature(new ObjectKey(String.valueOf(achievement.getBadgeUrl()))).transform(new CircleCrop()).into(holder.badgeImage);
            }
        } else {
            holder.requirement.setText("??");
            assert achievement != null;

        }

        // This method must be called on the main thread.


    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return achievements.getArrayList().size();
    }

    /**
     * this method will be executed when the share button is clicked
     * It will share the badge logo to instagram stories
     * @param position The position of the badge
     */
    public void shareFileToInstagram(int position) {
        Achievement achievement = achievements.getArrayList().get(position);

        // Define background and sticker asset URIs and attribution link URL
        Uri stickerAssetUri = getImageUri(achievement.getPathImg());
        String attributionLinkUrl = "https://play.google.com/store/apps/details?id=com.mad.p03.np2020.routine&hl=en";

        // Instantiate implicit intent with ADD_TO_STORY action,
        // background asset, sticker asset, and attribution link
        Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/jpeg");
        intent.putExtra("interactive_asset_uri", stickerAssetUri);
        intent.putExtra("content_url", attributionLinkUrl);
        intent.putExtra("top_background_color", "#33FF33");
        intent.putExtra("bottom_background_color", "#FF00FF");
        intent.putExtra("content_url", attributionLinkUrl);


        // Instantiate activity and verify it will resolve implicit intent
        Activity activity = (Activity) mContext;
        activity.grantUriPermission(
                "com.instagram.android", stickerAssetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
            activity.startActivityForResult(intent, 0);
        }else{
            Toast.makeText(mContext, "Instagram not found on your device", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Get the image URI of the badge in order to build the authority for the image to convert as URI
     *
     * @param imgFile
     * @return
     */
    private Uri getImageUri(File imgFile) {
        return FileProvider.getUriForFile(mContext.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", imgFile);
    }

    /**
     * Get the description of the badge
     * @param position
     */
    public void showBadgeAchieved(int position) {
        if (dialog == null || !dialog.isShowing()) {

            TextView badgeTitle, badgeDescription, close;
            ImageView badgeImage;
            ImageButton shareButton;
            Achievement achievement = achievements.getArrayList().get(position);

            builder = new AlertDialog.Builder(mContext, R.style.BadgeDialog);

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View shareCustomLayout = inflater.inflate(R.layout.custom_share_layout, null);

            //Find view of each respective Widget
            badgeTitle = shareCustomLayout.findViewById(R.id.badgeTitle);
            badgeDescription = shareCustomLayout.findViewById(R.id.badgeDescription);
            close = shareCustomLayout.findViewById(R.id.closeViewAchievementPopup);
            shareButton = shareCustomLayout.findViewById(R.id.shareButton);
            badgeImage = shareCustomLayout.findViewById(R.id.badgeView);

            close.setOnClickListener(view -> dialog.cancel());

            //When the share button is click, it will share the image to instagram story
            shareButton.setOnClickListener(view -> shareFileToInstagram(position));


            //This will be displayed if the achievement is not unlocked
            if (achievement == null) {
                badgeTitle.setText("Achievement Locked");
                badgeDescription.setText("You have to reached ??");
                shareButton.setVisibility(View.INVISIBLE);
            } else {
                //This will be displayed if the achievement is unlocked
                badgeTitle.setText("Achievement Unlocked");
                badgeDescription.setText("You reached " + achievement.getRequirement() + " " + achievement.getAchievementName(achievement.getTypeAchievement() - 1));
                File imgFile = achievement.getPathImg();
                if (imgFile.exists()) {
                    //Glide is used to load the image into the imageView of the badge
                    Glide.with(mContext).load(new File(imgFile.getAbsolutePath())).signature(new ObjectKey(String.valueOf(achievement.getBadgeUrl()))).transform(new CircleCrop()).into(badgeImage);
                }
            }

            builder.setView(shareCustomLayout);

            // create and show the alert dialog
            dialog = builder.create();

            dialog.show();

        }
    }
}
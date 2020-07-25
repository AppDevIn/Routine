package com.mad.p03.np2020.routine.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.signature.ObjectKey;
import com.mad.p03.np2020.routine.BuildConfig;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.ViewHolder.AchievementViewHolder;
import com.mad.p03.np2020.routine.ViewHolder.ItemAchievementViewHolder;
import com.mad.p03.np2020.routine.models.Achievement;
import com.mad.p03.np2020.routine.models.User;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;

public class GridViewAdapterAchievements extends RecyclerView.Adapter<ItemAchievementViewHolder> {

    private final Context mContext;
    private final User.achievementView achievements;
    private String TAG = "Achievement";


    // 1
    public GridViewAdapterAchievements(Context context, User.achievementView achievements) {
        this.mContext = context;
        this.achievements = achievements;
    }

    @NonNull
    @Override
    public ItemAchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View historyView = LayoutInflater.from(mContext).inflate(R.layout.layout_item_achievement, parent, false);
        return new ItemAchievementViewHolder(historyView, this, parent);

    }

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

    public void shareFileToInstagram(int position) {
        Achievement achievement = achievements.getArrayList().get(position);

        // Define background and sticker asset URIs and attribution link URL
        Uri backgroundAssetUri = getImageUri(achievement.getPathImg());
        Uri stickerAssetUri = getImageUri(achievement.getPathImg());
        String attributionLinkUrl = "https://www.my-aweseome-app.com/p/BhzbIOUBval/";

        // Instantiate implicit intent with ADD_TO_STORY action,
        // background asset, sticker asset, and attribution link
        Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(backgroundAssetUri, "image/jpeg");
        intent.putExtra("interactive_asset_uri", stickerAssetUri);
        intent.putExtra("content_url", attributionLinkUrl);

        // Instantiate activity and verify it will resolve implicit intent
        Activity activity = (Activity) mContext;
        activity.grantUriPermission(
                "com.instagram.android", stickerAssetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
            activity.startActivityForResult(intent, 0);
        }

    }

    private Uri getImageUri(File imgFile) {
        return FileProvider.getUriForFile(mContext.getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", imgFile);
    }

}
package com.mad.p03.np2020.routine.background;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mad.p03.np2020.routine.Focus.DAL.AchievementDBHelper;
import com.mad.p03.np2020.routine.Focus.Model.Achievement;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * The worker delete and update the achievement data in firebase from
 * the background which extends the Worker.
 *
 * @author Lee Quan Sheng
 * @since 02-08-2020
 */

public class GetAchievementWorker extends Worker {
    AchievementDBHelper achievementDBHelper;
    String TAG = "AchievementWorker";

    public GetAchievementWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        achievementDBHelper = new AchievementDBHelper(context);

    }

    @NonNull
    @Override
    public Result doWork() {
        achievementHour();
        achievementCycle();
        achievementConsecutive();
        return Result.success();
    }

    /***
     * get achievement that is on hours
     */
    private void achievementHour() {
        int type = 1;

        //Get achievement data
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("achievement").child("Hours");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "Adding data " + dataSnapshot.getKey());

                String task = dataSnapshot.getKey();
                //Check if task exist in database
                if (!achievementDBHelper.Exist(task, type)) {
                    String stageNo = task;
                    String fileName = dataSnapshot.child("Badge").getValue(String.class);
                    int typeAchievement = 1;
                    int requirement = dataSnapshot.child("Requirement").getValue(Integer.class);
                    String badgeUrl = String.valueOf(dataSnapshot.child("BadgeUrl").getValue(Integer.class));


                    Achievement achievement = new Achievement();
                    achievement.setRequirement(requirement);
                    achievement.setFilename(fileName);
                    achievement.setTypeAchievement(typeAchievement);
                    achievement.setStageNo(stageNo);
                    achievement.setBadgeUrl(badgeUrl);
                    getImagesHourData(achievement.getFilename());

                    achievementDBHelper.insertAchievements(achievement);
                } else {

                    String stageNo = task;
                    String fileName = dataSnapshot.child("Badge").getValue(String.class);
                    int typeAchievement = 1;
                    int requirement = dataSnapshot.child("Requirement").getValue(Integer.class);
                    String badgeUrl = String.valueOf(dataSnapshot.child("BadgeUrl").getValue(Integer.class));

                    Achievement achievement = new Achievement();
                    achievement.setRequirement(requirement);
                    achievement.setFilename(fileName);
                    achievement.setTypeAchievement(typeAchievement);
                    achievement.setStageNo(stageNo);
                    achievement.setBadgeUrl(badgeUrl);

                    Achievement taskDataBase = achievementDBHelper.getAchievementItem(stageNo, typeAchievement);
                    if (!achievement.equals(taskDataBase)) {
                        achievementDBHelper.update(achievement);

                        //Used to check if the badge datetime last update is the same
                        if (!achievement.getBadgeUrl().equals(taskDataBase.getBadgeUrl())) {
                            getImagesHourData(achievement.getFilename());
                        }

                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged(): " + dataSnapshot.getKey());
                Log.d(TAG, "onChildChanged(): " + dataSnapshot.getValue());
                String task = dataSnapshot.getKey();

                String stageNo = task;
                String fileName = dataSnapshot.child("Badge").getValue(String.class);
                int typeAchievement = 1;
                int requirement = dataSnapshot.child("Requirement").getValue(Integer.class);
                String badgeUrl = String.valueOf(dataSnapshot.child("BadgeUrl").getValue(Integer.class));

                Achievement achievement = new Achievement();
                achievement.setRequirement(requirement);
                achievement.setFilename(fileName);
                achievement.setTypeAchievement(typeAchievement);
                achievement.setStageNo(stageNo);
                achievement.setBadgeUrl(badgeUrl);

                Achievement taskDataBase = achievementDBHelper.getAchievementItem(stageNo, typeAchievement);

                if (!achievement.equals(taskDataBase)) {
                    achievementDBHelper.update(achievement);
                    //Used to check if the badge datetime last update is the same
                    if (!achievement.getBadgeUrl().equals(taskDataBase.getBadgeUrl())) {
                        getImagesHourData(achievement.getFilename());
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "Delete Data " + dataSnapshot);

                String task = dataSnapshot.getKey();

                //Used to check if database exist, then delete the existing data
                if (achievementDBHelper.Exist(task, type)) {
                    achievementDBHelper.delete(task, type);
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /***
     * get achievement that is on cycle
     */
    private void achievementCycle() {
        int type = 2;

        //Get achievement data
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("achievement").child("Cycle");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "Adding data " + dataSnapshot.getKey());

                String task = dataSnapshot.getKey();
                //Check if task exist in database
                if (!achievementDBHelper.Exist(task, type)) {
                    String stageNo = task;
                    String fileName = dataSnapshot.child("Badge").getValue(String.class);
                    int typeAchievement = 2;
                    int requirement = dataSnapshot.child("Requirement").getValue(Integer.class);
                    String badgeUrl = String.valueOf(dataSnapshot.child("BadgeUrl").getValue(Integer.class));

                    Achievement achievement = new Achievement();
                    achievement.setRequirement(requirement);
                    achievement.setFilename(fileName);
                    achievement.setTypeAchievement(typeAchievement);
                    achievement.setStageNo(stageNo);
                    achievement.setBadgeUrl(badgeUrl);

                    achievementDBHelper.insertAchievements(achievement);
                    //Used to check if the badge datetime last update is the same
                    getImagesCycleData(achievement.getFilename());

                } else {

                    String stageNo = task;
                    String fileName = dataSnapshot.child("Badge").getValue(String.class);
                    int typeAchievement = 2;
                    int requirement = dataSnapshot.child("Requirement").getValue(Integer.class);
                    String badgeUrl = String.valueOf(dataSnapshot.child("BadgeUrl").getValue(Integer.class));

                    Achievement achievement = new Achievement();
                    achievement.setRequirement(requirement);
                    achievement.setFilename(fileName);
                    achievement.setTypeAchievement(typeAchievement);
                    achievement.setStageNo(stageNo);
                    achievement.setBadgeUrl(badgeUrl);

                    Achievement taskDataBase = achievementDBHelper.getAchievementItem(stageNo, typeAchievement);
                    if (!achievement.equals(taskDataBase)) {
                        achievementDBHelper.update(achievement);
                        //Used to check if the badge datetime last update is the same
                        if (!achievement.getBadgeUrl().equals(taskDataBase.getBadgeUrl())) {
                            getImagesCycleData(achievement.getFilename());
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged(): " + dataSnapshot.getKey());
                Log.d(TAG, "onChildChanged(): " + dataSnapshot.getValue());
                String task = dataSnapshot.getKey();

                String stageNo = task;
                String fileName = dataSnapshot.child("Badge").getValue(String.class);
                int typeAchievement = 2;
                int requirement = dataSnapshot.child("Requirement").getValue(Integer.class);
                String badgeUrl = String.valueOf(dataSnapshot.child("BadgeUrl").getValue(Integer.class));

                Achievement achievement = new Achievement();
                achievement.setRequirement(requirement);
                achievement.setFilename(fileName);
                achievement.setTypeAchievement(typeAchievement);
                achievement.setStageNo(stageNo);
                achievement.setBadgeUrl(badgeUrl);

                Achievement taskDataBase = achievementDBHelper.getAchievementItem(stageNo, typeAchievement);

                if (!achievement.equals(taskDataBase)) {
                    if (!achievement.getBadgeUrl().equals(taskDataBase.getBadgeUrl())) {
                        getImagesCycleData(achievement.getFilename());
                    }
                    achievementDBHelper.update(achievement);
                    //Used to check if the badge datetime last update is the same

                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "Delete Data " + dataSnapshot);

                String task = dataSnapshot.getKey();

                //Used to check if database exist, then delete the existing data
                if (achievementDBHelper.Exist(task, type)) {
                    achievementDBHelper.delete(task, type);
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    /***
     * get achievement that is on consecutive
     */
    private void achievementConsecutive() {
        int type = 3;

        //Get achievement data
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("achievement").child("Consecutive");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "Adding data " + dataSnapshot.getKey());

                String task = dataSnapshot.getKey();
                //Check if task exist in database
                if (!achievementDBHelper.Exist(task, type)) {
                    String stageNo = task;
                    String fileName = dataSnapshot.child("Badge").getValue(String.class);
                    int typeAchievement = 3;
                    int requirement = dataSnapshot.child("Requirement").getValue(Integer.class);
                    String badgeUrl = String.valueOf(dataSnapshot.child("BadgeUrl").getValue(Integer.class));

                    Achievement achievement = new Achievement();
                    achievement.setRequirement(requirement);
                    achievement.setFilename(fileName);
                    achievement.setTypeAchievement(typeAchievement);
                    achievement.setStageNo(stageNo);
                    achievement.setBadgeUrl(badgeUrl);

                    achievementDBHelper.insertAchievements(achievement);
                    //Used to check if the badge datetime last update is the same

                    getImagesConsecutiveData(achievement.getFilename());

                } else {

                    String stageNo = task;
                    String fileName = dataSnapshot.child("Badge").getValue(String.class);
                    int typeAchievement = 3;
                    int requirement = dataSnapshot.child("Requirement").getValue(Integer.class);
                    String badgeUrl = String.valueOf(dataSnapshot.child("BadgeUrl").getValue(Integer.class));

                    Achievement achievement = new Achievement();
                    achievement.setRequirement(requirement);
                    achievement.setFilename(fileName);
                    achievement.setTypeAchievement(typeAchievement);
                    achievement.setStageNo(stageNo);
                    achievement.setBadgeUrl(badgeUrl);

                    Achievement taskDataBase = achievementDBHelper.getAchievementItem(stageNo, typeAchievement);
                    if (!achievement.equals(taskDataBase)) {
                        //Used to check if the badge datetime last update is the same
                        if (!achievement.getBadgeUrl().equals(taskDataBase.getBadgeUrl())) {
                            getImagesConsecutiveData(achievement.getFilename());
                        }
                        achievementDBHelper.update(achievement);

                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged(): " + dataSnapshot.getKey());
                Log.d(TAG, "onChildChanged(): " + dataSnapshot.getValue());
                String task = dataSnapshot.getKey();

                String stageNo = task;
                String fileName = dataSnapshot.child("Badge").getValue(String.class);
                int typeAchievement = 3;
                int requirement = dataSnapshot.child("Requirement").getValue(Integer.class);
                String badgeUrl = String.valueOf(dataSnapshot.child("BadgeUrl").getValue(Integer.class));

                Achievement achievement = new Achievement();
                achievement.setRequirement(requirement);
                achievement.setFilename(fileName);
                achievement.setTypeAchievement(typeAchievement);
                achievement.setStageNo(stageNo);
                achievement.setBadgeUrl(badgeUrl);

                Achievement taskDataBase = achievementDBHelper.getAchievementItem(stageNo, typeAchievement);

                if (!achievement.equals(taskDataBase)) {
                    if (!achievement.getBadgeUrl().equals(taskDataBase.getBadgeUrl())) {
                        getImagesConsecutiveData(achievement.getFilename());
                    }
                    achievementDBHelper.update(achievement);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                Log.d(TAG, "Delete Data " + dataSnapshot);

                String task = dataSnapshot.getKey();

                //Used to check if database exist, then delete the existing data
                if (achievementDBHelper.Exist(task, type)) {
                    achievementDBHelper.delete(task, type);
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    protected void getImagesHourData(String imageUrl) {

        Log.v(TAG, "Getting images data from google storage " + imageUrl);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference().child("Hours" + "/" + imageUrl);
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String filename = imageUrl;
            String url = uri.toString();
            new DownloadFileFromURL().execute(url, filename, "1");
        });
    }


    protected void getImagesCycleData(String imageUrl) {

        Log.v(TAG, "Getting images data from google storage " + imageUrl);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference().child("Cycle" + "/" + imageUrl);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String filename = imageUrl;
            String url = uri.toString();
            new DownloadFileFromURL().execute(url, filename, "2");
        });
    }


    protected void getImagesConsecutiveData(String imageUrl) {

        Log.v(TAG, "Getting images data from google storage");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference().child("Consecutive" + "/" + imageUrl);
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String filename = imageUrl;
            String url = uri.toString();
            new DownloadFileFromURL().execute(url, filename, "3");
        });
    }


    class DownloadFileFromURL extends AsyncTask<Object, Void, Bitmap> {
        String filename;
        String foldername;

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected Bitmap doInBackground(Object... URL) {
            String imageURL = (String) URL[0];
            filename = (String) URL[1];
            foldername = (String) URL[2];

            Log.v(TAG, "Getting file: " + filename);

            Log.v(TAG, imageURL);
            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }


        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(Bitmap result) {

            if (result != null) {
                String rootPath = getApplicationContext().getFilesDir() + "/" + foldername + "/";
                Log.v(TAG, "Added to " + rootPath);

                File destination = new File(rootPath);

                if (!destination.mkdirs()) {
                    Log.i("Test", "This path is already exist: " + destination.getAbsolutePath());
                }
                destination = new File(rootPath + filename);

                try {
                    destination.createNewFile();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    result.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    FileOutputStream fos = new FileOutputStream(destination);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();

                    Log.v(TAG, "File saved in " + destination);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}

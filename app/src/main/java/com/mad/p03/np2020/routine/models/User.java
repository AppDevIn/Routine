package com.mad.p03.np2020.routine.models;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.FormatException;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.p03.np2020.routine.Focus.DAL.AchievementDBHelper;
import com.mad.p03.np2020.routine.Focus.Model.Achievement;
import com.mad.p03.np2020.routine.Focus.Model.Focus;
import com.mad.p03.np2020.routine.Focus.Interface.FocusDBObserver;
import com.mad.p03.np2020.routine.Habit.Interface.HabitDBObservable;
import com.mad.p03.np2020.routine.Habit.Interface.HabitDBObserver;
import com.mad.p03.np2020.routine.Habit.models.HabitGroup;
import com.mad.p03.np2020.routine.Habit.models.Habit;
import com.mad.p03.np2020.routine.Habit.models.HabitReminder;
import com.mad.p03.np2020.routine.Habit.models.HabitRepetition;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.background.GetAchievementWorker;
import com.mad.p03.np2020.routine.Habit.DAL.HabitRepetitionDBHelper;
import com.mad.p03.np2020.routine.helpers.GetTaskSectionWorker;
import com.mad.p03.np2020.routine.Register.models.UploadDataWorker;
import com.mad.p03.np2020.routine.Focus.DAL.FocusDBHelper;
import com.mad.p03.np2020.routine.Habit.DAL.HabitDBHelper;
import com.mad.p03.np2020.routine.Habit.DAL.HabitGroupDBHelper;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Model used to manage the user data
 *
 * @author Lee Quan Sheng and Jeyavishnu
 * @since 04-06-2020
 */
public class User implements Parcelable, FocusDBObserver {

    /**
     * The table name for this model
     */
    public static final String TABLE_NAME = "user"; //Name of the table

    /**
     * Used as the primary key for this table
     */
    public static final String COLUMN_NAME_ID = "id";
    /**
     * Column name for table,  to identify the name of the user
     */
    public static final String COLUMN_NAME_NAME = "name";
    /**
     * Column name for table,  to identify the email of the user
     */
    public static final String COLUMN_NAME_EMAIL = "email";
    /**
     * Column name for table,  to identify the password of the task
     */
    public static final String COLUMN_NAME_PASSWORD = "password"; //The password will be encrypted

    /**
     * The query needed to create a sql database
     * for the user
     */
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                    COLUMN_NAME_NAME + " TEXT," +
                    COLUMN_NAME_EMAIL + " TEXT," +
                    COLUMN_NAME_PASSWORD + " TEXT)";

    /**
     * The query needed to drop the table for task
     */
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    final private String TAG = "User";

    //Member variable
    private FirebaseUser mAuth;
    private String mName;
    private String mUID;
    private String mEmailAddr;
    private String mPassword;
    private List<Section> mSectionList = new ArrayList<>();
    private String mPPID;
    private List<Label> mListLabel = new ArrayList<>();
    private ArrayList<Focus> mFocusList = new ArrayList<>();
    private ArrayList<Focus> aFocusList = new ArrayList<>();
    private HashMap<Integer, ArrayList<Achievement>> achievementArrayList = new HashMap<>();


    private DatabaseReference myRef;
    private DatabaseReference userRef;
    private DatabaseReference hRef;
    private FocusDBHelper focusDBHelper;
    private AchievementDBHelper achievementDBHelper;

    private HabitDBHelper habitDBHelper;
    private HabitGroupDBHelper habitGroupDBHelper;
    private HabitRepetitionDBHelper habitRepetitionDBHelper;
    private Habit.HabitList habitList;
    private ArrayList<HabitGroup> habitGroupsList;
    ArrayList<Date> dateArrayList = new ArrayList<>();

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //Dialog Builder
    AlertDialog.Builder builder;
    AlertDialog dialog;

    public static ChildEventListener habitListener, hrListener, hgListener;


    /**
     * Parcelable constructor for custom object
     *
     * @param in Container for a message (data and object references) that is sent through an IBinder
     */
    protected User(Parcel in) {
        mAuth = in.readParcelable(FirebaseUser.class.getClassLoader());
        mName = in.readString();
        mUID = in.readString();
        mEmailAddr = in.readString();
        mPassword = in.readString();
        mPPID = in.readString();
        mFocusList = in.createTypedArrayList(Focus.CREATOR);
    }

    /**
     * Implementation of parcelable interface of a type implements the Parcelable.Creator Interface
     *
     * @param in Container for a message (data and object references) that is sent through an IBinder
     */
    public static final Creator<User> CREATOR = new Creator<User>() {

        /**
         * Implementation of parcelable interface of a type implements the Parcelable.Creator Interface
         *
         * @param in Container for a message (data and object references) that is sent through an IBinder
         * @return User return object create from parcel
         */
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        /**
         * Implementation of parcelable interface of a type implements the Parcelable.Creator Interface
         *
         * @param size Container for a message (data and object references) that is sent through an IBinder
         * @return customObject Array size
         */
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    /**
     * Method to get all FocusList for current User
     *
     * @return ArrayList of focus object
     */
    public ArrayList<Focus> getmFocusList() {
        return mFocusList;
    }

    /**
     * Method to get FocusList based on date for current User
     *
     * @param getDate Date to filter
     * @return ArrayList of focus object
     */
    public ArrayList<Focus> getmFocusList(Date getDate) throws ParseException {

        String pattern = "dd/MM/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);

        String dateSelected = df.format(getDate);

        ArrayList<Focus> focusArrayList = new ArrayList<>();

        for (Focus focus : mFocusList
        ) {
            String date = focus.getsDate();

            if (dateSelected.equals(date)) {
                focusArrayList.add(focus);
            }
        }

        return focusArrayList;
    }

    /***
     * Get the earliest focus date
     *
     * @return
     */
    public Date getMinFocus() {
        if (dateArrayList.size() == 0) {

            return new Date();
        }
        return Collections.min(dateArrayList);
    }

    /***
     * Get the latest focus date
     *
     * @return
     */
    public Date getMaxFocus() {
        if (dateArrayList.size() == 0) {

            return new Date();
        }
        return Collections.max(dateArrayList);

    }

    /***
     * Setting the DB helper
     *
     * @return
     */
    public void setAchievementDBHelper(AchievementDBHelper achievementDBHelper){
        this.achievementDBHelper = achievementDBHelper;
    }

    /***
     * Getting the focus list based on date range
     * @param sDate startDate of the list
     * @param eDate endDate of the list
     * @return ArrayList<Focus></Focus> the filtered dates focus list
     * @throws ParseException
     */
    public ArrayList<Focus> getmFocusList(Date sDate, Date eDate) throws ParseException {
        ArrayList<Focus> focusArrayList = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.setTime(eDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 1);
        cal.set(Calendar.MILLISECOND, 0);

        eDate = cal.getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(sDate);
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, -1);
        cal2.set(Calendar.MILLISECOND, 0);

        sDate = cal2.getTime();

        Log.v(TAG, "Sorting date between " + sDate + " and " + eDate);


        for (Focus focus : mFocusList
        ) {
            Date date = focus.getdDate();
            Log.v(TAG, "Sorting between " + date);

            if (date.after(sDate) && date.before(eDate)) {
                focusArrayList.add(focus);
            }
        }

        return focusArrayList;
    }

    /**
     * Get all unsuccessful focus
     * @return
     */
    public ArrayList<Focus> getmUnsuccessFocusList() {
        ArrayList<Focus> unSuccessList = new ArrayList<>();

        if (mFocusList.size() != 0) {
            for (Focus item : mFocusList) {
                if (item.getmCompletion().equals("False")) {
                    unSuccessList.add(item);
                }
            }
        }

        return unSuccessList;

    }

    /**
     * Get all successful focus
     * @return
     */
    public ArrayList<Focus> getmSuccessFocusList() {
        ArrayList<Focus> SuccessList = new ArrayList<>();

        if (mFocusList.size() != 0) {
            for (Focus item : mFocusList) {
                if (item.getmCompletion().equals("True")) {
                    SuccessList.add(item);
                }
            }
        }

        if (aFocusList.size() != 0) {
            for (Focus item : aFocusList) {
                if (item.getmCompletion().equals("True")) {
                    SuccessList.add(item);
                }
            }
        }

        return SuccessList;
    }

    /**
     * get the highest recurrence that is consecutive successful focus
     * @return
     */
    public int getHighestRecurrence() {
        int highestOccurrence = 0;
        int trace = 0;

        if (mFocusList.size() != 0) {
            for (Focus item : mFocusList) {
                if (item.getmCompletion().equals("True")) {
                    trace++;
                } else {
                    if (highestOccurrence < trace) {
                        highestOccurrence = trace;
                    }
                    trace = 0;
                }
            }
        }

        return highestOccurrence;
    }

    /**
     * Filter the list of focus based on unsuccessful
     * @param mFocusList
     * @return
     */
    public ArrayList<Focus> getmUnsuccessFocusList(ArrayList<Focus> mFocusList) {
        ArrayList<Focus> unSuccessList = new ArrayList<>();

        if (mFocusList.size() != 0) {
            for (Focus item : mFocusList) {
                if (item.getmCompletion().equals("False")) {
                    unSuccessList.add(item);
                }
            }
        }

        return unSuccessList;

    }

    /**
     * Filter the list of focus based on successful focus
     * @param mFocusList
     * @return
     */
    public ArrayList<Focus> getmSuccessFocusList(ArrayList<Focus> mFocusList) {
        ArrayList<Focus> SuccessList = new ArrayList<>();

        if (mFocusList.size() != 0) {
            for (Focus item : mFocusList) {
                if (item.getmCompletion().equals("True")) {
                    SuccessList.add(item);
                }
            }
        }

        return SuccessList;
    }

    /**
     * Get total hour that the user achieves for focus (This does not include archive data)
     * @return
     */
    public int getTotalHours() {
        int hours = 0;
        for (Focus item : mFocusList) {
            if (item.getmCompletion().equals("True")) {
                hours = +Integer.parseInt(item.getmDuration());
            }
        }

        return hours;
    }

    /**
     * get all hour including the archive data
     * @return
     */
    public long getAllHours() {
        long hours = 0;
        for (Focus item : mFocusList) {
            hours = hours + item.getmTimeTaken();
            Log.v(TAG, "Adding to total hours: " + hours);

        }

        for (Focus item : aFocusList) {
            hours = hours + item.getmTimeTaken();
        }
        Log.v(TAG, "Getting total hours: " + hours);
        return hours;
    }


    //Get Hours Achievement List
    public HashMap<Integer, ArrayList<Achievement>> getAchievementArrayList() {
        return achievementArrayList;
    }


    @Override
    public void onDatabaseChanged() {

    }

    /**
     * A custom dictionary to be return two values for achievement page
     */
    public class achievementView {
        private final ArrayList<Achievement> arrayList;
        private final long valueOutput;
        private final int badges;

        public int getBadges() {
            return badges;
        }

        public achievementView(ArrayList<Achievement> first, long second, int badges) {
            this.arrayList = first;
            this.valueOutput = second;
            this.badges = badges;
        }

        public ArrayList<Achievement> getArrayList() {
            return arrayList;
        }

        public long getValueOutput() {
            return valueOutput;
        }
    }

    /***
     * This is used to retrieve the achievement category, together with the file name
     * @param type the type of the achievement (1,2,3)
     * @param context Current context
     * @return
     */
    public achievementView getAchievementListofPartiularType(int type, Context context) {

        //This is to store
        ArrayList<Achievement> newReturnAchievements = new ArrayList<>();
        long viewValue = 0;
        int badges = 0;

        //case 1 is used to check for the hours achievement type
        //case 2 is used to check for the total cycle type
        //case 3 is used to check for the consecutive successful cycle

        //Each case have different condition check respectively
        //Works the same, Get the image path of the file
        //Get the requirement, if not met it will be set as null
        //Null is used to define that the user have not achieved the badge
        switch (type) {
            case 1: {
                //Used to check the hours
                HashMap<Integer, ArrayList<Achievement>> hashMapAchievements = getAchievementArrayList();
                ArrayList<Achievement> AchievementsDL = hashMapAchievements.get(type);
                viewValue = getAllHours();

                assert AchievementsDL != null;

                for (Achievement achievement : AchievementsDL) {
                    if (viewValue > (achievement.requirement * 3600000)) {
                        String path = context.getFilesDir() + "/" + "1" + "/" + achievement.getFilename();
                        Log.v(TAG, "Getting image from path : " + path);

                        File file = new File(path);
                        achievement.setPathImg(file);
                        newReturnAchievements.add(achievement);
                        badges = badges + 1;
                    } else {
                        newReturnAchievements.add(null);
                    }
                }
                viewValue = (long) Math.floor(viewValue / 3600000);

                break;
            }
            case 2: {
                //check the cycle

                HashMap<Integer, ArrayList<Achievement>> hashMapAchievements = getAchievementArrayList();
                ArrayList<Achievement> AchievementsDL = hashMapAchievements.get(type);
                int totalSuccessfulCycle = getmSuccessFocusList().size();

                assert AchievementsDL != null;
                for (Achievement achievement : AchievementsDL) {
                    if (totalSuccessfulCycle > (achievement.requirement)) {
                        String path = context.getFilesDir() + "/" + "2" + "/" + achievement.getFilename();
                        Log.v(TAG, "Getting image from path : " + path);

                        File file = new File(path);
                        achievement.setPathImg(file);
                        newReturnAchievements.add(achievement);
                        badges = badges + 1;

                    } else {
                        newReturnAchievements.add(null);
                    }
                }

                viewValue = totalSuccessfulCycle;
                break;
            }
            case 3: {

                //Check the highest recurrence
                HashMap<Integer, ArrayList<Achievement>> hashMapAchievements = getAchievementArrayList();
                ArrayList<Achievement> AchievementsDL = hashMapAchievements.get(type);
                int totalSuccessfulCycle = getHighestRecurrence();

                assert AchievementsDL != null;
                for (Achievement achievement : AchievementsDL) {
                    if (totalSuccessfulCycle > (achievement.requirement)) {
                        String path = context.getFilesDir() + "/" + "3" + "/" + achievement.getFilename();
                        Log.v(TAG, "Getting image from path : " + path);

                        File file = new File(path);
                        achievement.setPathImg(file);
                        badges = badges + 1;
                        newReturnAchievements.add(achievement);
                    } else {
                        newReturnAchievements.add(null);
                    }
                }
                viewValue = totalSuccessfulCycle;
                break;
            }
        }

        return new achievementView(newReturnAchievements, viewValue, badges);
    }

    /***
     * Setting the achievement list
     * @param achievementArrayList
     */
    public void setAchievementList(HashMap<Integer, ArrayList<Achievement>> achievementArrayList) {
        this.achievementArrayList = achievementArrayList;
    }

    /**
     * Method to set all FocusList for current User
     *
     * @return ArrayList of focus object
     */
    public void setmFocusList(ArrayList<Focus> mFocusList) throws ParseException {
        dateArrayList = new ArrayList<>();
        for (Focus focus : mFocusList
        ) {
            dateArrayList.add(focus.getdDate());
        }
        this.mFocusList = mFocusList;
    }

    /**
     * Method to set all FocusList for current User
     *
     * @return ArrayList of archive focus object
     */
    public void setaFocusList(ArrayList<Focus> mFocusList) throws ParseException {
        this.aFocusList = mFocusList;
    }

    /**
     * Method to add focus object to Focus List
     *
     * @param focus Set object focus to the focus
     */
    public void addFocusList(Focus focus) {
        this.mFocusList.add(focus);
    }

    public void renewFocusList() {
        try {

            setmFocusList(focusDBHelper.getAllMainData());
            setaFocusList(focusDBHelper.getAllArchiveData());
            Log.v(TAG, "List updated to " + mFocusList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to add focus object to Focus List
     *
     * @param focus Set object focus to the focus
     */
    public void addaFocusList(Focus focus) {
        this.aFocusList.add(focus);
    }

    /**
     * Method to add focus object to Focus List
     *
     * @param achievement Set object achievement to the focus
     */
    public void addAchievementList(Achievement achievement) {
        if (!achievementArrayList.containsKey(achievement.typeAchievement)) {
            this.achievementArrayList.put(achievement.typeAchievement, new ArrayList<>());
        }

        achievementArrayList.get(achievement.typeAchievement).add(achievement);
    }

    /**
     * Method to add focus object to Focus List
     *
     */

    public void renewAchievementList() {
        achievementArrayList = new HashMap<>();

        for (Achievement a : achievementDBHelper.getAchievementData()) {
            addAchievementList(a);
            Log.v(TAG, "add Achievement list(): " + a);
        }
    }

    /**
     * Method to clear Focus List
     */
    public void clearFocusList() throws ParseException {
        setmFocusList(new ArrayList<Focus>());
    }

    /**
     * Method to clear Focus List
     */
    public void clearaFocusList() throws ParseException {
        setaFocusList(new ArrayList<Focus>());
    }

    /**
     * Read the firebase of focus datas
     *
     * @param context set context to the current content
     */
    public void readFocusFirebase(Context context, FocusDBObserver focusDBObserver) {

        myRef = FirebaseDatabase.getInstance().getReference().child("archiveFocusData").child(getUID());
        focusDBHelper = new FocusDBHelper(context);
        focusDBHelper.registerDbObserver(focusDBObserver);
        //Clear all data since there is a change to the database so it can be updated
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String uid = dataSnapshot.child("fbID").getValue(String.class);
                Focus focus = getFocus(dataSnapshot);
                if (!focusDBHelper.rowAexist(uid)) {
                    Log.d(TAG, "Adding Archive data UID: " + uid);
                    focusDBHelper.addArchiveData(focus);
                    renewFocusList();

                } else {
                    if (!focusDBHelper.getOneArchiveFocusData(uid).equals(focus)) {
                        Log.d(TAG, "Updating Archive data UID: " + uid);
                        focusDBHelper.updateAfocus(focus);
                        renewFocusList();

                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String uid = dataSnapshot.child("fbID").getValue(String.class);
                Log.d(TAG, "Changed archive for UID: " + uid);
                Focus focus = getFocus(dataSnapshot);
                if (!focusDBHelper.rowAexist(uid)) {
                    focusDBHelper.addArchiveData(focus);
                    renewFocusList();

                } else {
                    if (!focusDBHelper.getOneArchiveFocusData(uid).equals(focus)) {
                        focusDBHelper.updateAfocus(focus);
                        renewFocusList();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String uid = dataSnapshot.child("fbID").getValue(String.class);
                Log.d(TAG, "Removing for archive UID: " + uid);
                Focus focus = getFocus(dataSnapshot);
                if (focusDBHelper.rowAexist(uid)) {
                    focusDBHelper.removeOneAData(focus);
                    renewFocusList();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read value.", databaseError.toException());

            }
        });

        myRef = FirebaseDatabase.getInstance().getReference().child("focusData").child(getUID());

        //Clear all data since there is a change to the database so it can be updated
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String uid = dataSnapshot.child("fbID").getValue(String.class);
                Focus focus = getFocus(dataSnapshot);
                if (!focusDBHelper.rowExist(uid)) {
                    Log.d(TAG, "Adding data for : " + focus);

                    focusDBHelper.addData(focus);
                    renewFocusList();
                } else {
                    if (!focusDBHelper.getOneFocusData(uid).equals(focus)) {
                        focusDBHelper.update(focus);
                        renewFocusList();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String uid = dataSnapshot.child("fbID").getValue(String.class);
                Focus focus = getFocus(dataSnapshot);
                if (!focusDBHelper.rowExist(uid)) {
                    Log.d(TAG, "Adding for UID that change: " + focus);

                    focusDBHelper.addData(focus);
                    renewFocusList();

                } else {
                    if (!focusDBHelper.getOneFocusData(uid).equals(focus)) {
                        focusDBHelper.update(focus);
                        renewFocusList();

                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Focus focus = getFocus(dataSnapshot);
                Log.d(TAG, "Checking for UID to remove: " + focus.getFbID());

                if (focusDBHelper.rowExist(focus.getFbID())) {
                    Log.d(TAG, "Row exist: " + focus.getFbID());

                    focusDBHelper.removeOneData(focus);
                    renewFocusList();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read value.", databaseError.toException());

            }
        });
    }

    /***
     * Custom method to get the focus snapshot
     * @param dataSnapshot
     * @return
     */
    private Focus getFocus(@NonNull DataSnapshot dataSnapshot) {
        Focus focus = new Focus();
        focus.setFbID((String) dataSnapshot.child("fbID").getValue());
        focus.setmCompletion((String) dataSnapshot.child("mCompletion").getValue());
        focus.setmDateTime((String) dataSnapshot.child("mDateTime").getValue());
        focus.setmDuration((String) dataSnapshot.child("mDuration").getValue());
        focus.setmTask((String) dataSnapshot.child("mTask").getValue());
        focus.setmTimeTaken((long) dataSnapshot.child("mTimeTaken").getValue());
        return focus;
    }


    /***
     * Achievement Database
     */
    public void getAchievement(Activity activity) {

        // Check if we have write permission
        if (getPermission(activity)) {
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();



            OneTimeWorkRequest getSectionTask = new OneTimeWorkRequest.
                    Builder(GetAchievementWorker.class)
                    .setConstraints(constraints)
                    .build();

            WorkManager.getInstance().enqueue(getSectionTask);
        }
    }

    /**
     * Getting the permission from the user
     * @param activity
     * @return
     */
    public boolean getPermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * This method is to show a dialog on the permission reason of requesting internal storage access
     * @param activity
     */
    public void showPermissionDescription(Activity activity){

        TextView close;

        builder = new AlertDialog.Builder(activity, R.style.BadgeDialog);

        LayoutInflater inflater = LayoutInflater.from(activity);
        View shareCustomLayout = inflater.inflate(R.layout.popup_permission, null);

        close = shareCustomLayout.findViewById(R.id.closePermissionDialog);

        close.setOnClickListener(view -> {
            dialog.cancel();
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        });

        builder.setView(shareCustomLayout);

        // create and show the alert dialog
        dialog = builder.create();

        dialog.show();

    }


    public Habit.HabitList getHabitList() {
        return habitList;
    }

    public void setHabitList(Habit.HabitList habitList) {
        this.habitList = habitList;
    }

    public void clearHabitList() {
        setHabitList(new Habit.HabitList());
    }

    public ArrayList<HabitGroup> getHabitGroupsList() {
        return habitGroupsList;
    }

    public void setHabitGroupsList(ArrayList<HabitGroup> habitGroupsList) {
        this.habitGroupsList = habitGroupsList;
    }

    public void clearHabitGroupsList() {
        setHabitGroupsList(new ArrayList<HabitGroup>());
    }

    public Habit getHabit(@NonNull DataSnapshot singleSnapshot){
        Habit habit = new Habit();
        long habitID = singleSnapshot.child("habitID").getValue(Long.class);
        habit.setHabitID(habitID);
        habit.setTitle((String) singleSnapshot.child("title").getValue());
        habit.setOccurrence(singleSnapshot.child("occurrence").getValue(Integer.class));
        habit.setPeriod(singleSnapshot.child("period").getValue(Integer.class));
        habit.setHolder_color((String) singleSnapshot.child("holder_color").getValue());
        habit.setTime_created((String) singleSnapshot.child("time_created").getValue());

        HabitGroup habitGroup = new HabitGroup();
        if (singleSnapshot.hasChild("group")) {
            habitGroup.setGrp_id(singleSnapshot.child("group").child("grp_id").getValue(Long.class));
            habitGroup.setGrp_name((String) singleSnapshot.child("group").child("grp_name").getValue());
            habit.setGroup(habitGroup);
        } else {
            habit.setGroup(null);
        }

        HabitReminder habitReminder = new HabitReminder();
        if (singleSnapshot.hasChild("habitReminder")) {
            habitReminder.setId(singleSnapshot.child("habitReminder").child("id").getValue(Integer.class));
            habitReminder.setMessage((String) singleSnapshot.child("habitReminder").child("message").getValue());
            habitReminder.setCustom_text((String) singleSnapshot.child("habitReminder").child("custom_text").getValue());
            habitReminder.setHours(singleSnapshot.child("habitReminder").child("hours").getValue(Integer.class));
            habitReminder.setMinutes(singleSnapshot.child("habitReminder").child("minutes").getValue(Integer.class));
            habit.setHabitReminder(habitReminder);
        } else {
            habit.setHabitReminder(null);
        }

        return habit;
    }

    public HabitRepetition getHabitRepetition(@NonNull DataSnapshot singleSnapshot){
        HabitRepetition hr = new HabitRepetition();
        hr.setHabitID(singleSnapshot.child("habitID").getValue(Long.class));
        hr.setRow_id(singleSnapshot.child("row_id").getValue(Long.class));
        hr.setTimestamp(singleSnapshot.child("timestamp").getValue(Long.class));
        hr.setCycle(singleSnapshot.child("cycle").getValue(Integer.class));
        hr.setCycle_day(singleSnapshot.child("cycle_day").getValue(Integer.class));
        hr.setCount(singleSnapshot.child("count").getValue(Integer.class));
        hr.setConCount(singleSnapshot.child("conCount").getValue(Integer.class));

        return hr;
    }

    public HabitGroup getHabitGroup(@NonNull DataSnapshot singleSnapshot){
        HabitGroup habitGroup = new HabitGroup();
        habitGroup.setGrp_id(singleSnapshot.child("grp_id").getValue(Long.class));
        habitGroup.setGrp_name((String) singleSnapshot.child("grp_name").getValue());
        return habitGroup;
    }

    public void renewHabitList() {
        setHabitList(habitDBHelper.getAllHabits());
        Log.v(TAG, "Habit List updated to " + habitList);
    }

    /**
     * This method is read the habits from firebase
     *
     * @param context This is to get the context of the activity
     */
    public void readHabit_Firebase(Context context, HabitDBObserver habitDBObserver) {

        habitDBHelper = new HabitDBHelper(context);
        habitDBHelper.registerDbObserver(habitDBObserver);
        habitRepetitionDBHelper = new HabitRepetitionDBHelper(context);
        habitRepetitionDBHelper.registerDbObserver(habitDBObserver);
        hRef = FirebaseDatabase.getInstance().getReference().child("users").child(getUID()).child("habit");
        habitListener = hRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded: habit");
                long habitID = dataSnapshot.child("habitID").getValue(Long.class);
                Habit habit = getHabit(dataSnapshot);

                if (!habitDBHelper.isHabitIDExisted(habitID)){
                    Log.d(TAG, "onChildAdded: addHabit");
                    habitDBHelper.insertHabitFromFirebase(habit, getUID());
                    renewHabitList();
                }else{
                    if (!habitDBHelper.getHabitByID(habitID).equals(habit)){
                        Log.d(TAG, "onChildAdded: updateHabit");
                        habitDBHelper.updateHabitFromFirebase(habit);
                        renewHabitList();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged: habit");
                long habitID = dataSnapshot.child("habitID").getValue(Long.class);
                Habit habit = getHabit(dataSnapshot);
                if (!habitDBHelper.isHabitIDExisted(habitID)){
                    Log.d(TAG, "onChildChanged: addHabit");
                    habitDBHelper.insertHabitFromFirebase(habit, getUID());
                    renewHabitList();
                }else{
                    if (!habitDBHelper.getHabitByID(habitID).equals(habit)){
                        Log.d(TAG, "onChildChanged: updateHabit");
                        habitDBHelper.updateHabitFromFirebase(habit);
                        renewHabitList();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved: habit");
                Habit habit = getHabit(dataSnapshot);

                if (habitDBHelper.isHabitIDExisted(habit.getHabitID())){
                    Log.d(TAG, "onChildRemoved: remove habit");
                    habitDBHelper.deleteHabitFromFirebase(habit);
                    renewHabitList();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read value.", databaseError.toException());
            }
        });


        hRef = FirebaseDatabase.getInstance().getReference().child("users").child(getUID()).child("habitRepetition");
        hrListener = hRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded: habitRepetition");
                long id = dataSnapshot.child("row_id").getValue(Long.class);
                HabitRepetition hr = getHabitRepetition(dataSnapshot);

                if (!habitRepetitionDBHelper.isHabitRepetitionExisted(id)){
                    Log.d(TAG, "onChildAdded: add habitRepetition");

                    habitRepetitionDBHelper.insertHabitRepetitionFromFirebase(hr, getUID());
                }else{
                    if (!habitRepetitionDBHelper.getHabitRepetitionByRowID(id).equals(hr)){
                        Log.d(TAG, "onChildAdded: updateHabitRepetition");
                        habitRepetitionDBHelper.update(hr);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged: habitRepetition");
                long id = dataSnapshot.child("row_id").getValue(Long.class);
                HabitRepetition hr = getHabitRepetition(dataSnapshot);

                if (!habitRepetitionDBHelper.isHabitRepetitionExisted(id)){
                    Log.d(TAG, "onChildChanged: add habitRepetition");
                    habitRepetitionDBHelper.insertHabitRepetitionFromFirebase(hr, getUID());
                }else{
                    if (!habitRepetitionDBHelper.getHabitRepetitionByRowID(id).equals(hr)){
                        Log.d(TAG, "onChildChanged: updateHabitRepetition");
                        habitRepetitionDBHelper.update(hr);
                    }
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved: habitRepetition");
                HabitRepetition hr = getHabitRepetition(dataSnapshot);

                if (habitRepetitionDBHelper.isHabitRepetitionExisted(hr.getRow_id())){
                    Log.d(TAG, "onChildRemoved: remove habitRepetition");
                    habitRepetitionDBHelper.removeOneData(hr);
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to read value.", databaseError.toException());
            }
        });

        habitGroupDBHelper = new HabitGroupDBHelper(context);
        hRef = FirebaseDatabase.getInstance().getReference().child("users").child(getUID()).child("habitGroup");
        hgListener = hRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded: habitGroup");
                long id = dataSnapshot.child("grp_id").getValue(Long.class);
                HabitGroup hg = getHabitGroup(dataSnapshot);

                if (!habitGroupDBHelper.isHabitGroupExisted(id)){
                    Log.d(TAG, "onChildAdded: add habitGroup");
                    habitGroupDBHelper.insertGroupFromFirebase(hg);
                }else{
                    if (!habitGroupDBHelper.getHabitGroupByRowID(id).equals(hg)){
                        Log.d(TAG, "onChildAdded: updateHabitGroup");
                        habitGroupDBHelper.update(hg);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildChanged: habitGroup");
                long id = dataSnapshot.child("grp_id").getValue(Long.class);
                HabitGroup hg = getHabitGroup(dataSnapshot);

                if (!habitGroupDBHelper.isHabitGroupExisted(id)){
                    Log.d(TAG, "onChildChanged: add habitGroup");
                    habitGroupDBHelper.insertGroupFromFirebase(hg);
                }else{
                    if (!habitGroupDBHelper.getHabitGroupByRowID(id).equals(hg)){
                        Log.d(TAG, "onChildChanged: updateHabitGroup");
                        habitGroupDBHelper.update(hg);
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                HabitGroup hg = getHabitGroup(dataSnapshot);
                
                if (habitGroupDBHelper.isHabitGroupExisted(hg.getGrp_id())){
                    Log.d(TAG, "onChildRemoved: deleteHabitGroup");
                    habitGroupDBHelper.removeOneData(hg);
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

    public void getAllSectionAndTask() {
        //Setting condition
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();


        //Create the request
        OneTimeWorkRequest getSectionTask = new OneTimeWorkRequest.
                Builder(GetTaskSectionWorker.class)
                .setConstraints(constraints)
                .build();

        //Enqueue the request
        WorkManager.getInstance().enqueue(getSectionTask);
    }

    public User() {
    }


    /**
     * This will set the name and the password
     * to the object
     *
     * @param name     The name of the user
     * @param password The password of the user
     */
    User(String name, String password) {
        this.mName = name;
        this.mPassword = password;
    }

    /**
     * This will set the UID, name, password,
     * dob and email into the object
     *
     * @param UID      The unique ID of the user
     * @param name     The name of the user
     * @param password The password of the user
     * @param Email    The email of the user
     */
    public User(String UID, String name, String password, String Email) {
        this.mUID = UID;
        this.mName = name;
        this.mPassword = password;
        this.mEmailAddr = Email;
    }

    /**
     * You can set the name, password and the email of the
     * user in to the object
     *
     * @param name      The name of the user
     * @param password  The password of the user
     * @param emailAddr The email of the user
     */
    public User(String name, String password, String emailAddr) {
        this.mName = name;
        this.mPassword = password;
        this.mEmailAddr = emailAddr;
    }

    /**
     * This methods is to check if the name is empty and
     * set it into the object
     *
     * @param name This parameter take in the name of the user
     *             and set it
     * @throws FormatException On input given if it empty of not
     */
    public void setName(String name) throws FormatException {

        if (!name.isEmpty()) {
            mName = name;
        } else
            throw new FormatException("Name is empty");
    }

    /**
     * This method is to check if the email is empty and if it's in the email format and
     * set it into the object
     *
     * @param emailAdd This parameter take in the email address of the user
     *                 and set it
     * @throws FormatException On input given must follow the email format (@ and .com)
     *                         and not empty
     */
    public void setEmailAdd(String emailAdd) throws FormatException {

        if (!emailAdd.isEmpty()) {
            mEmailAddr = emailAdd;

        } else {
            throw new FormatException("Email is empty");
        }
    }

    /**
     * This method is to check if the password is empty and if it's in the strong password format and
     * set it into the object
     *
     * @param password This parameter take in the password of the user
     *                 and set it
     * @throws FormatException On input given must follow the strong password format have 8 characters,
     *                         with special symbol and alphanumeric, not empty and no white space
     */
    public void setPassword(String password) throws FormatException {

        if (!password.isEmpty()) {
            mPassword = password;

        } else {
            //Error message for empty text
            throw new FormatException("Password is empty");
        }

    }

    /**
     * This method is used to set the
     * user unique ID into the object
     *
     * @param UID This parameter is used to set the UID
     *            of the user
     */
    public void setUID(String UID) {
        mUID = UID;
    }

    /**
     * This method is used to set the
     * user authentication into the object
     *
     * @param auth This parameter is used to set the authentication
     *             info of the user from firebase
     */
    public void setAuth(FirebaseUser auth) {
        mAuth = auth;

        //Set the UID for this user
        this.setUID(this.mAuth.getUid());
    }


    /**
     * @return String This return the UID of the user
     */
    public String getUID() {
        return mUID;
    }

    /**
     * @return FirebaseUser This return the authentication for this user by firebase
     */
    public FirebaseUser getAuth() {
        return mAuth;
    }

    /**
     * @return String This return the name of the user
     */
    public String getName() {
        return mName;
    }

    /**
     * @return String This return the email address the user
     */
    public String getEmailAdd() {
        return mEmailAddr;
    }

    /**
     * @return String This return the password of the user
     */
    public String getPassword() {
        return mPassword;
    }


    /**
     * Upload Name, Email and UID up the firebase
     * After registering successfully
     * <p>
     * It will be done in the background
     */
    public void executeFirebaseUserUpload() {

        Log.d(TAG, "executeFirebaseUserUpload(): Preparing the upload ");

        //Setting condition
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //Adding data which will be received from the worker
        @SuppressLint("RestrictedApi") Data firebaseUserData = new Data.Builder()
                .putString("ID", getUID())
                .putString("Name", getName())
                .putString("Email", getEmailAdd())
                .build();

        //Create the request
        OneTimeWorkRequest uploadTask = new OneTimeWorkRequest.
                Builder(UploadDataWorker.class)
                .setConstraints(constraints)
                .setInputData(firebaseUserData)
                .build();

        //Enqueue the request
        WorkManager.getInstance().enqueue(uploadTask);
        Log.d(TAG, "executeFirebaseUserUpload(): Put in queue");

    }


    /**
     * Describe the kinds of special objects contained in this
     * Parcelable instance's marshaled representation.
     *
     * @return This return 0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write to parcel
     *
     * @param dest  set dest to current content
     * @param flags set flags to current content
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mAuth, flags);
        dest.writeString(mName);
        dest.writeString(mUID);
        dest.writeString(mEmailAddr);
        dest.writeString(mPassword);
        dest.writeString(mPPID);
        dest.writeTypedList(mFocusList);
    }
}
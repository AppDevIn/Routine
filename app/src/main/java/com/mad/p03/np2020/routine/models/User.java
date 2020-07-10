package com.mad.p03.np2020.routine.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.nfc.FormatException;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.p03.np2020.routine.background.GetTaskSectionWorker;
import com.mad.p03.np2020.routine.Register.models.UploadDataWorker;
import com.mad.p03.np2020.routine.DAL.FocusDBHelper;
import com.mad.p03.np2020.routine.DAL.HabitDBHelper;
import com.mad.p03.np2020.routine.DAL.HabitGroupDBHelper;

import java.util.ArrayList;
import java.util.List;


/**
 * Model used to manage the user data
 *
 * @author Lee Quan Sheng and Jeyavishnu
 * @since 04-06-2020
 */
public class User implements Parcelable {

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
    private DatabaseReference myRef;
    private FocusDBHelper focusDBHelper;
    private HabitDBHelper habitDBHelper;
    private HabitGroupDBHelper habitGroupDBHelper;

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

    public ArrayList<Focus> getmSuccessFocusList() {
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
     * Method to set all FocusList for current User
     *
     * @return ArrayList of focus object
     */
    public void setmFocusList(ArrayList<Focus> mFocusList) {
        this.mFocusList = mFocusList;
    }

    /**
     * Method to add focus object to Focus List
     *
     * @param focus Set object focus to the focus
     */
    public void addFocusList(Focus focus) {
        this.mFocusList.add(focus);
    }

    /**
     * Method to clear Focus List
     */
    public void clearFocusList() {
        setmFocusList(new ArrayList<Focus>());
    }

    /**
     * Read the firebase of User
     *
     * @param context set context to the current content
     */
    public void readFocusFirebase(Context context) {
        myRef = FirebaseDatabase.getInstance().getReference().child("focusData").child(getUID());
        focusDBHelper = new FocusDBHelper(context);

        //Clear all data since there is a change to the database so it can be updated

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                focusDBHelper.deleteAll();
                clearFocusList();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Focus focus = new Focus();
                    focus.setFbID((String) singleSnapshot.child("fbID").getValue());
                    focus.setmCompletion((String) singleSnapshot.child("mCompletion").getValue());
                    focus.setmDateTime((String) singleSnapshot.child("mDateTime").getValue());
                    focus.setmDuration((String) singleSnapshot.child("mDuration").getValue());
                    focus.setmTask((String) singleSnapshot.child("mTask").getValue());
                    addFocusList(focus);
                    focusDBHelper.addData(focus);
                }
                setmFocusList(focusDBHelper.getAllData());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    /**
     * This method is read the habits from firebase
     *
     * @param context This is to get the context of the activity
     */
    public void readHabit_Firebase(Context context) {
        Log.d(TAG, "read Habit_Firebase: ");

        habitDBHelper = new HabitDBHelper(context);

        // delete all the habit
        habitDBHelper.deleteAllHabit();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").child(getUID());
        myRef.child("habit").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // to retrieve the data from each snapshot and insert them into SQLiteDatabase
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Habit habit = new Habit();
                    habit.setHabitID((Long) singleSnapshot.child("habitID").getValue(Long.class));
                    habit.setTitle((String) singleSnapshot.child("title").getValue());
                    habit.setCount((Integer) singleSnapshot.child("count").getValue(Integer.class));
                    habit.setOccurrence((Integer) singleSnapshot.child("occurrence").getValue(Integer.class));
                    habit.setPeriod((Integer) singleSnapshot.child("period").getValue(Integer.class));
                    habit.setHolder_color((String) singleSnapshot.child("holder_color").getValue());
                    habit.setTime_created((String) singleSnapshot.child("time_created").getValue());

                    HabitGroup habitGroup = new HabitGroup();
                    if (singleSnapshot.hasChild("group")) {
                        habitGroup.setGrp_id((Long) singleSnapshot.child("group").child("grp_id").getValue(Long.class));
                        habitGroup.setGrp_name((String) singleSnapshot.child("group").child("grp_name").getValue());
                        habit.setGroup(habitGroup);
                    } else {
                        habit.setGroup(null);
                    }

                    HabitReminder habitReminder = new HabitReminder();
                    if (singleSnapshot.hasChild("habitReminder")) {
                        habitReminder.setId((Integer) singleSnapshot.child("habitReminder").child("id").getValue(Integer.class));
                        habitReminder.setMessage((String) singleSnapshot.child("habitReminder").child("message").getValue());
                        habitReminder.setCustom_text((String) singleSnapshot.child("habitReminder").child("custom_text").getValue());
                        habitReminder.setHours((Integer) singleSnapshot.child("habitReminder").child("hours").getValue(Integer.class));
                        habitReminder.setMinutes((Integer) singleSnapshot.child("habitReminder").child("minutes").getValue(Integer.class));
                        habit.setHabitReminder(habitReminder);
                    } else {
                        habit.setHabitReminder(null);
                    }


                    habitDBHelper.insertHabitFromFirebase(habit, getUID());
                    Log.d(TAG, "reading Habit Lines");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    /**
     * This method is read the habitGroups from firebase
     *
     * @param context This is to get the context of the activity
     */
    public void readHabitGroup_Firebase(Context context) {
        Log.d(TAG, "read HabitGroup_Firebase: ");

        habitGroupDBHelper = new HabitGroupDBHelper(context);

        // delete all habitGroups
        habitGroupDBHelper.deleteAllHabitGroups();

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("users").child(getUID());
        myRef.child("habitGroup").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // to retrieve the data from each snapshot and insert them into SQLiteDatabase
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    HabitGroup habitGroup = new HabitGroup();
                    habitGroup.setGrp_id((Long) singleSnapshot.child("grp_id").getValue(Long.class));
                    habitGroup.setGrp_name((String) singleSnapshot.child("grp_name").getValue());

                    Log.d(TAG, "onDataChange: " + habitGroup.getGrp_id());
                    Log.d(TAG, "onDataChange: " + habitGroup.getGrp_name());

                    habitGroupDBHelper.insertGroupFromFirebase(habitGroup);

                    Log.d(TAG, "reading HabitGroup Lines");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read value.", error.toException());
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
            throw new FormatException("Text is empty");
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
            //Declare Constants
            String EMAILPATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            if (emailAdd.trim().matches(EMAILPATTERN)) {
                mEmailAddr = emailAdd;

            } else {
                //Error message for email when .com and @ is not present
                throw new FormatException("Text does not have @ and .com");


            }
        } else {
            throw new FormatException("Text is empty");
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
        //TODO: Encrypt the password

        if (!password.isEmpty()) {
            String STRONGPASSWORD = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
            if (password.trim().matches(STRONGPASSWORD)) {
                mPassword = password;
            } else {


                //a digit must occur at least once
                //a lower case letter must occur at least once
                //an upper case letter must occur at least once
                //a special character must occur at least once
                //    no whitespace allowed in the entire string
                //anything, at least eight places though

                //Error message for password when password doesn't
                //have digit, lower and upper case, special character and min 8 letter
                throw new FormatException("Needs to be alphanumeric, special\n character and the length of 8");
            }
        } else {
            //Error message for empty text
            throw new FormatException("Text is empty");
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
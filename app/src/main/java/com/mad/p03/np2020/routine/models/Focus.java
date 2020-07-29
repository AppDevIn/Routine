package com.mad.p03.np2020.routine.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 *
 * Model used to manage the Focus
 *
 * @author Lee Quan Sheng
 * @since 02-06-2020
 */

public class Focus implements Parcelable {

    private String fbID;
    private String mDateTime;
    private String mDuration;
    private String mTask;
    private String mCompletion;
    private long mTimeTaken;


    /**Name for table,  to identify the name of the table*/
    public static final String FOCUS_TABLE = "focus";
    public static final String FOCUS_Archive_TABLE = "focus_archive";


    /**Column task_name for table,  to identify the name of the section*/
    public static final String COLUMN_TASK_NAME = "TASK_NAME";

    /**Column task_date for table,  to identify the name of the section*/
    public static final String COLUMN_TASK_DATE = "TASK_DATE";

    /**Column task_duration for table,  to identify the name of the section*/
    public static final String COLUMN_TASK_DURATION = "TASK_DURATION";

    /**Column task_complete for table,  to identify the name of the section*/
    public static final String COLUMN_TASK_COMPLETE = "TASK_COMPLETE";

    /**Primary key for table,  to identify the row.*/
    public static final String COLUMN_TASK_fbID = "fbID";

    /**Column task_duration for table,  to identify the name of the section*/
    public static final String COLUMN_TASK_TIME_TAKEN = "TASK_TAKEN";


    /**Main Database**/
    public static final String CREATE_SQL = "CREATE TABLE " + FOCUS_TABLE + " (" + COLUMN_TASK_fbID + " TEXT PRIMARY KEY, " + COLUMN_TASK_NAME + " TEXT, " + COLUMN_TASK_DATE + " TEXT, " + COLUMN_TASK_DURATION + " TEXT, " + COLUMN_TASK_COMPLETE + " BOOL, " + COLUMN_TASK_TIME_TAKEN + " BIGINT)";

    /**Archive Database**/
    public static final String CREATE_ARCHIVE_SQL = "CREATE TABLE " + FOCUS_Archive_TABLE + " (" + COLUMN_TASK_fbID + " TEXT PRIMARY KEY, " + COLUMN_TASK_NAME + " TEXT, " + COLUMN_TASK_DATE + " TEXT, " + COLUMN_TASK_DURATION + " TEXT, " + COLUMN_TASK_COMPLETE + " BOOL, " + COLUMN_TASK_TIME_TAKEN + " BIGINT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FOCUS_TABLE;

    public static final String SQL_DELETE_ENTRIES_ARCHIVE =
            "DROP TABLE IF EXISTS " + FOCUS_Archive_TABLE;

    /**
     * Initialize Focus Activity with Focus object
     * when its called
     *
     * @param mDate Date is used to capture the date of activity
     * @param mDuration Duration is used to capture the Duration of activity
     * @param mTask Task is used to capture the task of activity
     * @param mCompletion Completion is used to capture the success of the Focus
     */
    public Focus(String mDate, String mDuration, String mTask, String mCompletion, long mTimeTaken) {
        this.mDateTime = mDate;
        this.mDuration = mDuration;
        this.mTask = mTask;
        this.mCompletion = mCompletion;
        this.mTimeTaken = mTimeTaken;
    }

    /**
     * Initialize Focus Activity with Focus object
     * when its called
     *
     * @param fbID fpID is used for firebase as the primary key
     * @param mDate Date is used to capture the date of activity
     * @param mDuration Duration is used to capture the Duration of activity
     * @param mTask Task is used to capture the task of activity
     * @param mCompletion Completion is used to capture the success of the Focus
     */
    public Focus(String fbID, String mDate, String mDuration, String mTask, String mCompletion, long mTimeTaken) {
        this.fbID = fbID;
        this.mDateTime = mDate;
        this.mDuration = mDuration;
        this.mTask = mTask;
        this.mCompletion = mCompletion;
        this.mTimeTaken = mTimeTaken;
    }

    /**
     * Initialize Focus Activity with Focus object
     * when its called
     *
     * Empty Constructor
     */
    public Focus() {
    }

    /**
     * Initialize Focus Activity with Focus object
     * when its called
     *
     * Parcelable Constructor to allow this custom object to pass on to the class
     */
    protected Focus(Parcel in) {
        mDateTime = in.readString();
        mDuration = in.readString();
        mTask = in.readString();
        mCompletion = in.readString();
        mTimeTaken = in.readLong();
    }

    /**
     *
     * Creator must be implemented for parcelable constructor
     */
    public static final Creator<Focus> CREATOR = new Creator<Focus>() {
        @Override
        public Focus createFromParcel(Parcel in) {
            return new Focus(in);
        }

        @Override
        public Focus[] newArray(int size) {
            return new Focus[size];
        }
    };


    /**
     *
     * Get DateTime of task
     */
    public String getmDateTime() {
        return mDateTime;
    }

    /**
     *
     * Get DateTime of task
     */
    public String getsDate() throws ParseException {
        Date date;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        date = dateFormat.parse(mDateTime);

        String pattern = "dd/MM/yyyy";
        DateFormat df = new SimpleDateFormat(pattern);

        String dateAsString = df.format(date);
        return dateAsString;

    }

    public Date getdDate() throws ParseException {
        Date date;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm");
        date = dateFormat.parse(mDateTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        date = cal.getTime();
        return date;

    }

    /**
     *
     * Set DateTime of task
     */
    public void setmTimeTaken(long mTimeTaken) {
        this.mTimeTaken = mTimeTaken;
    }

    /**
     *
     * Get Duration of task
     */
    public long getmTimeTaken() {
        return mTimeTaken;
    }

    /**
     *
     * Set DateTime of task
     */
    public void setmDateTime(String mDateTime) {
        this.mDateTime = mDateTime;
    }

    /**
     *
     * Get Duration of task
     */
    public String getmDuration() {
        return mDuration;
    }

    /**
     *
     * Set Duration of task
     */
    public void setmDuration(String mDuration) {
        this.mDuration = mDuration;
    }

    /**
     *
     * Get Name of task
     */
    public String getmTask() {
        return mTask;
    }

    /**
     *
     * Set Name of task
     */
    public void setmTask(String mTask) {
        this.mTask = mTask;
    }

    /**
     *
     * Get Completion of task
     */
    public String getmCompletion() {
        return mCompletion;
    }

    /**
     *
     * Set Completion of task
     */
    public void setmCompletion(String mCompletion) {
        this.mCompletion = mCompletion;
    }

    /**
     *
     * Get Firebase ID
     */
    public String getFbID() {
        return fbID;
    }

    /**
     *
     * Set Firebase ID
     */
    public void setFbID(String fbID) {
        this.fbID = fbID;
    }

    /**
     *
     * Custom toString object
     */
    @NonNull
    @Override
    public String toString() {
        return "Focus{" +
                "mDateTime='" + mDateTime + '\'' +
                ", mDuration='" + mDuration + '\'' +
                ", mTask='" + mTask + '\'' +
                ", mCompletion='" + mCompletion + '\'' +
                '}';
    }

    /**
     *
     *
     * This class have child classes, used of child in this case can return in describeContent() different values,
     * so to know which particular object type to create from Parcel.
     *
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     *
     * Flatten Focus object to a parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDateTime);
        dest.writeString(mDuration);
        dest.writeString(mTask);
        dest.writeString(mCompletion);
    }
}
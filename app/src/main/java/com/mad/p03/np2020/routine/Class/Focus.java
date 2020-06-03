package com.mad.p03.np2020.routine.Class;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 *
 * Model used to manage the section
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

    /**
     * Initialize Focus Activity with Focus object
     * when its called
     *
     * @param mDate Date is used to capture the date of activity
     * @param mDuration Duration is used to capture the Duration of activity
     * @param mTask Task is used to capture the task of activity
     * @param mCompletion Completion is used to capture the success of the Focus
     */
    public Focus(String mDate, String mDuration, String mTask, String mCompletion) {
        this.mDateTime = mDate;
        this.mDuration = mDuration;
        this.mTask = mTask;
        this.mCompletion = mCompletion;
    }

    /**
     * Initialize Focus Activity with Focus object
     * when its called
     *
     * @param fbID fpID is used for firebase as the primary key
     * @param sqlID sqlID is used for SQLite as the primary key
     * @param mDate Date is used to capture the date of activity
     * @param mDuration Duration is used to capture the Duration of activity
     * @param mTask Task is used to capture the task of activity
     * @param mCompletion Completion is used to capture the success of the Focus
     */
    public Focus(String fbID, String mDate, String mDuration, String mTask, String mCompletion) {
        this.fbID = fbID;
        this.mDateTime = mDate;
        this.mDuration = mDuration;
        this.mTask = mTask;
        this.mCompletion = mCompletion;
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
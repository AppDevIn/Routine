package com.mad.p03.np2020.routine.Class;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Focus implements Parcelable {

    private String sqlID;
    private String fbID;
    private String mDateTime;
    private String mDuration;
    private String mTask;
    private String mCompletion;

    public Focus(String mDate, String mDuration, String mTask, String mCompletion) {
        this.mDateTime = mDate;
        this.mDuration = mDuration;
        this.mTask = mTask;
        this.mCompletion = mCompletion;
    }

    public Focus(String sqlID, String mDate, String mDuration, String mTask, String mCompletion) {
        this.sqlID = sqlID;
        this.mDateTime = mDate;
        this.mDuration = mDuration;
        this.mTask = mTask;
        this.mCompletion = mCompletion;
    }

    public Focus(String fbID, String sqlID, String mDate, String mDuration, String mTask, String mCompletion) {
        this.fbID = fbID;
        this.sqlID = sqlID;
        this.mDateTime = mDate;
        this.mDuration = mDuration;
        this.mTask = mTask;
        this.mCompletion = mCompletion;
    }

    public Focus() {
    }

    protected Focus(Parcel in) {
        sqlID = in.readString();
        mDateTime = in.readString();
        mDuration = in.readString();
        mTask = in.readString();
        mCompletion = in.readString();
    }

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

    public String getmDateTime() {
        return mDateTime;
    }

    public void setmDateTime(String mDateTime) {
        this.mDateTime = mDateTime;
    }

    public String getmDuration() {
        return mDuration;
    }

    public void setmDuration(String mDuration) {
        this.mDuration = mDuration;
    }

    public String getmTask() {
        return mTask;
    }

    public void setmTask(String mTask) {
        this.mTask = mTask;
    }

    public String getmCompletion() {
        return mCompletion;
    }

    public void setmCompletion(String mCompletion) {
        this.mCompletion = mCompletion;
    }

    public String getSqlID() {
        return sqlID;
    }

    public void setSqlID(String sqlID) {
        this.sqlID = sqlID;
    }

    public String getFbID() {
        return fbID;
    }

    public void setFbID(String fbID) {
        this.fbID = fbID;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sqlID);
        dest.writeString(mDateTime);
        dest.writeString(mDuration);
        dest.writeString(mTask);
        dest.writeString(mCompletion);
    }
}
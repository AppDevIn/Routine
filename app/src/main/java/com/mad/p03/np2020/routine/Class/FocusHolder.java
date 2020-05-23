package com.mad.p03.np2020.routine.Class;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Focus implements Parcelable {
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

    public Focus() {
    }

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

    }
}
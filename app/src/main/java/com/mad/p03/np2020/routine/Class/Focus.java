package com.mad.p03.np2020.routine.Class;

public class Focus {
    private String mDateTime;
    private String mDuration;
    private String mTask;
    private boolean mCompletion;

    public Focus(String mDate, String mDuration, String mTask, boolean mCompletion) {
        this.mDateTime = mDate;
        this.mDuration = mDuration;
        this.mTask = mTask;
        this.mCompletion = mCompletion;
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

    public boolean ismCompletion() {
        return mCompletion;
    }

    public void setmCompletion(boolean mCompletion) {
        this.mCompletion = mCompletion;
    }

    public Focus() {
    }

    public void TimeDone(){
        //TODO: Please commit any chnages to the class to main branch 
    }

    public void exitedNotification(){
        //TODO: Please commit any chnages to the class to main branch 
    }
    

}
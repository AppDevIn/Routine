package com.mad.p03.np2020.routine.Class;

import java.util.List;

public class Habit {
    private String title;
    private int occurrence;
    private int count;
    private List<Integer> mCountList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Habit(String title, int occurrence, int count) {
        this.title = title;
        this.occurrence = occurrence;
        this.count = count;
    }

    public Habit(){}

    public void addCount(){
        this.setCount(this.getCount()+1);
    }

    public void modifyCount(int count){
        // TODO: Please upload any chnages to this class to the main branch`
    }

    public void calculateProgress(String Occurrence, int count){
        // TODO: Please upload any chnages to this class to the main branch`
    }

    public void displayProgress(String Occurrence, int count){
        // TODO: Please upload any chnages to this class to the main branch`
    }

    public void changeDescNoun(String descNoun){
        // TODO: Please upload any chnages to this class to the main branch`
    }
}

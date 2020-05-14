package com.mad.p03.np2020.routine.Class;

import java.util.ArrayList;
import java.util.List;

public class Habit {
    private String title;
    private int occurrence;
    private int count;
    private int period;
    private List<Integer> mCountList;

    public String getTitle() {
        return title.toUpperCase().trim();
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

    public Habit(String title, int occurrence, int count, int period) {
        this.title = title;
        this.occurrence = occurrence;
        this.count = count;
        this.period = period;
    }

    public void addCount(){
        this.setCount(this.getCount() + 1);
    }

    public void minusCount() {
        if (this.getCount() > 0){
            this.setCount(this.getCount() - 1);
        }
    }

    public void modifyTitle(String title){
        this.title = title.toUpperCase().trim();
    }
    public void modifyCount(int count){
        this.count = count;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
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

    public String returnPeriodText(int period){
        switch(period){
            case 1:
                return "TODAY:";

            case 7:
                return "THIS WEEK:";

            case 30:
                return "THIS MONTH:";

            case 365:
                return "THIS YEAR:";
        }
        return "nothing";
    }

    public static class HabitList {

        private ArrayList<Habit> habitList;

        public HabitList() { this.habitList = new ArrayList<>(); }

        public Habit getItemAt(Integer index) { return this.habitList.get(index); }

        public void addItem(String title, int occurrence, int count, int period) { this.habitList.add(new Habit(title, occurrence, count, period)); }

        public void removeItemAt(int position) { this.habitList.remove(position); }

        public Integer size() { return this.habitList.size(); }
    }
}

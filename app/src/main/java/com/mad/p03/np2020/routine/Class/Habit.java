package com.mad.p03.np2020.routine.Class;

import com.mad.p03.np2020.routine.R;

import java.util.ArrayList;
import java.util.List;

public class Habit {
    private String title;
    private int occurrence;
    private int count;
    private int period;
    private String time_created;
    private String holder_color;
    private HabitReminder habitReminder;
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


    public Habit(String title, int occurrence, int count, int period, String time_created, String holder_color, HabitReminder habitReminder) {
        this.title = title;
        this.occurrence = occurrence;
        this.count = count;
        this.period = period;
        this.time_created = time_created;
        this.holder_color = holder_color;
        this.habitReminder = habitReminder;
    }

    public void addCount(){
        this.setCount(this.getCount() + 1);
    }

    public void minusCount() {
        if (this.getCount() > 0){
            this.setCount(this.getCount() - 1);
        }
    }

    public HabitReminder getHabitReminder() {
        return habitReminder;
    }

    public void setHabitReminder(HabitReminder habitReminder) {
        this.habitReminder = habitReminder;
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

    public String getTime_created() {
        return time_created;
    }

    public void setTime_created(String time_created) {
        this.time_created = time_created;
    }

    public String getHolder_color() {
        return holder_color;
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

    public void setHolder_color(String holder_color) {
        this.holder_color = holder_color;
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

    public int returnColorID(String color) {
        switch (color) {
            case ("cyangreen"):
                return R.color.colorCyanGreen;

            case ("lightcoral"):
                return R.color.colorLightCoral;

            case ("fadepurple"):
                return R.color.colorFadePurple;

            case ("slightdesblue"):
                return R.color.colorSlightDesBlue;
        }
        return 0;
    }

    public static class HabitList {

        private ArrayList<Habit> habitList;

        public HabitList() { this.habitList = new ArrayList<>(); }

        public Habit getItemAt(Integer index) { return this.habitList.get(index); }

        public void addItem(String title, int occurrence, int count, int period, String time_created, String holder_color, HabitReminder reminder) { this.habitList.add(new Habit(title, occurrence, count, period, time_created, holder_color,reminder)); }

        public void removeItemAt(int position) { this.habitList.remove(position); }

        public Integer size() { return this.habitList.size(); }
    }
}

package com.mad.p03.np2020.routine.Habit.models;

import java.util.ArrayList;

/**
 *
 * Model used to manage the habitRepetition
 *
 * @author Hou Man
 * @since 20-07-2020
 */

public class HabitRepetition {

    /**Used as the name of the table*/
    public static final String TABLE_NAME = "habitRepetitions";
    /**Column of the habitRepetitions table, Used as the id of each habitRepetition*/
    public static final String COLUMN_ID = "_id";
    /**Column of the habitRepetitions table, Used as the id of the habit*/
    public static final String COLUMN_HABIT_ID ="HabitID";
    /**Column of the habitRepetitions table, Used as the timestamp of the habit*/
    public static final String COLUMN_HABIT_TIMESTAMP = "timestamp";
    /**Column of the habitRepetitions table, Used as the cycle of the habit*/
    public static final String COLUMN_HABIT_CYCLE = "cycle";
    /**Column of the habitRepetitions table, Used as the day of the cycle of the habit*/
    public static final String COLUMN_HABIT_CYCLE_DAY = "day";
    /**Column of the habitRepetitions table, Used as the consecutive count of the habit*/
    public static final String COLUMN_HABIT_CONCOUNT = "conCount";
    /**Column of the habit table, Used as the count of the habit*/
    public static final String COLUMN_HABIT_COUNT = "count";

    /**
     * The query to create habitRepetition table
     */
    public static final String CREATE_HABITS_REPETITION_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_HABIT_ID  + " INTEGER," +
                    COLUMN_HABIT_TIMESTAMP  + " LONG," +
                    COLUMN_HABIT_CYCLE  + " INTEGER," +
                    COLUMN_HABIT_CYCLE_DAY + " INTEGER," +
                    COLUMN_HABIT_COUNT  + " INTEGER," +
                    COLUMN_HABIT_CONCOUNT  + " INTEGER" + ")";

    /**
     * The query to drop habit table
     */
    public static final String DROP_HABITS_Repetition_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private long row_id;
    private long habitID;
    private long timestamp;
    private int cycle;
    private int cycle_day;
    private int count;
    private int conCount;
    private ArrayList<Long> rowList;

    // constructor
    public HabitRepetition() {
    }

    public HabitRepetition(long timestamp, int count) {
        this.timestamp = timestamp;
        this.count = count;
    }

    // getter and setter

    public long getHabitID() {
        return habitID;
    }

    public void setHabitID(long habitID) {
        this.habitID = habitID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public int getCycle_day() {
        return cycle_day;
    }

    public void setCycle_day(int cycle_day) {
        this.cycle_day = cycle_day;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getConCount() {
        return conCount;
    }

    public void setConCount(int conCount) {
        this.conCount = conCount;
    }

    public long getRow_id() {
        return row_id;
    }

    public void setRow_id(long row_id) {
        this.row_id = row_id;
    }

    public ArrayList<Long> getRowList() {
        return rowList;
    }

    public void setRowList(ArrayList<Long> rowList) {
        this.rowList = rowList;
    }
}

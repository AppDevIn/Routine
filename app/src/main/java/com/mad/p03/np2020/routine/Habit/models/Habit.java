package com.mad.p03.np2020.routine.Habit.models;

import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.models.User;

import java.util.ArrayList;

/**
 *
 * Model used to manage the habit
 *
 * @author Hou Man
 * @since 02-06-2020
 */


public class Habit {

    //Declare the constants of the database

    /**Used as the name of the table*/
    public static final String TABLE_NAME = "habits";

    /**Used as the primary key for this table*/
    public static final String COLUMN_ID = "_id";
    /**Column of the habit table, Used as the title of the habit*/
    public static final String COLUMN_HABIT_TITLE = "title";
    /**Column of the habit table, Used as the occurrence of the habit*/
    public static final String COLUMN_HABIT_OCCURRENCE = "occurrence";
    /**Column of the habit table, Used as the period of the habit*/
    public static final String COLUMN_HABIT_PERIOD = "period";
    /**Column of the habit table, Used as the time created of the habit*/
    public static final String COLUMN_HABIT_TIMECREATED = "timeCreated";
    /**Column of the habit table, Used as the holder color of the habit*/
    public static final String COLUMN_HABIT_HOLDERCOLOR = "holderColor";
    /**Column of the habit table, Used as the primary key of the reminder of the habit*/
    public static final String COLUMN_HABIT_REMINDER_ID = "reminderId";
    /**Column of the habit table, Used as the reminder minutes of the habit*/
    public static final String COLUMN_HABIT_REMINDER_MINUTES = "reminderMinutes";
    /**Column of the habit table, Used as the reminder hours of the habit*/
    public static final String COLUMN_HABIT_REMINDER_HOURS = "reminderHours";
    /**Column of the habit table, Used as the reminder messages of the habit*/
    public static final String COLUMN_HABIT_REMINDER_MESSAGES = "reminderMessages";
    /**Column of the habit table, Used as the reminder custom text of the habit*/
    public static final String COLUMN_HABIT_REMINDER_CUSTOMTEXT = "reminderCustomText";
    /**Column of the habit table, Imported as the foreign key of the group of the habit*/
    public static final String COLUMN_HABIT_GROUP_ID = "groupID";
    /**Column of the habit table, Used as the group name of the habit*/
    public static final String COLUMN_HABIT_GROUP_NAME = "groupName";
    /**Column of the habit table, Imported as the user's foreign key of the habit*/
    public static final String COLUMN_USERID = "userId";

    /**
     * The query to create habit table
     */
    public static final String CREATE_HABITS_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_HABIT_TITLE  + " TEXT," +
                    COLUMN_HABIT_PERIOD  + " INTEGER," +
                    COLUMN_HABIT_OCCURRENCE  + " INTEGER," +
                    COLUMN_HABIT_HOLDERCOLOR  + " TEXT," +
                    COLUMN_HABIT_TIMECREATED + " TEXT," +
                    COLUMN_HABIT_REMINDER_ID + " INTEGER," +
                    COLUMN_HABIT_REMINDER_MINUTES + " INTEGER," +
                    COLUMN_HABIT_REMINDER_HOURS + " INTEGER," +
                    COLUMN_HABIT_REMINDER_MESSAGES + " TEXT," +
                    COLUMN_HABIT_REMINDER_CUSTOMTEXT + " TEXT," +
                        COLUMN_HABIT_GROUP_ID  + " INTEGER," +
                    COLUMN_HABIT_GROUP_NAME  + " TEXT," +
                    COLUMN_USERID + " INTEGER,"
                    + "FOREIGN KEY (" + COLUMN_USERID + ") REFERENCES  " + User.TABLE_NAME + "(" + User.COLUMN_NAME_ID + "));";

    /**
     * The query to drop habit table
     */
    public static final String DROP_HABITS_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    // set values for period section
    public static final int[] period_buttonIDS = new int[]{R.id.daily_period, R.id.weekly_period, R.id.monthly_period};
    public static final String[] period_textList = new String[]{"DAY", "WEEK", "MONTH"};
    public static final int[] period_countList = new int[]{1, 7, 30, 365};

    // set values for color section
    public static final int[]  color_buttonIDS = new int[]{R.id.lightcoral_btn, R.id.slightdesblue_btn, R.id.fadepurple_btn, R.id.cyangreen_btn};
    public static final int[]  color_schemeIDS = new int[]{R.color.colorLightCoral, R.color.colorSlightDesBlue, R.color.colorFadePurple, R.color.colorCyanGreen};
    public static final String[]  colorList = new String[]{"lightcoral", "slightdesblue", "fadepurple", "cyangreen"};
    private static final String TAG = "HabitClass";

    private long habitID;
    private String title;
    private int occurrence;
    private int count;
    private int period;
    private String time_created;
    private String holder_color;
    private HabitReminder habitReminder;
    private HabitGroup group;

    /**This method is an empty constructor for habit*/
    public Habit() { }

    /**This method is a constructor for habit*/
    public Habit(String title, int occurrence, int period, String holder_color) {
        this.title = title;
        this.occurrence = occurrence;
        this.period = period;
        this.holder_color = holder_color;
    }

    /**This method is a constructor for habit*/
    public Habit(String title, int occurrence, int count, int period, String holder_color) {
        this.title = title;
        this.occurrence = occurrence;
        this.count = count;
        this.period = period;
        this.holder_color = holder_color;
    }


    /**This method is a constructor for habit*/
    public Habit(long habitID, String title, int occurrence, int count, int period, String time_created, String holder_color, HabitReminder habitReminder, HabitGroup group) {
        this.habitID = habitID;
        this.title = title;
        this.occurrence = occurrence;
        this.count = count;
        this.period = period;
        this.time_created = time_created;
        this.holder_color = holder_color;
        this.habitReminder = habitReminder;
        this.group = group;
    }

    /**This method is a constructor for habit*/
    public Habit(String title, int occurrence, int period, String time_created, String holder_color, HabitReminder habitReminder, HabitGroup group) {
        this.title = title;
        this.occurrence = occurrence;
        this.period = period;
        this.time_created = time_created;
        this.holder_color = holder_color;
        this.habitReminder = habitReminder;
        this.group = group;
    }

    public int calculateProgress(){
        int progress = (int) ((this.count/(double)this.occurrence) * 100);
        return Math.min(progress, 100);
    }

    /**@return String This return the title of the habit*/
    public String getTitle() {
        return title.toUpperCase().trim();
    }

    /**
     *
     * This method is used to set
     * the title of the habit
     *
     * @param title This parameter is used to set the
     *              title of the habit
     * */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * This method is used to format
     * the title of the habit
     *
     * @param title This parameter is used to set the
     *              ID of the habit
     * */
    public void modifyTitle(String title){
        this.title = title.toUpperCase().trim();
    }

    /**@return int This return the occurrence of the habit*/
    public int getOccurrence() {
        return occurrence;
    }

    /**
     *
     * This method is used to set
     * the occurrence of the habit
     *
     * @param occurrence This parameter is used to set the
     *              occurrence of the habit
     * */
    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }

    /**@return int This return the count of the habit*/
    public int getCount() {
        return count;
    }

    /**
     *
     * This method is used to set
     * the count of the habit
     *
     * @param count This parameter is used to set the
     *              title of the habit
     * */
    public void setCount(int count) {
        this.count = count;
    }


    /**@return int This return the primary key of the habit, habitID*/
    public long getHabitID() {
        return habitID;
    }

    /**
     *
     * This method is used to set
     * the ID of the habit
     *
     * @param habitID This parameter is used to set the
     *              ID of the habit
     * */
    public void setHabitID(long habitID) {
        this.habitID = habitID;
    }

    /**
     *
     * This method is used to increase
     * the count of the habit by 1
     * */
    public void addCount(){
        this.setCount(this.getCount() + 1);
    }

    /**
     *
     * This method is used to decrease
     * the count of the habit by 1
     * if the count is more than 0
     * */
    public void minusCount() {
        if (this.getCount() > 0){
            this.setCount(this.getCount() - 1);
        }
    }

    /**
     *
     * This method is used to modify
     * the count of the habit
     *
     * @param count This parameter is used to modify
     *              the count of the habit
     * */
    public void modifyCount(int count){
        this.count = count;
    }

    /**@return HabitReminder This return the reminder object of the habit*/
    public HabitReminder getHabitReminder() {
        return habitReminder;
    }

    /**
     *
     * This method is used to set
     * the reminder of the habit
     *
     * @param habitReminder This parameter is used to
     *              reminder of the habit
     * */
    public void setHabitReminder(HabitReminder habitReminder) {
        this.habitReminder = habitReminder;
    }


    /**@return int This return the primary key of the habit, habitID*/
    public int getPeriod() {
        return period;
    }

    /**
     *
     * This method is used to set
     * the period of the habit
     *
     * @param period This parameter is used to
     *              period of the habit
     * */
    public void setPeriod(int period) {
        this.period = period;
    }

    /**@return String This return the time created of the habit*/
    public String getTime_created() {
        return time_created;
    }

    /**
     *
     * This method is used to set
     * the time created of the habit
     *
     * @param time_created This parameter is used to
     *              time created of the habit
     * */
    public void setTime_created(String time_created) {
        this.time_created = time_created;
    }

    /**@return String This return the holder color of the habit*/
    public String getHolder_color() {
        return holder_color;
    }

    /**
     *
     * This method is used to set
     * the holder color of the habit
     *
     * @param holder_color This parameter is used to
     *              holder color of the habit
     * */
    public void setHolder_color(String holder_color) {
        this.holder_color = holder_color;
    }

    /**@return HabitGroup This return the group object of the habit*/
    public HabitGroup getGroup() {
        return group;
    }

    /**
     *
     * This method is used to set
     * the group of the habit
     *
     * @param group This parameter is used to
     *              group of the habit
     * */
    public void setGroup(HabitGroup group) {
        this.group = group;
    }

    /**
     *
     * This method is used to retrieve the period text of the period
     *
     * @param period This parameter is used to
     *              parse the period of the habit
     *
     * @return String This returns the period text of the habit
     *                  based on the period
     * */
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


    /**
     *
     * This method is used to retrieve the color id of the color
     *
     * @param color This parameter is used to
     *              parse the holder color of the habit
     *
     * @return int This returns the color id of the habit
     *                  based on the color
     * */
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


    /**
     *
     * Model used to manage the habitList
     *
     */
    public static class HabitList {

        private ArrayList<Habit> habitList;

        /**This method is a constructor for habitList*/
        public HabitList() { this.habitList = new ArrayList<>(); }

        /**
         *
         * This method is used to retrieve the habit object based on its index from the habitList
         *
         * @param index This parameter is used to
         *              parse the index of the habit from a habitList
         *
         * @return Habit This returns a Habit object
         * */
        public Habit getItemAt(Integer index) { return this.habitList.get(index); }

        /**
         *
         * This method is used to add the habit object to the habitList
         *
         * @param habit This parameter is used to
         *              parse the habit
         *
         * */
        public void addItem(Habit habit) { this.habitList.add(habit); }

        /**
         *
         * This method is used to remove the habit object to the habitList
         *
         * @param position This parameter is used to
         *              parse the position of the habit from the habitList
         *
         * */
        public void removeItemAt(int position) { this.habitList.remove(position); }

        /**
         *
         * This method is used to retrieve the size of the habitList
         *
         * @return Integer this will return the size of the habitList
         * */
        public Integer size() { return this.habitList.size(); }
    }
}

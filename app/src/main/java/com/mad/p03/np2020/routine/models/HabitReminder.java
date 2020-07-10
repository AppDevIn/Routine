package com.mad.p03.np2020.routine.models;

/**
 *
 * Model used to manage the habitReminder
 *
 * @author Hou Man
 * @since 02-06-2020
 */

public class HabitReminder {

    private String message;
    private int id;
    private int minutes;
    private int hours;
    private String custom_text;

    /**This method is an empty constructor for habitReminder*/
    public HabitReminder() { }

    public HabitReminder(String message, int minutes, int hours, String custom_text) {
        this.message = message;
        this.minutes = minutes;
        this.hours = hours;
        this.custom_text = custom_text;
    }

    /**This method is a constructor for habitReminder*/
    public HabitReminder(String message, int id, int minutes, int hours, String custom_text) {
        this.message = message;
        this.id = id;
        this.minutes = minutes;
        this.hours = hours;
        this.custom_text = custom_text;
    }

    /**@return String This return the custom text of the habitReminder*/
    public String getCustom_text() {
        return custom_text;
    }

    /**
     *
     * This method is used to set
     * the custom text of the habitReminder
     *
     * @param custom_text This parameter is used to set the
     *              the custom text of the habitReminder
     * */
    public void setCustom_text(String custom_text) {
        this.custom_text = custom_text;
    }

    /**@return String This return the message title of the habitReminder*/
    public String getMessage() {
        return message;
    }

    /**
     *
     * This method is used to set
     * message title of the habitReminder
     *
     * @param message This parameter is used to set the
     *              message title of the habitReminder
     * */
    public void setMessage(String message) {
        this.message = message;
    }

    /**@return int This return the ID of the habitReminder*/
    public int getId() {
        return id;
    }

    /**
     *
     * This method is used to set
     * the ID of the habitReminder
     *
     * @param id This parameter is used to set the
     *             the ID of the habitReminder
     * */
    public void setId(int id) {
        this.id = id;
    }

    /**@return int This return the minutes of the habitReminder*/
    public int getMinutes() {
        return minutes;
    }

    /**
     *
     * This method is used to set
     * the minutes of the habitReminder
     *
     * @param minutes This parameter is used to set the
     *              the minutes of the habitReminder
     * */
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    /**@return int This return the hours of the habitReminder*/
    public int getHours() {
        return hours;
    }

    /**
     *
     * This method is used to set
     * the hours of the habitReminder
     *
     * @param hours This parameter is used to set the
     *              the hours of the habitReminder
     * */
    public void setHours(int hours) {
        this.hours = hours;
    }

    /**
     *
     * This method is used to check whether two habitReminder is identical
     *
     * @param habitReminder This parameter is used to get the
     *              the habitReminder
     *
     * @return boolean This will return true if two objects is identical and false if they're not
     * */
    public boolean isIdentical(HabitReminder habitReminder){
        return this.message.equals(habitReminder.message) &&
                this.hours == habitReminder.getHours() &&
                this.minutes == habitReminder.getMinutes() &&
                this.custom_text.equals(habitReminder.getCustom_text()) &&
                this.id == habitReminder.getId();
    }
}

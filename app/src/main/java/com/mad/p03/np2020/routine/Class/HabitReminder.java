package com.mad.p03.np2020.routine.Class;

public class HabitReminder {

    String message;
    int id;
    int minutes;
    int hours;
    String custom_text;


    public HabitReminder(String message, int id, int minutes, int hours, String custom_text) {
        this.message = message;
        this.id = id;
        this.minutes = minutes;
        this.hours = hours;
        this.custom_text = custom_text;
    }

    public String getCustom_text() {
        return custom_text;
    }

    public void setCustom_text(String custom_text) {
        this.custom_text = custom_text;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }
}

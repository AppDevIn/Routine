package com.mad.p03.np2020.routine.Habit.Interface;

/**
 *
 * This is used to notify data set changed when there is changes in SQLiteDatabase
 *
 * @author Hou Man
 * @since 02-08-2020
 */

public interface HabitDBObserver {
    // this will notify the adapter as database is changed
    void onDatabaseChanged();
}

package com.mad.p03.np2020.routine.Habit.Interface;

/**
 *
 * This is used to observe whether there is any change in SQLiteDatabase
 *
 * @author Hou Man
 * @since 02-08-2020
 */

public interface HabitDBObservable {
    //register the observer with this method
    void registerDbObserver(HabitDBObserver habitDBObserver);
    //unregister the observer with this method
    void removeDbObserver(HabitDBObserver habitDBObserver);
    //call this method upon database change
    void notifyDbChanged();
}

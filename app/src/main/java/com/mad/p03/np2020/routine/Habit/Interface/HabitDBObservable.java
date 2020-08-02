package com.mad.p03.np2020.routine.Habit.Interface;


public interface HabitDBObservable {
    //register the observer with this method
    void registerDbObserver(HabitDBObserver habitDBObserver);
    //unregister the observer with this method
    void removeDbObserver(HabitDBObserver habitDBObserver);
    //call this method upon database change
    void notifyDbChanged();
}

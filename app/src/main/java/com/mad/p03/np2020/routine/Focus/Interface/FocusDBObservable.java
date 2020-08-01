package com.mad.p03.np2020.routine.Focus.Interface;

/**
 * Custom interface to listen for SQLiteDatabase changes
 *
 *  @author Lee Quan Sheng
 *  @since 01-08-2020
 */
public interface FocusDBObservable {
    //register the observer with this method
    void registerDbObserver(FocusDBObserver focusDBObserver);
    //unregister the observer with this method
    void removeDbObserver(FocusDBObserver focusDBObserver);
    //call this method upon database change
    void notifyDbChanged();

}
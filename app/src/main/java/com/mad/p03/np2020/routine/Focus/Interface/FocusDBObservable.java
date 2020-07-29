package com.mad.p03.np2020.routine.Focus.Interface;

import com.mad.p03.np2020.routine.Focus.Interface.FocusDBObserver;

public interface FocusDBObservable {
    //register the observer with this method
    void registerDbObserver(FocusDBObserver focusDBObserver);
    //unregister the observer with this method
    void removeDbObserver(FocusDBObserver focusDBObserver);
    //call this method upon database change
    void notifyDbChanged();

}
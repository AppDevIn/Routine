package com.mad.p03.np2020.routine.background;

public interface DatabaseObservable {
    //register the observer with this method
    void registerDbObserver(DatabaseObserver databaseObserver);
    //unregister the observer with this method
    void removeDbObserver(DatabaseObserver databaseObserver);
    //call this method upon database change
    void notifyDbChanged();

}
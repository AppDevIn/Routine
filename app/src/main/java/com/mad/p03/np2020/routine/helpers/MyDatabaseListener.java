package com.mad.p03.np2020.routine.helpers;

/**
 * 
 * This interface defines the onDataAdd and OnDataDelete which i will like to
 * communicate with my owner
 */
public interface MyDatabaseListener {

    void onDataAdd(Object object);
    void onDataUpdate(Object object);
    void onDataDelete(String ID);
}

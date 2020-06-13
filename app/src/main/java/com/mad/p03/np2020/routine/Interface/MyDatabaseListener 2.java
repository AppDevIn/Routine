package com.mad.p03.np2020.routine.Interface;

import com.mad.p03.np2020.routine.Class.Section;

/**
 * 
 * This interface defines the onDataAdd and OnDataDelete which i will like to
 * communicate with my owner
 */
public interface MyDatabaseListener {

    /**
     *
     * @param object The object that got added
     */
    public void onDataAdd(Object object);

    /**
     * @param ID The ID of the object you want delete
     */
    public void onDataDelete(String ID);

    /**
     *
     * @param object The object you want to update
     */
    public void onDataUpdate(Object object);
}

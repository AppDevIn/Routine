package com.mad.p03.np2020.routine.database;

import com.mad.p03.np2020.routine.Class.Section;

// Step 1 - This interface defines the type of messages I want to communicate to my owner
public interface MyDatabaseListener {

    public void onDataAdd(Object object);
    public void onDataDelete(String ID);
}

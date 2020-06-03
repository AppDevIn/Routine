package com.mad.p03.np2020.routine.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import androidx.annotation.Nullable;

/**
 *
 *
 * This created to store common things such
 * as the name of the database the version
 *
 * @author Jeyavishnu
 * @since 03-06-2020
 */
public class DBHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "MyRoutine.db";
    static final int DATABASE_VERSION = 8;

    //Listener
    static MyDatabaseListener mMyDatabaseListener;

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     *
     * Called when the database is created for
     * the first time.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    /**
     * Called when the database needs to be upgraded
     * @param sqLiteDatabase The database.
     * @param i The old database version.
     * @param i1 The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

}

package com.mad.p03.np2020.routine.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.mad.p03.np2020.routine.Class.Focus;
import com.mad.p03.np2020.routine.Class.Habit;
import com.mad.p03.np2020.routine.Class.HabitGroup;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.Task;
import com.mad.p03.np2020.routine.Class.User;

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
    static final int DATABASE_VERSION = 10;

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
        //Create user database
        sqLiteDatabase.execSQL(Section.SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(Task.SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(User.SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(Habit.CREATE_HABITS_TABLE);
        sqLiteDatabase.execSQL(HabitGroup.CREATE_GROUPS_TABLE);
        sqLiteDatabase.execSQL(Focus.CREATE_SQL);
    }

    /**
     * Called when the database needs to be upgraded
     * @param sqLiteDatabase The database.
     * @param i The old database version.
     * @param i1 The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL(Section.SQL_DELETE_ENTRIES); // Delete existing user
        sqLiteDatabase.execSQL(Task.SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(User.SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(Habit.DROP_HABITS_TABLE);
        sqLiteDatabase.execSQL(HabitGroup.DROP_GROUPS_TABLE);
        sqLiteDatabase.execSQL(Focus.SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }

    /**
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db,oldVersion,newVersion);
    }

}

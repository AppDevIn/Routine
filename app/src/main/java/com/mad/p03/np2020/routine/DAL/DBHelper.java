package com.mad.p03.np2020.routine.DAL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


<<<<<<< HEAD:app/src/main/java/com/mad/p03/np2020/routine/DAL/DBHelper.java
import com.mad.p03.np2020.routine.helpers.MyDatabaseListener;
import com.mad.p03.np2020.routine.models.Check;
=======
>>>>>>> master:app/src/main/java/com/mad/p03/np2020/routine/DAL/DBHelper.java
import com.mad.p03.np2020.routine.models.Focus;
import com.mad.p03.np2020.routine.models.Habit;
import com.mad.p03.np2020.routine.models.HabitGroup;
import com.mad.p03.np2020.routine.models.Section;
import com.mad.p03.np2020.routine.models.Task;
import com.mad.p03.np2020.routine.models.User;
<<<<<<< HEAD:app/src/main/java/com/mad/p03/np2020/routine/DAL/DBHelper.java
=======
import com.mad.p03.np2020.routine.helpers.MyDatabaseListener;
>>>>>>> master:app/src/main/java/com/mad/p03/np2020/routine/DAL/DBHelper.java

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
    static final int DATABASE_VERSION = 13;

    //Listener
    static MyDatabaseListener mMyDatabaseListener;

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     *
     * Called when the database is created for
     * the first time. This will create all the
     * table like Section, task, user, habit, habitgroup
     * and focus
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(Section.SQL_CREATE_ENTRIES); //Create section database
        sqLiteDatabase.execSQL(Task.SQL_CREATE_ENTRIES); //Create task database
        sqLiteDatabase.execSQL(User.SQL_CREATE_ENTRIES); //Create user database
        sqLiteDatabase.execSQL(Habit.CREATE_HABITS_TABLE); //Create habit database
        sqLiteDatabase.execSQL(HabitGroup.CREATE_GROUPS_TABLE); //Create habit group database
        sqLiteDatabase.execSQL(Focus.CREATE_SQL); //Create focus database
        sqLiteDatabase.execSQL(Check.SQL_CREATE_ENTRIES); //Create focus database
    }

    /**
     *
     * Called when the database needs to be upgraded. This will drop the
     * database and create a new one. The data from the previous one will
     * move forward into the new db
     *
     * @param sqLiteDatabase The database.
     * @param i The old database version.
     * @param i1 The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL(Section.SQL_DELETE_ENTRIES); // Delete existing section
        sqLiteDatabase.execSQL(Check.SQL_DELETE_ENTRIES); // Delete existing check
        sqLiteDatabase.execSQL(Task.SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(User.SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(Habit.DROP_HABITS_TABLE);
        sqLiteDatabase.execSQL(HabitGroup.DROP_GROUPS_TABLE);
        sqLiteDatabase.execSQL(Focus.SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);

        //Add previous data
    }

    /**
     *
     * If current version is newer than the requested one. This will drop the
     * database and create a new one.
     *
     * @param db The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db,oldVersion,newVersion);
    }

}

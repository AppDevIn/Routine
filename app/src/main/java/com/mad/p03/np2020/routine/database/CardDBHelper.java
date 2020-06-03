package com.mad.p03.np2020.routine.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CardDBHelper extends SQLiteOpenHelper {

    private static final String TAG = "Card Database Helper";

    public static final String DATABASE_NAME = "NotesDB.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "Notes_Table";
    public static final String COL1 = "Card_Name";
    public static final String COL2 = "Notes";

    public CardDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        String Create_Notes_Table = "CREATE TABLE " + TABLE_NAME + "(" + COL1 + " TEXT," + COL2 + " TEXT" + ")";
        sqLiteDatabase.execSQL(Create_Notes_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}

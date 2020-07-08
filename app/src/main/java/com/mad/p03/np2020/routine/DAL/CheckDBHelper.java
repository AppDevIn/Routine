package com.mad.p03.np2020.routine.DAL;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mad.p03.np2020.routine.models.Check;
import com.mad.p03.np2020.routine.models.Section;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class CheckDBHelper extends DBHelper{

    private final String TAG = "CheckDatabase";

    public CheckDBHelper(@Nullable Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        super.onCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        super.onUpgrade(sqLiteDatabase, i, i1);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    public List<Check> getSection(String sectionID){

        List<Check> checkList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Log.d(TAG, "getUser: Querying data");


        //Get the data from sqlite
        Cursor cursor =  db.rawQuery( "SELECT * FROM " + Check.TABLE_NAME+ " WHERE SectionID="+sectionID+"", null );

        if (cursor != null){
            do {
                Check check = Check.fromCursor(cursor);

                Log.d(TAG, "getAllSections(): Reading data" + check.toString() );

                checkList.add(check);
            } while (cursor.moveToNext());
        }

        Log.d(TAG, "Reading data" + checkList.toString() );


        //Close the DB connection
        db.close();

        return checkList;

    }

}

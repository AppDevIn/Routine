package com.mad.p03.np2020.routine.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mad.p03.np2020.routine.Class.HabitGroup;

import java.util.ArrayList;

public class HabitGroupDBHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "MyRoutine.db";
    static final int DATABASE_VERSION = 3;
    private final String TAG = "HabitGroupDatabase";

    public HabitGroupDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HabitGroup.CREATE_GROUPS_TABLE);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(HabitGroup.DROP_GROUPS_TABLE);
        onCreate(db);
        Log.d(TAG, "onUpgrade: ");
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }

    public ArrayList<HabitGroup> getAllGroups(){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<HabitGroup> habitGroups = new ArrayList<>();

        Cursor res =  db.rawQuery( "select * from " + HabitGroup.TABLE_NAME, null );
        res.moveToFirst();

        while(!res.isAfterLast()) {
            long id = res.getLong(res.getColumnIndex(HabitGroup.COLUMN_ID));
            String name = res.getString(res.getColumnIndex(HabitGroup.COLUMN_GROUP_NAME));

            Log.d(TAG, "getAllGroups: "+name);
            habitGroups.add(new HabitGroup(id, name));
            res.moveToNext();
        }

        db.close();

        Log.d(TAG, "getAllGroups: " + habitGroups.size());
        return habitGroups;
    }

    public long insertGroup(HabitGroup habitGroup){
        ContentValues values = new ContentValues();
        Log.d(TAG, "insertGroup: "+habitGroup.getGrp_name());
        values.put(HabitGroup.COLUMN_GROUP_NAME, habitGroup.getGrp_name());

        SQLiteDatabase db = this.getWritableDatabase();

        long id =  db.insert(HabitGroup.TABLE_NAME, null, values);
        if (id == -1){
            Log.d(TAG, "insertGroup: " + "Error");
        }else{
            Log.d(TAG, "insertGroup: " + "Successful");

        }
        db.close();

        return id;
    }

}

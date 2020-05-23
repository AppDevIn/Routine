package com.mad.p03.np2020.routine.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.mad.p03.np2020.routine.Class.User;
import androidx.annotation.NonNull;


//This database is just the cache for online data
public class UserDBHelper extends SQLiteOpenHelper {

    //TODO: Declare the constant
    static final String DATABASE_NAME = "MyRoutine.db";
    static final int DATABASE_VERSION = 1;
    private final String TAG = "UserDatebase";



    public UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "User database is being created");

        //Create user database
        sqLiteDatabase.execSQL(User.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, "User database is being upgraded");

        sqLiteDatabase.execSQL(User.SQL_DELETE_ENTRIES); // Delete existing user dat
        onCreate(sqLiteDatabase);

    }

    //If current version is newer than the requested one
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "User database is downgraded");

        onUpgrade(db,oldVersion,newVersion);
    }

    /**
     * To insert the user which name, email and dob
     * in the sqlite
     *
     * @param user passed to acces the name, email and dob
     * @return the id in this case the row in belongs
     */
    public long insertUser(User user){

        Log.d(TAG, "insertUser(): Preparing to insert the new user ");

        //Add values into the database
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(User.COLUMN_NAME_NAME, user.getName());
        values.put(User.COLUMN_NAME_EMAIL, user.getEmailAdd());
        values.put(User.COLUMN_NAME_PASSWORD, user.getPassword());

        // Insert the new row, returning the primary key value of the new row
        //If -1 means there is an error

        long id = db.insert(User.TABLE_NAME, null, values);

        if(id == -1){
            Log.e(TAG, "insertUser(): There has been error inserting the data" );
        } else{
            Log.d(TAG, "insertUser(): Data inserted");
        }

        return id;
    }


    /**
     * Get the first user in the SQL database
     *
     * @return the user the back with the name, email and dob
     */
    public User getUser(){
        SQLiteDatabase db = this.getReadableDatabase();

        Log.d(TAG, "getUser: Querying data");
        //Get the data from sqllite
        Cursor cursor = db.rawQuery("select * from " + User.TABLE_NAME, null);

        if (cursor != null)
            cursor.moveToFirst(); //Only getting the first value

        //Prepare a user object
        User user = new User(
                cursor.getString(cursor.getColumnIndex(User.COLUMN_NAME_NAME)),
                cursor.getString(cursor.getColumnIndex(User.COLUMN_NAME_PASSWORD)),
                cursor.getString(cursor.getColumnIndex(User.COLUMN_NAME_EMAIL))
        );

        //Close the DB connection
        db.close();

        return user;

    }



}

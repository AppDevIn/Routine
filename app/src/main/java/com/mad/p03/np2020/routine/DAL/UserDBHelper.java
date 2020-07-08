package com.mad.p03.np2020.routine.DAL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.mad.p03.np2020.routine.models.User;


//This database is just the cache for online data
public class UserDBHelper extends DBHelper {

    private final String TAG = "UserDatebase";

    public UserDBHelper(Context context) {
        super(context);
    }


    /**
     * Called when the database is created for
     * the first time. This where the creation of
     * the user data table will occur
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        super.onCreate(sqLiteDatabase);
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
        super.onUpgrade(sqLiteDatabase,i,i1);
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
        super.onDowngrade(db, oldVersion, newVersion);
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
        values.put(User.COLUMN_NAME_ID, user.getUID());
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
    public User getUser(String UID){
        SQLiteDatabase db = this.getReadableDatabase();

        Log.d(TAG, "getUser: Querying data");
        //Get the data from sqllite
        Cursor cursor = db.rawQuery("select * from " + User.TABLE_NAME + " where " + User.COLUMN_NAME_ID + "='" + UID+"';", null);

        User user = null;
        if (cursor.moveToFirst()) {
            //Prepare a user object
            user = new User(
                    cursor.getString(cursor.getColumnIndex(User.COLUMN_NAME_NAME)),
                    cursor.getString(cursor.getColumnIndex(User.COLUMN_NAME_PASSWORD)),
                    cursor.getString(cursor.getColumnIndex(User.COLUMN_NAME_EMAIL))
            );
        }

        //Close the DB connection
        db.close();

        return user;

    }



}

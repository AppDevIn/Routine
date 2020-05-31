package com.mad.p03.np2020.routine.Class;

import android.annotation.SuppressLint;
import android.nfc.FormatException;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mad.p03.np2020.routine.Class.Label;
import com.mad.p03.np2020.routine.Class.Section;

import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Class.FocusHolder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User implements Parcelable {

    //User table
    public static final String TABLE_NAME = "user"; //Name of the table

    //Column name in the user table
    public static final String COLUMN_NAME_ID = "id";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_NAME_EMAIL = "email";
    public static final String COLUMN_NAME_PASSWORD = "password"; //The password will be encrypted

    //The creation of the database
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + " TEXT," +
                    COLUMN_NAME_NAME + " TEXT," +
                    COLUMN_NAME_EMAIL + " TEXT," +
                    COLUMN_NAME_PASSWORD + " TEXT)";

    //Query to delete the table
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


    FirebaseUser mAuth;
    private String mName;
    private String mUID;
    private String mEmailAddr;
    private String mPassword;
    private Date mDateOfBirth;
    private List<Section> mSectionList;
    private String mPPID;
    private List<Label> mListLabel;
    private ArrayList<FocusHolder> mFocusList;

    protected User(Parcel in) {
        mAuth = in.readParcelable(FirebaseUser.class.getClassLoader());
        mName = in.readString();
        mUID = in.readString();
        mEmailAddr = in.readString();
        mPassword = in.readString();
        mPPID = in.readString();
        mFocusList = in.createTypedArrayList(FocusHolder.CREATOR);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public ArrayList<FocusHolder> getmFocusList() {
        return mFocusList;
    }

    public void setmFocusList(ArrayList<FocusHolder> mFocusList) {
        this.mFocusList = mFocusList;
    }

    public void addFocusList(FocusHolder focusHolder){
        this.mFocusList.add(focusHolder);
    }

    public void removeFocusList(FocusHolder focusHolder){
        this.mFocusList.remove(focusHolder);
    }
    private List<Label> mListLabel;
    private List<Label> mListLabel;
    private List<Focus> mFocusList;

    public User() {
    }

    User(String name, String password) {
        this.mName = name;
        this.mPassword = password;
    }

    public User(String name, String password, String emailAddr) {
        this.mName = name;
        this.mPassword = password;
        this.mEmailAddr = emailAddr;
//        this.mDateOfBirth = dateOfBirth;
    }

    public void setName(String name) throws FormatException {

        if(!name.isEmpty()){
            mName = name;
        }else
            throw new FormatException("Text is empty");
    }

    public void setEmailAdd(String emailAdd) throws FormatException {

        if(!emailAdd.isEmpty()){
            //Declare Constants
            String EMAILPATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            if(emailAdd.trim().matches(EMAILPATTERN)) {
                mEmailAddr = emailAdd;

            }else{
                //Error message for email when .com and @ is not present
                throw new FormatException("Text does not have @ and .com");


            }
        }else {
            throw new FormatException("Text is empty");
        }
    }

    public void setPassword(String password) throws FormatException{
        //TODO: Encrypt the password

        if(!password.isEmpty()){
            String STRONGPASSWORD = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
            if(password.trim().matches(STRONGPASSWORD)) {
                mPassword = password;
            } else {


                //a digit must occur at least once
                //a lower case letter must occur at least once
                //an upper case letter must occur at least once
                //a special character must occur at least once
                //    no whitespace allowed in the entire string
                //anything, at least eight places though

                //Error message for password when password doesn't
                //have digit, lower and upper case, special character and min 8 letter
                throw new FormatException("Text doesn't meet strong password requirement");
            }
        } else {
            //Error message for empty text
            throw new FormatException("Text is empty");
        }

    }

    public void setDateOfBirth(String dateOfBirth) throws FormatException{
        if(!dateOfBirth.isEmpty()){
            String DOBPATTERN = "[0-9]+[0-9]+/+[0-9]+[0-9]+/[0-9]+[0-9][0-9]+[0-9]";
            if(dateOfBirth.trim().matches(DOBPATTERN)) {
                //Get the date from String
                mDateOfBirth = stringToDate(dateOfBirth);

            } else {

                //Error message for DOB when it does match DD/MM/YYYY
                throw new  FormatException("Text doesn't meet DOB (DD/MM/YYYY) requirement");
            }
        } else {
            //Error message for empty text
            throw new  FormatException("Text is empty");
        }

    }

    public void setAuth(FirebaseUser auth) {
        mAuth = auth;

        //Set the UID for this user
        this.setUID(this.mAuth.getUid());
    }

    public void setUID(String UID) {
        mUID = UID;
    }

    public String getUID() {
        return mUID;
    }

    public FirebaseUser getAuth() {
        return mAuth;
    }

    public String getName() {
        return mName;
    }

    public String getEmailAdd() {
        return mEmailAddr;
    }

    public String getPassword() {
        return mPassword;
    }

    public String digestPassword() {
        return mPassword;
    }

    public String getDateOfBirth() {

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return mDateOfBirth == null ? null : dateFormat.format(mDateOfBirth);
    }

    public void changeName(String newName){
        // TODO: Please upload any changes to this class to the main branch`
    }

    public void setPPID(){
        // TODO: Please upload any changes to this class to the main branch`
    }

    public void addSection(Section section){
        // TODO: Please upload any changes to this class to the main branch`
    }

    public void addFocus(){
        // TODO: Please upload any changes to this class to the main branch`
    }

    public void resetPwd(){
        // TODO: Please upload any changes to this class to the main branch`
    }


    /**
     * To convert string to date
     * The function has 2 possible returns
     *
     * if Successfully changed a Date object will be return
     * else null will be returned
     *
     * @param DOB is a String that is provided by the user
     */

    private Date stringToDate(String DOB){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyyy");
        try {
            return sdf.parse(DOB);
        } catch (ParseException ex) {
            Log.e("Exception", "Date unable to change reason: "+ ex.getLocalizedMessage());
            return null;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mAuth, flags);
        dest.writeString(mName);
        dest.writeString(mUID);
        dest.writeString(mEmailAddr);
        dest.writeString(mPassword);
        dest.writeString(mPPID);
        dest.writeTypedList(mFocusList);
    }
}
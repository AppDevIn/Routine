package com.mad.p03.np2020.routine.models;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class Team {

    /**The table name of this class in SQL*/
    public static final String TABLE_NAME = "Team";

    /**Used as the foreign key for this table*/
    public static final String COLUMN_SectionID = "SectionID";
    /**The email*/
    public static final String COLUMN_EMAIL = "Email";


    /**
     * The query needed to create a sql database
     * for the check
     */
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "("
                    + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_EMAIL + " TEXT,"
                    + COLUMN_SectionID + " TEXT ,"
                    + "FOREIGN KEY (" + COLUMN_SectionID + ") REFERENCES  " + Section.TABLE_NAME + "(" + Section.COLUMN_SECTION_ID + "));";

    /**
     * The query needed to delete SQL table check from the database
     */
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    String sectionID;
    List<String> email = new ArrayList<>();

    public String getSectionID() {
        return sectionID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public void addEmail(String email){
        this.email.add(email);
    }
}

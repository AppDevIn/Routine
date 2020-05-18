package com.mad.p03.np2020.routine.Class;

import com.google.firebase.auth.FirebaseAuth;
import com.mad.p03.np2020.routine.Class.Focus;
import com.mad.p03.np2020.routine.Class.Label;
import com.mad.p03.np2020.routine.Class.Section;

import com.mad.p03.np2020.routine.Class.Focus;

import java.util.Date;
import java.util.List;

public class User {

    FirebaseAuth mAuth;
    private String mName;
    private int mUID;
    private String mEmailAddr;
    private String mPassword;
    private Date mDateOfBirth;
    private List<Section> mSectionList;
    private String mPPID;
    private List<Label> mListLabel;
    private List<Focus> mFocusList;


    User(String name, String password) {
        this.mName = name;
        this.mPassword = password;
    }

    User(String name, String password, String emailAddr, Date dateOfBirth) {
        this.mName = name;
        this.mPassword = password;
        this.mEmailAddr = emailAddr;
        this.mDateOfBirth = dateOfBirth;
    }

    public String getName() {
        return mName;
    }

    public String getEmailAddr() {
        return mEmailAddr;
    }

    public String getPassword() {
        return mPassword;
    }

    public Date getDateOfBirth() {
        return mDateOfBirth;
    }

    public void changeName(String newName){
        // TODO: Please upload any chnages to this class to the main branch`
    }

    public void setPPID(){
        // TODO: Please upload any chnages to this class to the main branch`
    }

    public void addSection(Section section){
        // TODO: Please upload any chnages to this class to the main branch`
    }

    public void addFocus(){
        // TODO: Please upload any chnages to this class to the main branch`
    }

    public void resetPwd(){
        // TODO: Please upload any chnages to this class to the main branch`
    }



















































    

    

}
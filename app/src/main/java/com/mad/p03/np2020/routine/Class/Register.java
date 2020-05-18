package com.mad.p03.np2020.routine.Class;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mad.p03.np2020.routine.RegisterActivity;

import java.util.Date;
import java.util.concurrent.Executor;

import androidx.annotation.NonNull;

public class Register extends User {

    private FirebaseAuth mAuth;

    public Register(String name, String password, String Email, Date DOB) {
        super(name, password, Email, DOB);

        mAuth = FirebaseAuth.getInstance();
    }


    /**
     * Will use the password from user and encrypt it
     *
     * Return: Encrypted password:String
    */
    private void digestPassword(){
        //TODO: Encrypt the password

    }

    public Task<AuthResult> createUser(Context context){

        //Creating a new account by passing the user's email and password
        return mAuth.createUserWithEmailAndPassword(getEmailAddr(),getPassword());

    }

    /**
     * Set initial data in Realtime firebase
     * At same time save store the firebase user
     *
     */

}

package com.mad.p03.np2020.routine;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class authFirebase extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            startActivity(new Intent(authFirebase.this, Home.class));
        }else{
            startActivity(new Intent(authFirebase.this, LoginActivity.class));
        }
    }
}

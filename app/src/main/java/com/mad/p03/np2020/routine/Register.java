package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Register extends AppCompatActivity {
    
    //Declare Constants
    final static String TAG = "Register";

    //Declare member variables
    Button mSubmit;
    int progessCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: Doing binding and startup logic");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: GUI is started ready to move to foreground");

        //Find IDs
        mSubmit = findViewById(R.id.btnSubmit);



    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: GUI is in the Foreground");


        //Listener to the submit button
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCardView(true);
            }
        });

    }

    private void updateCardView(Boolean isForward){
        if (isForward) {
            progessCount++;
        } else {
            progessCount--;
        }

        switch (progessCount){
            case 1:
                askName();
                break;
            case 2:
                askEmail();
                break;
            case 3:
                askPassword();
                break;
            case 4:
                askConfirmPassword();
                break;
            case 5:
                askDateOfBirth();
                break;
            case 6:
                startRegistration();
                break;
            default: 
                askName();
                break;
        }
    }

    //Changes the cardview details to name
    private void askName() {
        Log.d(TAG, "askName: ");

    }

    //Changes the cardview details to emial
    private void askEmail() {
        Log.d(TAG, "askEmail: ");

    }

    //Changes the cardview details to Password
    private void askPassword() {
        Log.d(TAG, "askPassword: ");

    }

    //Changes the cardview details to Confirm Password
    private void askConfirmPassword() {
        Log.d(TAG, "askConfirmPassword: ");

    }

    //Changes the cardview details to Confirm Password
    private void askDateOfBirth() {
        Log.d(TAG, "askDateOfBirth: ");

    }
    
    private void startRegistration(){
        Log.d(TAG, "startRegistration: ");
    }

}



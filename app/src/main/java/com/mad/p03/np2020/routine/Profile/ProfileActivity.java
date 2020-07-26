package com.mad.p03.np2020.routine.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.mad.p03.np2020.routine.LoginActivity;
import com.mad.p03.np2020.routine.R;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, UsernameDialog.UsernameDialogListener, PasswordDialog.PasswordDialogListener {

    private final String TAG = "ProfileActivity";

    FirebaseAuth mAuth;
    Button changeName;
    Button changePassword;
    Button reportProblem;
    Button rateApp;
    Button logoutButton;
    TextView username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        //TO-DO: Set username from database

        changeName = findViewById(R.id.changeNameButton);
        changePassword = findViewById(R.id.changePasswordButton);
        reportProblem = findViewById(R.id.reportProblemButton);
        rateApp = findViewById(R.id.rateAppButton);
        logoutButton = findViewById(R.id.logoutButton);

        /*
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Logout Clicked!");
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                mAuth.signOut();
                startActivity(intent);
            }
        });
         */

        changeName.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        reportProblem.setOnClickListener(this);
        rateApp.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.logoutButton:
                Log.v(TAG, "User Logging out!");
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                mAuth.signOut();
                startActivity(intent);
                break;

            case R.id.changeNameButton:
                Log.v(TAG, "User changing name!");
                changeUsername();
                break;

            case R.id.changePasswordButton:
                Log.v(TAG, "User changing password");
                changePassword();
                break;

            case R.id.reportProblemButton:
                Log.v(TAG, "User reporting a problem");
                break;

            case R.id.rateAppButton:
                Log.v(TAG, "User rating app");
                break;
        }

    }

    public void changeUsername()
    {
        UsernameDialog usernameDialog = new UsernameDialog();
        usernameDialog.show(getSupportFragmentManager(), "Change Username Dialog");
    }

    @Override
    public void getNewUsername(String newUsername)
    {
        username.setText(newUsername);
        //TO-DO: Update database with new username
    }

    public void changePassword()
    {
        PasswordDialog passwordDialog = new PasswordDialog();
        passwordDialog.show(getSupportFragmentManager(), "Change Password Dialog");
    }

    @Override
    public void getNewPassword(String oldPassword, String newPassword, String reNewPassword) {

    }
}

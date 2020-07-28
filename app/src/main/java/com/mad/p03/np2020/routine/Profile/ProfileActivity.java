package com.mad.p03.np2020.routine.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.mad.p03.np2020.routine.DAL.DBHelper;
import com.mad.p03.np2020.routine.LoginActivity;
import com.mad.p03.np2020.routine.R;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = "ProfileActivity";

    FirebaseAuth mAuth;
    Button logoutButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();

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
                DBHelper dbHelper = new DBHelper(this);
                dbHelper.deleteAll();
                break;
        }

    }
}

package com.mad.p03.np2020.routine.Profile;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.FormatException;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.mad.p03.np2020.routine.DAL.UserDBHelper;
import com.mad.p03.np2020.routine.LoginActivity;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.models.User;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, UsernameDialog.UsernameDialogListener, PasswordDialog.PasswordDialogListener {

    private final String TAG = "ProfileActivity";

    FirebaseAuth mAuth;
    User mUser;
    FirebaseUser firebaseUser;
    UserDBHelper userDBHelper;
    Button changeName;
    Button changePassword;
    Button reportProblem;
    Button rateApp;
    Button logoutButton;
    TextView username;
    String UID;
    private DatabaseReference mDatabase;
    DatabaseReference userRef;
    String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        UID = firebaseUser.getUid();

        mUser = new UserDBHelper(this).getUser(mAuth.getUid());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userRef = mDatabase.child("users").child(UID);
        userDBHelper = new UserDBHelper(getApplicationContext());

        username = findViewById(R.id.username);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("Name").getValue().toString();
                username.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v(TAG, "onCancelled", databaseError.toException());
            }
        });

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
                logout();
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
                reportAProblem();
                break;

            case R.id.rateAppButton:
                Log.v(TAG, "User rating app");
                rateApp();
                break;
        }

    }

    public void changeUsername()
    {
        UsernameDialog usernameDialog = new UsernameDialog();
        usernameDialog.show(getSupportFragmentManager(), "Change Username Dialog");
    }

    @Override
    public void getNewUsername(String newUsername) throws FormatException {
        //mUser.setName(newUsername);

        Log.v(TAG, "New username: " + mUser.getName());

        mDatabase.child("users").child(UID).child("Name").setValue(newUsername);

        //userDBHelper.updateUser(UID, mUser);
        username.setText(newUsername);
    }


    public void changePassword()
    {
        //PasswordDialog passwordDialog = new PasswordDialog();
        //passwordDialog.show(getSupportFragmentManager(), "Change Password Dialog");
        mAuth.sendPasswordResetEmail(firebaseUser.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            MakeToast("Password reset email sent!");

                            logout();
                        }
                        else
                        {
                            MakeToast(task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    @Override
    public void getNewPassword(String oldPassword, String newPassword, String reNewPassword) {

    }

    public void rateApp()
    {
        try
        {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        }
        catch (ActivityNotFoundException e)
        {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details?id=com.mad.p03.np2020.routine");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21)
        {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        }
        else
        {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    public void logout()
    {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        mAuth.signOut();
        startActivity(intent);
    }

    public void MakeToast(String info)
    {
        Toast toast = Toast.makeText(ProfileActivity.this, info, Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.GRAY);
        TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);
        toast.show();
    }

    public void reportAProblem()
    {
        ReportDialog reportDialog = new ReportDialog();
        reportDialog.show(getSupportFragmentManager(), "Report a problem dialog");
    }
}

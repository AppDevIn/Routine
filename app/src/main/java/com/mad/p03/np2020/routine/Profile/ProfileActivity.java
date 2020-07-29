package com.mad.p03.np2020.routine.Profile;

import android.app.ProgressDialog;
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
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.mad.p03.np2020.routine.DAL.UserDBHelper;
import com.mad.p03.np2020.routine.LoginActivity;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.models.User;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

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
    Button changeProfile;
    Button logoutButton;
    TextView username;
    String UID;
    String name;
    String myUri = "";
    StorageTask uploadTask;
    StorageReference storageProfilePicture;
    DatabaseReference mDatabase;
    DatabaseReference userRef;
    private CircleImageView profileImageView;
    private Uri imageUri;

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

        storageProfilePicture = FirebaseStorage.getInstance().getReference().child("ProfilePicture");

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

        profileImageView = findViewById(R.id.profilePicture);
        changeName = findViewById(R.id.changeNameButton);
        changePassword = findViewById(R.id.changePasswordButton);
        reportProblem = findViewById(R.id.reportProblemButton);
        rateApp = findViewById(R.id.rateAppButton);
        logoutButton = findViewById(R.id.logoutButton);
        changeProfile = findViewById(R.id.changeProfileButton);

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
        changeProfile.setOnClickListener(this);

        getUserInfo();
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

            case R.id.changeProfileButton:
                Log.v(TAG, "User changing profile picture");
                changeProfilePicture();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
            uploadProfileImage();
        }
        else
        {
            MakeToast("Error, try again");
        }
    }

    private void uploadProfileImage()
    {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Set your profile");
        progressDialog.setMessage("Please wait, while we are setting your data");
        progressDialog.show();

        if (imageUri != null)
        {
            final StorageReference fileRef = storageProfilePicture.child(mAuth.getCurrentUser().getUid() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        myUri = downloadUri.toString();

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("image", myUri);

                        mDatabase.child(mAuth.getCurrentUser().getUid()).updateChildren(userMap);

                        progressDialog.dismiss();
                    }
                }
            });
        }
        else
        {
            progressDialog.dismiss();
            MakeToast("Image not selected");
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

    public void changeProfilePicture()
    {
        CropImage.activity().setAspectRatio(1, 1).start(ProfileActivity.this);
        uploadProfileImage();
    }

    public void getUserInfo()
    {
        storageProfilePicture.child(mAuth.getCurrentUser().getUid() + ".jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Picasso.get().load(task.getResult()).into(profileImageView);
            }
        });
    }
}
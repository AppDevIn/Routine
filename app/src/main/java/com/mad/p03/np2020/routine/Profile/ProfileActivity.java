package com.mad.p03.np2020.routine.Profile;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.FormatException;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.mad.p03.np2020.routine.DAL.DBHelper;
import com.mad.p03.np2020.routine.DAL.ScheduleDBHelper;
import com.mad.p03.np2020.routine.Habit.DAL.HabitDBHelper;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.mad.p03.np2020.routine.DAL.UserDBHelper;
import com.mad.p03.np2020.routine.LoginActivity;
import com.mad.p03.np2020.routine.NavBarHelper;
import com.mad.p03.np2020.routine.Profile.Dialogs.PasswordDialog;
import com.mad.p03.np2020.routine.Profile.Dialogs.ReportDialog;
import com.mad.p03.np2020.routine.Profile.Dialogs.UsernameDialog;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Habit.models.AlarmReceiver;
import com.mad.p03.np2020.routine.Habit.models.Habit;
import com.mad.p03.np2020.routine.Habit.models.HabitReminder;

import com.mad.p03.np2020.routine.models.CardNotification;
import com.mad.p03.np2020.routine.models.Schedule;
import com.mad.p03.np2020.routine.models.User;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.twitter.sdk.android.core.models.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.mad.p03.np2020.routine.models.User.habitListener;
import static com.mad.p03.np2020.routine.models.User.hgListener;
import static com.mad.p03.np2020.routine.models.User.hrListener;

/**
 *
 * Profile Class for managing Profile
 *
 * @author Pritheev
 * @since 02-06-2020
 *
 */
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
    BottomNavigationView bottomNavigationView;
    List<Schedule> scheduleList;
    List<Integer> uniqueIDList;
    ScheduleDBHelper scheduleDBHelper;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        scheduleDBHelper = new ScheduleDBHelper(getApplicationContext());

        //Initializing database variables
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        UID = firebaseUser.getUid();
        mUser = new UserDBHelper(this).getUser(mAuth.getUid());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        userRef = mDatabase.child("users").child(UID);
        userDBHelper = new UserDBHelper(getApplicationContext());
        storageProfilePicture = FirebaseStorage.getInstance().getReference().child("ProfilePicture");

        username = findViewById(R.id.username);

        //Listener for name change
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

        //changeProfile.setBackgroundResource(R.drawable.change_pp);

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

        //On touch listeners
        changeName.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        reportProblem.setOnClickListener(this);
        rateApp.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        changeProfile.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

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

        try {
            getUserInfo();
        } catch (Exception e) {
            Log.v(TAG, "No image uploaded");
        }

        bottomNavigationView = findViewById(R.id.bottomNavViewBar);
        bottomNavInit(bottomNavigationView);
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

    /**
     *
     * When crop activity called to open up image selector
     *
     * */
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

    /**
     *
     * Function for uploading profile image to firebase
     *
     * */
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
                    progressDialog.dismiss();
                }
            });
        }
        else
        {
            progressDialog.dismiss();
            MakeToast("Image not selected");
        }
    }

    //Calls username dialog
    public void changeUsername()
    {
        UsernameDialog usernameDialog = new UsernameDialog();
        usernameDialog.show(getSupportFragmentManager(), "Change Username Dialog");
    }

    //Changes username and sets it in firebase and SQL
    @Override
    public void getNewUsername(String newUsername) throws FormatException {
        //mUser.setName(newUsername);

        Log.v(TAG, "New username: " + mUser.getName());

        mDatabase.child("users").child(UID).child("Name").setValue(newUsername);

        userDBHelper.updateUserName(UID, mUser);
        username.setText(newUsername);

        if (newUsername.length() > 16)
        {
            MakeToast("Username limited at 16 characters!");
        }
    }

    //Changes password by calling firebase
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

                            //logout();
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

    //Opens an intent to google play to rate the app
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

    //Function for validating url to google play link of app
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

    /**
     *
     * Logout function that clears all alarms
     * and logs user out
     *
     * */
    public void logout()
    {
        //Remove animation
        ActivityOptions options =
                ActivityOptions.makeCustomAnimation(this, 0, 0);

        clearHabitAlarm(this);
        cancelRepeatingHabit();
        removeHabitEventListener();
        cancelScheduleAlarms();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mAuth.signOut();
        DBHelper dbHelper = new DBHelper(this);
        dbHelper.deleteAll();
        startActivity(intent, options.toBundle());
    }

    /**
     *
     * This method is used to re-register the habitReminder after rebooting(boot completed).
     *
     * @param context This parameter is used to get the context.
     *
     * */
    public void clearHabitAlarm(Context context){
        HabitDBHelper habitDBHelper = new HabitDBHelper(context);
        Habit.HabitList habitList = habitDBHelper.getAllHabits();
        // looping through each reminder
        for (int i = 0; i < habitList.size(); i++){
            // get the habit object
            Habit habit = habitList.getItemAt(i);
            // get the habit reminder object
            HabitReminder reminder = habit.getHabitReminder();
            // jump to next loop if reminder is null
            if (reminder == null){ continue;}
            Log.d(TAG, "clearHabitAlarm: ");
            // get the reminder attributes
            String title = habit.getTitle();
            int id = reminder.getId();
            String custom_text = reminder.getCustom_text();
            // register the reminder again
            cancelReminder(title, id, custom_text);
        }
    }

    /**
     *
     * This method is used to cancel the habitReminder.
     *
     * @param name This parameter refers to the title of the reminder.
     *
     * @param id This parameter refers to the unique id of the alarm.
     *
     * @param custom_txt This parameter refers to the custom message of the reminder.
     *
     * */
    public void cancelReminder(String name,int id, String custom_txt){
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.setAction("HabitTracker");
        intent.putExtra("Name", name);
        intent.putExtra("id",id);
        intent.putExtra("custom_txt",custom_txt);
        // fill in the same pending intent as when setting it
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Log.d(TAG, "cancelReminder for ID "+ id);
        // Alarm manager cancel the reminder
        am.cancel(pi);
    }

    /**
     *
     * This method is used to call to reset the repeat the habit.
     *
     * */
    public void cancelRepeatingHabit() {
        Log.d(TAG, "cancelRepeatingHabit: ");
        int id = 873162723;
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.setAction("RepeatingHabit");
        intent.putExtra("id", id);
        // This initialise the pending intent which will be sent to the broadcastReceiver
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        am.cancel(pi);
    }

    /**
     *
     * Function is used to cancel all alarms of task
     *
     * */
    public void cancelScheduleAlarms()
    {
        Log.v(TAG, "cancelSchedule alarms: ");

        scheduleList = scheduleDBHelper.getAllSchedule();
        uniqueIDList = new ArrayList<>();

        for (Schedule schedule : scheduleList)
        {
            int unique = schedule.getUnique();
            uniqueIDList.add(unique);
        }

        for (Integer uniqueID : uniqueIDList)
        {
            Log.v(TAG, "Removing alarm: " + uniqueID);
            Intent intent = new Intent(getApplicationContext(), CardNotification.class);
            intent.setAction("CardNotification");
            intent.putExtra("TaskID", "Null");
            intent.putExtra("CardName", "Null");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), uniqueID, intent, 0);
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            am.cancel(pendingIntent);
        }


    }

    /**
     *
     * Method t make a toast
     *
     * */
    public void MakeToast(String info)
    {
        Toast toast = Toast.makeText(ProfileActivity.this, info, Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.GRAY);
        TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);
        toast.show();
    }

    /**
     *
     * Opens up report dialog
     *
     * */
    public void reportAProblem()
    {
        ReportDialog reportDialog = new ReportDialog();
        reportDialog.show(getSupportFragmentManager(), "Report a problem dialog");
    }

    /**
     *
     * Change profile picture based on image selected
     * and uploads it to firebase
     * */
    public void changeProfilePicture()
    {
        CropImage.activity().setAspectRatio(1, 1).start(ProfileActivity.this);
        uploadProfileImage();
    }

    /**
     *
     * Gets user info from firebase to set the profile picture if it exists
     *
     * */
    public void getUserInfo()
    {
        storageProfilePicture.child(mAuth.getCurrentUser().getUid() + ".jpg").getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    Picasso.get().load(task.getResult()).into(profileImageView);
                }
            }
        });
    }

    /**
     * To set the bottom nav to listen to item changes
     * and chose the item that have been selected
     *
     * @param bottomNavigationView The botomNav that needs to be set to listen
     */
    private void bottomNavInit(BottomNavigationView bottomNavigationView) {

        //To have the highlight
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(4);
        menuItem.setChecked(true);

        //To set setOnNavigationItemSelectedListener
        NavBarHelper navBarHelper = new NavBarHelper(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(navBarHelper);
    }

    public void removeHabitEventListener(){
        String UID = mAuth.getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(UID);
        if (habitListener != null){
            Log.d(TAG, "removeHabitEventListener: habit");
            ref.child("habit").removeEventListener(habitListener);
            habitListener = null;
        }
        if (hrListener != null){
            Log.d(TAG, "removeHabitEventListener: habitRepetition");
            ref.child("habitRepetition").removeEventListener(hrListener);
            hrListener = null;
        }
        if (hgListener != null){
            Log.d(TAG, "removeHabitEventListener: habitGroup");
            ref.child("habitGroup").removeEventListener(hgListener);
            hgListener = null;

        }

    }
}

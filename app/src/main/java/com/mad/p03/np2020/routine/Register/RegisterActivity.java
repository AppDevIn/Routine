package com.mad.p03.np2020.routine.Register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.animation.ObjectAnimator;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.nfc.FormatException;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.mad.p03.np2020.routine.LoginActivity;
import com.mad.p03.np2020.routine.models.User;

import com.mad.p03.np2020.routine.Home.Home;
import com.mad.p03.np2020.routine.Register.models.OnFirebaseAuth;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.Register.models.RegisterFirebaseUser;
import com.mad.p03.np2020.routine.DAL.UserDBHelper;


/**
 *
 * This is controller that glues the activity_register together
 *
 * @author Jeyavishnu
 * @since 06-06-2020
 */
public class RegisterActivity extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnClickListener, OnFirebaseAuth {

    //Declare Constants
    final static String TAG = "Register";


    //Declare member variables
    User mUser;
    TextView mTxtErrorName, mTxtErrorEmail, mTxtErrorPassword;
    ProgressDialog progressDialog;

    /**
     *
     * This is used to get the ID of for the view and initialize the recycler
     * view for the tasks. Setting the onclick lister and onEditorLister too
     *
     * @param savedInstanceState will be null at first as
     *                           the orientation changes it will get
     *                           in use
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "UI is being created");


        //Init User
        mUser = new User();

        //ID for the error message
        mTxtErrorName = findViewById(R.id.errorName);
        mTxtErrorEmail = findViewById(R.id.errorEmail);
        mTxtErrorPassword = findViewById(R.id.errorPwd);


        //Find ID for Input information
        EditText edName, edEmail, edPassword;
        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);

        //ID for the animation
        TextView txtTypeWriter = findViewById(R.id.txtTypeWriter);
        ImageView imageView = findViewById(R.id.imageView);

        //ID for the button
        Button btnRegister = findViewById(R.id.buttonRegister);

        //Doing the start animation
        startUpAnimation(txtTypeWriter, imageView);
        
        //Setting On Editor listener
        edName.setOnEditorActionListener(this);
        edEmail.setOnEditorActionListener(this);
        edPassword.setOnEditorActionListener(this);

        //Listening the register button click
        btnRegister.setOnClickListener(this);



    }

    /**
     * Used when the GUI is ready the screen is set full screen
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: GUI is ready");


        //To set to Full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }


    /**
     * Not implemented
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: GUI is in the Foreground and Interactive");
    }

    /**
     * Not implemented
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity not in foreground");
    }

    /**
     * When this activity activates the process to get destroyed
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: The activity is no longer visible");
        finish();

    }

    /**
     * Not implemented
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity no longer exists");
    }

    /**
     *
     * The action is being performed on the keyboard
     * when the the Enter key is pressed check if the
     * view that was entered has followed the right format
     *
     * @param textView The view that was clicked.
     * @param actionId  Identifier of the action. This will be either the identifier you supplied, or
     *                  EditorInfo#IME_NULL if being called due to the enter key being pressed.
     * @param keyEvent  If triggered by an enter key, this is the event; otherwise, this is null.
     * @return Return true if you have consumed the action, else false.
     */
    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                        keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            switch (textView.getId()){
                case R.id.edName:nameCheck(textView); break;
                case R.id.edEmail: emailCheck(textView); break;
                case R.id.edPassword: passwordCheck(textView); break;
            }
        }

        return false;
    }

    /**
     *
     * Starts the registration process when clicked and
     * also check if all the input are in the right format
     *
     * @param view The view that being clicked
     */
    @Override
    public void onClick(View view) {

        Log.d(TAG, "onClick(): Register has been clicked");

        boolean isEmailAllowed = emailCheck(findViewById(R.id.edEmail));
        boolean isPasswordAllowed = passwordCheck(findViewById(R.id.edPassword));
        boolean isNameAllowed = nameCheck(findViewById(R.id.edName));


        if(isNameAllowed && isEmailAllowed && isPasswordAllowed){
            RegisterFirebaseUser registerFirebaseUser = new RegisterFirebaseUser(
                    this,
                    this,
                    mUser.getEmailAdd(),
                    mUser.getPassword()
                    );
            
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Registration of student");
            progressDialog.setMessage("Please wait, while we are register you 😅");
            progressDialog.show();

            registerFirebaseUser.execute();
        }
    }

    /**
     * When the user is successfully registered I save the information into
     * firebase and sql and move the home layout
     */
    @Override
    public void OnSignUpSuccess(RegisterFirebaseUser user) {
        Log.d(TAG, mUser.getEmailAdd() + " is successfully created");

        progressDialog.dismiss();


        FirebaseAuth auth = FirebaseAuth.getInstance();

        //Store the current user details
        mUser.setAuth(auth.getCurrentUser());

        Log.i(TAG, "onComplete: The current user's email is " + mUser.getAuth().getEmail());

        //Save data
        mUser.executeFirebaseUserUpload();

        //SQL
        //instantiate UserDb
        UserDBHelper mUserDBHelper = new UserDBHelper(RegisterActivity.this);
        mUserDBHelper.insertUser(mUser);

        //Send verification
        user.sendEmailVerification(auth, this);

        //Move to another activity
        moveToHome();
    }

    /**
     * When the firebase fails to register the user
     * @param e The error
     */
    @Override
    public void OnSignUpFailure(Exception e) {
        Log.d(TAG, mUser.getEmailAdd() + " is unsuccessfully");
        Log.e(TAG, "Firebase error: " + e.getLocalizedMessage());

        progressDialog.dismiss();

        //Show error dialog to the user and move the email section
        firebaseFailedError(e.getLocalizedMessage());

    }

    /**
     *
     * This method is used to check if the email
     * is in the right format
     *
     * @param textView The textview that needs be checked
     * @return bool if the format is right it will true else false
     */
    private boolean emailCheck(TextView textView){


        try {
            mUser.setEmailAdd(textView.getText().toString().trim());
            return true;
        } catch (FormatException e) {

            textView.setError(e.getLocalizedMessage());
            e.printStackTrace();
            Log.e(TAG, "emailCheck: " + e.getLocalizedMessage());
            return false;
        }
    }


    /**
     *
     * This method is used to check if the name
     * is in the right format
     *
     * @param textView The textview that needs be checked
     * @return bool if the format is right it will true else false
     */
    private boolean nameCheck(TextView textView){

        if(textView.getText().equals("")){

            textView.setError("Name is empty");
        }

        try {
            mUser.setName(textView.getText().toString().trim());
            return true;
        } catch (FormatException e) {
            textView.setError(e.getLocalizedMessage());
            e.printStackTrace();
            Log.e(TAG, "nameCheck: " + e.getLocalizedMessage());
            return false;
        }
    }

    /**
     *
     * This method is used to check if the password
     * is in the right format
     *
     * @param textView The textview that needs be checked
     * @return bool if the format is right it will true else false
     */
    private boolean passwordCheck(TextView textView){

        try {
            mUser.setPassword(textView.getText().toString().trim());

            return true;
        } catch (FormatException e) {

            textView.setError(e.getLocalizedMessage());
            e.printStackTrace();
            Log.e(TAG, "passwordCheck: " + e.getLocalizedMessage());
            return false;
        }
    }



    /**
     *
     * This is the start up animation are done
     * like typewriter effect and up animation on the view
     * with our logo
     *
     * @param textView The text the typewriter animation is implemented
     * @param upView The view where the animation done
     */
    private void startUpAnimation(final TextView textView, View upView){

        Log.d(TAG, "startUpAnimation(): Starting up animation");

        ObjectAnimator animation = ObjectAnimator.ofFloat(upView, "translationY",100f ,-100f);
        animation.setDuration(2000);
        animation.start();


        //Incase any error occur

            //Add typewriter effect to the textview

            new CountDownTimer(1800, 200) {
            int i = 0;
            public void onFinish() {
                // When timer is finished
                // Execute your code here'
                Log.d(TAG, "onFinish: Done with Register animation");
                textView.setText(R.string.register);

            }

            public void onTick(long millisUntilFinished) {
                // millisUntilFinished    The amount of time until finished.
                try {
                    String title = "Register";
                    textView.append(title.substring(i, i + 1));
                    i++;
                }
                catch (Exception e){

                    textView.setText(R.string.register);
                   this.cancel();
                }

            }
        }.start();
    }



    /**
     * Move to the home page
     */
    private void moveToHome(){
        Intent homePage = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(homePage);
    }

    private void sendEmailVerification(FirebaseAuth auth){
        String email = auth.getCurrentUser().getEmail();
        //Send email verification
        auth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(RegisterActivity.this, "Verification email sent to " + email , Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                sendEmailVerification(auth);
            }
        });
    }


    /**
     * Will show a error message to the user
     *
     * Upon pressing move the email
     * Reason email is because the error is likely to be a firebase error
     * Which is either password or email going to the email since is before the password
     *
     * @param errorMessage String The message that you to display
     *
     */
    private void firebaseFailedError(String errorMessage){
        Log.d(TAG, "firebaseFailedError: Alert dialog being created");

        //Show error dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

        //Setting content into the builder
        builder.setTitle("Firebase error");
        builder.setMessage(errorMessage);
        builder.setIcon(R.drawable.ic_error_black_24dp);
        builder.setCancelable(false); //To prevent people from exiting without clicking the okay button

        //To go the email
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                /*Not Implemented*/
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        Log.d(TAG, "firebaseFailedError: Alert dialog being Showed");

    }





}



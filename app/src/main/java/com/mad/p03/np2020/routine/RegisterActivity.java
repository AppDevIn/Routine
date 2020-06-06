package com.mad.p03.np2020.routine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.animation.ObjectAnimator;


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


import com.google.firebase.auth.FirebaseAuth;
import com.mad.p03.np2020.routine.Class.User;

import com.mad.p03.np2020.routine.Interface.OnFirebaseAuth;
import com.mad.p03.np2020.routine.background.RegisterFirebaseUser;
import com.mad.p03.np2020.routine.database.UserDBHelper;



public class RegisterActivity extends AppCompatActivity implements TextView.OnEditorActionListener, View.OnClickListener, OnFirebaseAuth {

    //Declare Constants
    final static String TAG = "Register";


    //Declare member variables
    User mUser;
    TextView mTxtErrorName, mTxtErrorEmail, mTxtErrorPassword;


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

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: GUI is ready");


        //To set to Full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: GUI is in the Foreground and Interactive");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity not in foreground");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: The activity is no longer visible");
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity no longer exists");
    }


    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                        keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
            switch (textView.getId()){
                case R.id.edName:nameCheck(textView); break;
                case R.id.edEmail: emailCheck(textView); break;
                case R.id.edPassword: passwordCheck(textView); break;
            }
        }

        return false;
    }

    @Override
    public void onClick(View view) {

        Log.d(TAG, "onClick(): Register has been clicked");

        boolean isEmailAllowed = emailCheck((TextView) findViewById(R.id.edEmail));
        boolean isPasswordAllowed = passwordCheck((TextView) findViewById(R.id.edPassword));
        boolean isNameAllowed = nameCheck((TextView) findViewById(R.id.edName));

        if(isNameAllowed && isEmailAllowed && isPasswordAllowed){
            RegisterFirebaseUser registerFirebaseUser = new RegisterFirebaseUser(
                    this,
                    this,
                    mUser.getEmailAdd(),
                    mUser.getPassword());

            registerFirebaseUser.execute();
        }
    }

    @Override
    public void OnSignUpSuccess() {
        Log.d(TAG, mUser.getEmailAdd() + " is successfully created");

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

        //Move to another activity
        moveToHome();
    }

    @Override
    public void OnSignUpFailure(Exception e) {
        Log.d(TAG, mUser.getEmailAdd() + " is unsuccessfully");
        Log.e(TAG, "Firebase error: " + e.getLocalizedMessage());

        //Show error dialog to the user and move the email section
        firebaseFailedError(e.getLocalizedMessage());
    }

    private boolean emailCheck(TextView textView){


        try {
            mUser.setEmailAdd(textView.getText().toString().trim());
            mTxtErrorEmail.setVisibility(View.INVISIBLE);
            return true;
        } catch (FormatException e) {
            mTxtErrorEmail.setVisibility(View.VISIBLE);
            mTxtErrorEmail.setText(e.getLocalizedMessage());
            e.printStackTrace();
            Log.e(TAG, "emailCheck: " + e.getLocalizedMessage());
            return false;
        }
    }


    private boolean nameCheck(TextView textView){

        if(textView.getText().equals("")){
            mTxtErrorName.setVisibility(View.VISIBLE);
        }

        try {
            mUser.setName(textView.getText().toString().trim());
            mTxtErrorName.setVisibility(View.INVISIBLE);
            return true;
        } catch (FormatException e) {
            mTxtErrorName.setVisibility(View.VISIBLE);
            mTxtErrorName.setText(e.getLocalizedMessage());
            e.printStackTrace();
            Log.e(TAG, "nameCheck: " + e.getLocalizedMessage());
            return false;
        }
    }

    private boolean passwordCheck(TextView textView){

        try {
            mUser.setPassword(textView.getText().toString().trim());
            mTxtErrorPassword.setVisibility(View.INVISIBLE);
            return true;
        } catch (FormatException e) {
            mTxtErrorPassword.setVisibility(View.VISIBLE);
            mTxtErrorPassword.setText(e.getLocalizedMessage());
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

            }

            public void onTick(long millisUntilFinished) {
                // millisUntilFinished    The amount of time until finished.
                try {
                    String title = "Register";
                    textView.append(title.substring(i, i + 1));
                    i++;
                }
                catch (Exception e){
                    Log.e(TAG, "startUpAnimation: ", e);

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
        Intent homePage = new Intent(RegisterActivity.this, Home.class);
        startActivity(homePage);
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
                //TODO
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        Log.d(TAG, "firebaseFailedError: Alert dialog being Showed");

    }





}



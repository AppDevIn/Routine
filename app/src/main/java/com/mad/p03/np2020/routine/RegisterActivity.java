package com.mad.p03.np2020.routine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.mad.p03.np2020.routine.Class.User;

import com.mad.p03.np2020.routine.database.UserDBHelper;



public class RegisterActivity extends AppCompatActivity {

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


        //ID for the error message
        mTxtErrorName = findViewById(R.id.errorName);
        mTxtErrorEmail = findViewById(R.id.errorEmail);
        mTxtErrorPassword = findViewById(R.id.errorPwd);


        //ID for the animatio
        TextView txtTypeWriter = findViewById(R.id.txtTypeWriter);
        ImageView imageView = findViewById(R.id.imageView);

//        //Find IDs
//        mSubmit = findViewById(R.id.btnSubmit);
//        mTxtQuestion = findViewById(R.id.txtQuestion);
//        mEdInput = findViewById(R.id.input);
//        mBack = findViewById(R.id.btnBack);
//        mTxtErrorMessage = findViewById(R.id.txtError);
//
//        //Create a User object
//        mUser = new User();


        startUpAnimation(txtTypeWriter, imageView);

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

        //TODO: Put shared preference
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: The activity is no longer visible");

        //TODO: Put shared preference

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity no longer exists");
    }


    private void startRegistration(){

        Log.d(TAG, "Start to register ");

        //Register the user in firebase and run in background
        RequestFirebase requestFirebase= new RequestFirebase();
        requestFirebase.execute();


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


        ObjectAnimator animation = ObjectAnimator.ofFloat(upView, "translationY",100f ,-100f);
        animation.setDuration(2000);
        animation.start();


        //Add typewriter effect to the textview
        final CountDownTimer countDownTimer = new CountDownTimer(1800, 200) {
            int i = 0;
            public void onFinish() {
                // When timer is finished
                // Execute your code here'
                Log.d(TAG, "onFinish: Done with Register animation");

            }

            public void onTick(long millisUntilFinished) {
                // millisUntilFinished    The amount of time until finished.
                String title = "Register";
                textView.append(title.substring(i,i+1));
                i++;

            }
        }.start();
    }



    /**
     * Class to process the request to firebase
     * in the background
     */
    @SuppressLint("StaticFieldLeak")
    public class RequestFirebase extends AsyncTask<Void, Void, Void>{
        FirebaseAuth auth = FirebaseAuth.getInstance();
        @Override
        protected Void doInBackground(Void... voids) {
            auth.createUserWithEmailAndPassword(mUser.getEmailAdd(), mUser.getPassword())
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() { // Check if the process is completed
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "Firebase password auth is been completed");

                            //Check if is successful
                            if(task.isSuccessful()){
                                Log.d(TAG, mUser.getEmailAdd() + " is successfully created");

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

                        }
                    }).addOnFailureListener(new OnFailureListener() { //If firebase fail to create
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, mUser.getEmailAdd() + " is unsuccessfully");
                    Log.e(TAG, "Firebase error: " + e.getLocalizedMessage() );

                    //Show error dialog to the user and move the email section
                    firebaseFailedError(e.getLocalizedMessage());
                }
            });
            return null;
        }

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



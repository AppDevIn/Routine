package com.mad.p03.np2020.routine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.nfc.FormatException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.p03.np2020.routine.Class.User;
import com.mad.p03.np2020.routine.database.UserDBHelper;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    //Declare Constants
    final static String TAG = "Register";


    //Declare member variables
    Button mSubmit, mBack;
    int mProgessCount = 0;
    TextView mTxtQuestion, mTxtErrorMessage;
    EditText mEdInput;
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "UI is being created");

        //Find IDs
        mSubmit = findViewById(R.id.btnSubmit);
        mTxtQuestion = findViewById(R.id.txtQuestion);
        mEdInput = findViewById(R.id.input);
        mBack = findViewById(R.id.btnBack);
        mTxtErrorMessage = findViewById(R.id.txtError);

        //Create a User object
        mUser = new User();




    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: GUI is ready");

        //Default Update before the click
        starterUI();

        //change Background olor of button transparent
        mSubmit.setBackgroundColor(Color.TRANSPARENT);

    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: GUI is in the Foreground and Interactive");

        //Listener to the submit button
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCardView(false);
            }
        });




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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity no longer exists");
    }

    //UI used when the layout is foreground
    private void starterUI(){
        mProgessCount = 1;
        askName();
    }

    private void updateCardView(Boolean isForward){

        Log.d(TAG, "UI updated");

        if (isForward) {
            mProgessCount++;
        } else {
            mProgessCount--;
        }

        switch (mProgessCount){
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
                askDateOfBirth();
                break;
            case 5:
                startRegistration();
                break;
            default:
                break;
        }
    }



    private void updateUI(String name, TextView textView){
        Log.d(TAG, name + ": " + textView.getText().toString());

        //Move the next one
        updateCardView(true);

        //Make the text invisible
        toggleError();
    }

    /**
     * show the error text view and make it visible
     *
     * @param localizedMessage is the String provided from the user class
     *
     */
    private void toggleError(String localizedMessage){

        Log.d(TAG, "Showing error message");
        mTxtErrorMessage.setVisibility(View.VISIBLE);
        //Error message for empty text
        Log.e(TAG, "toggleError: Condition failed: " + localizedMessage);
        mTxtErrorMessage.setText(localizedMessage);

    }

    /**
     * Make the text invisible
     *
     */
    private void toggleError(){
        if(mTxtErrorMessage.getVisibility() == View.VISIBLE){
            Log.d(TAG, "Error text disappear");
            mTxtErrorMessage.setVisibility(View.INVISIBLE);
        }
    }





    //Changes the cardview details to name
    private void askName() {
        Log.d(TAG, "Asking for name");

        //Show previous data if exist
        if(mUser.getName() != null){
            mEdInput.setText(mUser.getName());

            Log.d(TAG, "Existing value, Name: " + mUser.getName());

        }else {
            Log.d(TAG, "No Existing value, Name value");
            mEdInput.setText("");

        }

        //Ask for the name
        mTxtQuestion.setText(R.string.registerName);

        //Invisible is because going back is not possible
        mBack.setVisibility(View.INVISIBLE);

        //Check for input and ensure the string is not empty
        mEdInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Log.i(TAG, "onClick: " + textView.getText().toString());

                try {
                    mUser.setName(textView.getText().toString());// Make the error text invisible move the next question
                    updateUI("Name", textView);
                } catch (FormatException e) {
                    e.printStackTrace();
                    toggleError(e.getLocalizedMessage());//To show Error message
                }

                return false;
            }
        });

        //Setting a listener for the button
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: " + mEdInput.getText().toString());

                try {
                    mUser.setName(mEdInput.getText().toString());// Make the error text invisible move the next question
                    updateUI("Name", mEdInput);
                } catch (FormatException e) {
                    e.printStackTrace();
                    toggleError(e.getLocalizedMessage());//To show Error message
                }
            }
        });
    }

    //Changes the cardview details to emial
    private void askEmail() {
        Log.d(TAG, "Ask for Email");

        //Run the default ask
        //Show previous data if exist
        if(mUser.getEmailAdd() != null){
            mEdInput.setText(mUser.getEmailAdd());

            Log.d(TAG, "Existing value, Name: " + mUser.getEmailAdd());

        }else {
            Log.d(TAG, "No Existing value, Name value");
            mEdInput.setText("");

        }

        //Ask for the email
        mTxtQuestion.setText(R.string.registerEmail);

        //Visible as going is possible
        mBack.setVisibility(View.VISIBLE);

        //Check for input and ensure the string is not empty and is email
        mEdInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                try {
                    mUser.setEmailAdd(textView.getText().toString().trim());
                    updateUI("Email", textView); // Make the error text invisible move the next question
                } catch (FormatException e) {
                    e.printStackTrace();
                    toggleError(e.getLocalizedMessage());//To show Error message
                }
                return false;
            }

        });

        //Setting a listener for the button
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mUser.setEmailAdd(mEdInput.getText().toString().trim());
                    updateUI("Email", mEdInput); // Make the error text invisible move the next question
                } catch (FormatException e) {
                    e.printStackTrace();
                    toggleError(e.getLocalizedMessage());//To show Error message
                }
            }
        });
    }

    //Changes the cardview details to Password
    private void askPassword() {
        Log.d(TAG, "Ask for password");

        //Run the default ask
        //Show previous data if exist
        if(mUser.getPassword() != null){
            mEdInput.setText(mUser.getPassword());

            Log.d(TAG, "Existing value, Name: " + mUser.getPassword());

        }else {
            Log.d(TAG, "No Existing value, Name value");
            mEdInput.setText("");

        }

        //Set the the input the dots
        mEdInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        //Aak for password
        mTxtQuestion.setText(R.string.registerPassword);

        //TODO: Set dot as the password

        //Check for input and ensure the string is not empty
        mEdInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                try {
                    mUser.setPassword(textView.getText().toString().trim());
                    updateUI("Password", textView); // Make the error text invisible move the next question
                } catch (FormatException e) {
                    e.printStackTrace();
                    toggleError(e.getLocalizedMessage()); //To show Error message
                }

                return false;
            }
        });

        //Setting a listener for the button
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mUser.setPassword(mEdInput.getText().toString().trim());
                    updateUI("Password", mEdInput); // Make the error text invisible move the next question
                } catch (FormatException e) {
                    e.printStackTrace();
                    toggleError(e.getLocalizedMessage()); //To show Error message
                }

            }
        });
    }

    //Changes the cardview details to Confirm Password
    private void askDateOfBirth() {
        Log.d(TAG, "Ask for DOB");

        //Run the default ask
        //Show previous data if exist
        if(mUser.getDateOfBirth() != null){
            mEdInput.setText(mUser.getDateOfBirth().toString());

            Log.d(TAG, "Existing value, Name: " + mUser.getDateOfBirth());

        }else {
            Log.d(TAG, "No Existing value, Name value");
            mEdInput.setText("");

        }

        //Ask for date for birth
        mTxtQuestion.setText(R.string.registerDOB);

        //Give hint the the user on the format
        mEdInput.setHint(R.string.dateFormat);


        //Check for input and ensure the string is not empty
        mEdInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                try {
                    mUser.setDateOfBirth(textView.getText().toString().trim());
                    updateUI("DOB", textView);// Make the error text invisible move the next question
                } catch (FormatException e) {
                    e.printStackTrace();
                    toggleError(e.getLocalizedMessage()); //To show Error message
                }

                return false;
            }
        });

        //Setting a listener for the button
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    mUser.setDateOfBirth(mEdInput.getText().toString().trim());
                    updateUI("DOB", mEdInput);// Make the error text invisible move the next question
                } catch (FormatException e) {
                    e.printStackTrace();
                    toggleError(e.getLocalizedMessage()); //To show Error message
                }

            }
        });

    }

    private void startRegistration(){

        Log.d(TAG, "Start to register ");

        //Register the user in firebase and run in background
        RequestFirebase requestFirebase= new RequestFirebase();
        requestFirebase.execute();


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

                                //Firebase
                                SaveFirebaseTask saveFirebaseTask = new SaveFirebaseTask();
                                saveFirebaseTask.execute();

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
        //Change the progress to 0
        mProgessCount = 2; //To ensure it start from the email

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
                //goes back to the start(Email)
                askEmail();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        Log.d(TAG, "firebaseFailedError: Alert dialog being Showed");

    }


    /**
     * Upload Name, Email, DOB up the firebase
     * After registering successfully
     *
     * It will be done in the background
     */
    @SuppressLint("StaticFieldLeak")
    private class SaveFirebaseTask extends AsyncTask<Void, Void, Void>{

        DatabaseReference mDatabase;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "onPreExecute(): Creating database reference");
            //Getting a database reference to Users
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUID());
        }

        @Override
        protected Void doInBackground(Void... voids) {

            //Setting data into the user portion
            mDatabase.child("Name").setValue(mUser.getName()); //Setting the name
            mDatabase.child("Email").setValue(mUser.getEmailAdd()); //Setting the Email
            mDatabase.child("DOB").setValue("08/10/2000"); //Setting the DOB

            Log.i(TAG, "doInBackground(): Name, Email and DOB are uploaded");

            return null;
        }
    }







}



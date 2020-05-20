package com.mad.p03.np2020.routine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mad.p03.np2020.routine.Class.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    //Declare Constants
    final static String TAG = "Register";
    final String EMAILPATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    final String STRONGPASSWORD = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
    final String DOBPATTERN = "[0-9]+[0-9]+/+[0-9]+[0-9]+/[0-9]+[0-9][0-9]+[0-9]";


    //Declare member variables
    Button mSubmit, mBack;
    int mProgessCount = 0;
    TextView mTxtQuestion, mTxtErrorMessage;
    EditText mEdInput;
    HashMap<String, String> mRegisterMap = new HashMap<>();
    ProgressBar mProgressBar;
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
        mProgressBar = findViewById(R.id.progressBar);
        mBack = findViewById(R.id.btnBack);
        mTxtErrorMessage = findViewById(R.id.txtError);

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


    private void askDefault(String name){
        //Show previous data if exist
        if(mRegisterMap.get(name) != null){
            mEdInput.setText(mRegisterMap.get(name));

            Log.d(TAG, "Existing value, " + name + ": " + mRegisterMap.get(name));

        }else {
            Log.d(TAG, "No Existing value, " + name + " value");
            mEdInput.setText("");

        }

        //Change the button Name
        //mSubmit.setText(R.string.submit);


    }

    private void updateUI(String name, TextView textView){
        Log.d(TAG, name + ": " + textView.getText().toString());


        //Save the data
        mRegisterMap.put(name, textView.getText().toString());

        //Move the next one
        updateCardView(true);

        //Make the text invisible
        toggleError();
    }

    /**
     * show the error text view and make it visible
     *
     * @param resId is the String in strings.xml to show the error message
     *
     */
    private void toggleError(int resId){

        Log.d(TAG, "Showing error message");
        mTxtErrorMessage.setVisibility(View.VISIBLE);
        //Error message for empty text
        mTxtErrorMessage.setText(resId);

    }

    /**
     * Make the text invisible
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

        //Run the default ask
        askDefault("Name");

        //Ask for the name
        mTxtQuestion.setText(R.string.registerName);

        //Invisible is because going back is not possible
        mBack.setVisibility(View.INVISIBLE);

        //Check for input and ensure the string is not empty
        mEdInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Log.i(TAG, "onClick: " + textView.getText().toString());
                if(!textView.getText().toString().isEmpty()){
                    updateUI("Name", textView);
                }else {
                    Log.e(TAG, "Condition failed: Text is empty");

                    //Error message for empty text
                    toggleError(R.string.empty_text_error);
                }

                return false;
            }
        });

        //Setting a listener for the button
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: " + mEdInput.getText().toString());
                if(!mEdInput.getText().toString().isEmpty()){
                    updateUI("Name", mEdInput);
                }else {
                    Log.e(TAG, "Condition failed: Text is empty");

                    //Error message for empty text
                    toggleError(R.string.empty_text_error);
                }
            }
        });
    }

    //Changes the cardview details to emial
    private void askEmail() {
        Log.d(TAG, "Ask for Email");

        //Run the default ask
        askDefault("Email");

        //Ask for the email
        mTxtQuestion.setText(R.string.registerEmail);

        //Visible as going is possible
        mBack.setVisibility(View.VISIBLE);

        //Check for input and ensure the string is not empty and is email
        mEdInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {


                if(!textView.getText().toString().isEmpty()){
                    if(textView.getText().toString().trim().matches(EMAILPATTERN)) {
                        updateUI("Email", textView);
                    }else{
                        //Error message for email when .com and @ is not present
                        Log.e(TAG, "Condition failed: Text does not have @");
                        toggleError(R.string.email_text_error);
                    }
                }else {
                    Log.e(TAG, "Condition failed: Text is empty");

                    //Error message for empty text
                    toggleError(R.string.empty_text_error);
                }

                return false;
            }
        });

        //Setting a listener for the button
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mEdInput.getText().toString().isEmpty()){
                    if(mEdInput.getText().toString().trim().matches(EMAILPATTERN)) {
                        updateUI("Email", mEdInput);
                    }else{
                        //Error message for email when .com and @ is not present
                        Log.e(TAG, "Condition failed: Text does not have @");
                        toggleError(R.string.email_text_error);
                    }
                }else {
                    Log.e(TAG, "Condition failed: Text is empty");

                    //Error message for empty text
                    toggleError(R.string.empty_text_error);
                }
            }
        });
    }

    //Changes the cardview details to Password
    private void askPassword() {
        Log.d(TAG, "Ask for password");

        //Run the default ask
        askDefault("Password");

        //Set the the input the dots
        mEdInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        //Aak for password
        mTxtQuestion.setText(R.string.registerPassword);

        //TODO: Set dot as the password

        //Check for input and ensure the string is not empty
        mEdInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {


                if(!textView.getText().toString().isEmpty()){
                    if(textView.getText().toString().trim().matches(STRONGPASSWORD)) {
                        updateUI("Password", textView);
                    } else {


                        //a digit must occur at least once
                        //a lower case letter must occur at least once
                        //an upper case letter must occur at least once
                        //a special character must occur at least once
                        //    no whitespace allowed in the entire string
                        //anything, at least eight places though

                        Log.e(TAG, "Condition failed: Text doesn't meet strong password requirement");

                        //Error message for password when password doesn't
                        //have digit, lower and upper case, special character and min 8 letter
                        toggleError(R.string.pwd_strong_error);
                    }
                } else {
                    Log.e(TAG, "Condition failed: Text is empty");

                    //Error message for empty text
                    toggleError(R.string.empty_text_error);
                }

                return false;
            }
        });

        //Setting a listener for the button
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mEdInput.getText().toString().isEmpty()){
                    if(mEdInput.getText().toString().trim().matches(STRONGPASSWORD)) {
                        updateUI("Password", mEdInput);
                    } else {


                        //a digit must occur at least once
                        //a lower case letter must occur at least once
                        //an upper case letter must occur at least once
                        //a special character must occur at least once
                        //    no whitespace allowed in the entire string
                        //anything, at least eight places though

                        Log.e(TAG, "Condition failed: Text doesn't meet strong password requirement");

                        //Error message for password when password doesn't
                        //have digit, lower and upper case, special character and min 8 letter
                        toggleError(R.string.pwd_strong_error);
                    }
                } else {
                    Log.e(TAG, "Condition failed: Text is empty");

                    //Error message for empty text
                    toggleError(R.string.empty_text_error);
                }

            }
        });
    }

    //Changes the cardview details to Confirm Password
    private void askDateOfBirth() {
        Log.d(TAG, "Ask for DOB");

        //Run the default ask
        askDefault("DOB");

        //Ask for date for birth
        mTxtQuestion.setText(R.string.registerDOB);

        //Give hint the the user on the format
        mEdInput.setHint(R.string.dateFormat);


        //Check for input and ensure the string is not empty
        mEdInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {


                if(!textView.getText().toString().isEmpty()){
                    if(textView.getText().toString().trim().matches(DOBPATTERN)) {
                        updateUI("DOB", textView);
                    } else {
                        Log.d(TAG, "Condition failed: Text doesn't meet DOB (DD/MM/YYYY) requirement");

                        //Error message for DOB when it does match DD/MM/YYYY
                        toggleError(R.string.date_text_error);
                    }
                } else {
                    Log.e(TAG, "Condition failed: Text is empty");

                    //Error message for empty text
                    toggleError(R.string.empty_text_error);
                }

                return false;
            }
        });

        //Setting a listener for the button
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mEdInput.getText().toString().isEmpty()){
                    if(mEdInput.getText().toString().trim().matches(DOBPATTERN)) {
                        updateUI("DOB", mEdInput);
                    } else {
                        Log.e(TAG, "Condition failed: Text doesn't meet DOB (DD/MM/YYYY) requirement");

                        //Error message for DOB when it does match DD/MM/YYYY
                        toggleError(R.string.date_text_error);
                    }
                } else {
                    Log.e(TAG, "Condition failed: Text is empty");

                    //Error message for empty text
                    toggleError(R.string.empty_text_error);
                }


            }
        });

    }

    private void startRegistration(){

        Log.d(TAG, "Start to register ");
        Log.d(TAG, mRegisterMap.toString());


        //Get the date from String
        Date DOB = stringToDate(mRegisterMap.get("DOB"));

        //Create a User object
        mUser = new User();

        //Register the user in firebase and run in background
        RequestFirebase requestFirebase= new RequestFirebase();
        requestFirebase.execute();


    }

    /**
     * To convert string to date
     * The function has 2 possible returns
     *
     * if Successfully changed a Date object will be return
     * else null will be returned
     *
     * @param DOB is a String that is provided by the user
     */

    private Date stringToDate(String DOB){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyyy");
        try {
            return sdf.parse(DOB);
        } catch (ParseException ex) {
            Log.e("Exception", "Date unable to change reason: "+ ex.getLocalizedMessage());
            return null;
        }
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
            auth.createUserWithEmailAndPassword(mRegisterMap.get("Email"),mRegisterMap.get("Password"))
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() { // Check if the process is completed
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "Firebase password auth is been completed");

                            //Check if is successful
                            if(task.isSuccessful()){
                                Log.d(TAG, mUser.getEmailAddr() + " is successfully created");


                                //Move to another activity
                                moveToHome();

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() { //If firebase fail to create
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, mUser.getEmailAddr() + " is unsuccessfully");
                    Log.e(TAG, "Firebase error: " + e.getLocalizedMessage() );

                    //Show error dialog to the user and move the email section
                    firebaseFailedError(e.getLocalizedMessage());
                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            //Show the spinner
            loadSpinner(true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Don't show the spinner
            loadSpinner(false);
        }
    }

    /**
     * Toggle between with spinner
     * and without spinner
     *
     * @param isLoadSpinner is to check if they want the
     *                      to load.
     *                      Load the spinner : True
     *                      Spinner stay invisible : False
     *
     */
    private void loadSpinner(Boolean isLoadSpinner){

        if(isLoadSpinner){

            Log.d(TAG, "Hiding everything expect the spinner");

            //Invisible to prevent from distraction
            mEdInput.setVisibility(View.INVISIBLE);
            mSubmit.setVisibility(View.INVISIBLE);
            mTxtQuestion.setVisibility(View.INVISIBLE);

            //Visible to see the progress
            mProgressBar.setVisibility(View.VISIBLE);

            Log.d(TAG, "Spinner started");

        }
        else{


            Log.d(TAG, "Hiding the spinner");

            //Remove the progress to have better UI
            mProgressBar.setVisibility(View.INVISIBLE);

            Log.d(TAG, "Showing the views");

            //Visible to enter information
            mEdInput.setVisibility(View.VISIBLE);
            mSubmit.setVisibility(View.VISIBLE);
            mTxtQuestion.setVisibility(View.VISIBLE);


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
     * Upon pressibng move the email
     * Reason email is because the error is likely to be a firebase error
     * Which is either password or email going to the email since is before the password
     *
     */
    private void firebaseFailedError(String errorMessage){
        Log.d(TAG, "firebaseFailedError: Alert dialog being created");
        //Change the progress to 0
        mProgessCount = 0;

        //Show error dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

        //Setting content into the builder
        builder.setTitle("Firebase error");
        builder.setMessage(errorMessage);
        builder.setIcon(R.drawable.ic_error_black_24dp);

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





}



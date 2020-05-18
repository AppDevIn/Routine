package com.mad.p03.np2020.routine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.google.firebase.auth.FirebaseUser;
import com.mad.p03.np2020.routine.Class.Register;

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
    TextView mTxtQuestion;
    EditText mEdInput;
    HashMap<String, String> mRegisterMap = new HashMap<>();
    ProgressBar mProgressBar;
    Register mUser;

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

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: GUI is ready");

        //Default Update before the click
        starterUI();

    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: GUI is in the Foreground and Interactive");

        //Listener to the submit button
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCardView(true);
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCardView(false);
            }
        });


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
            case 1:
            default:
                askName();
                break;
        }
    }


    //Changes the cardview details to name
    private void askName() {
        Log.d(TAG, "Asking for name");

        //Show previous data if exist
        if(mRegisterMap.get("Name") != null){
            mEdInput.setText(mRegisterMap.get("Name"));

            Log.d(TAG, "Value exists, Name: " + mRegisterMap.get("Name"));

            //Enable the button
            mSubmit.setEnabled(true);
            Log.d(TAG,"Button enabled");


        }else {
            Log.d(TAG, "No Name value");
            mEdInput.setText("");
        }

        //Change the button Name
        mSubmit.setText(R.string.next);

        //Disable button until name has valid string
        mSubmit.setEnabled(false);

        //Ask for the name
        mTxtQuestion.setText(R.string.registerName);

        //Invisible is because going back is not possible
        mBack.setVisibility(View.INVISIBLE);

        //Check for input and ensure the string is not empty
        mEdInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {


                if(!textView.getText().toString().isEmpty()){
                    Log.d(TAG, "Name: " + textView.getText().toString());

                    mSubmit.setEnabled(true);
                    Log.d(TAG,"Button enabled");

                    //Save the data
                    mRegisterMap.put("Name", textView.getText().toString());

                    //Move the next one
                    updateCardView(true);
                }else {
                    Log.d(TAG, "Condition failed: Text is empty");

                    //TODO: Error message
                }

                return false;
            }
        });
    }

    //Changes the cardview details to emial
    private void askEmail() {
        Log.d(TAG, "Ask for Email");

        //Show previous data if exist
        if(mRegisterMap.get("Email") != null){
            mEdInput.setText(mRegisterMap.get("Email"));

            //Enable the button
            mSubmit.setEnabled(true);
            Log.d(TAG,"Button enabled");

            Log.d(TAG, "Value exists, Email: " + mRegisterMap.get("Email"));
        }else {
            Log.d(TAG, "No Email value");
            mEdInput.setText("");
        }

        //Change the button Name
        mSubmit.setText(R.string.next);

        //Disable button until name has valid string
        mSubmit.setEnabled(false);

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


                        Log.d(TAG, "Email: " + textView.getText().toString());

                        mSubmit.setEnabled(true);
                        Log.d(TAG, "Button enabled");

                        //Save the data
                        mRegisterMap.put("Email", textView.getText().toString());

                        //Move the next one
                        updateCardView(true);
                    }else{
                        //TODO: ERROR Message
                        Log.d(TAG, "Condition failed: Text does not have @");
                    }
                }else {
                    Log.d(TAG, "Condition failed: Text is empty");

                    //TODO: Error message
                }

                return false;
            }
        });

    }

    //Changes the cardview details to Password
    private void askPassword() {
        Log.d(TAG, "Ask for password");

        //Show previous data if exist
        if(mRegisterMap.get("Password") != null){
            mEdInput.setText(mRegisterMap.get("Password"));

            //Enable the button
            mSubmit.setEnabled(true);
            Log.d(TAG,"Button enabled");

            Log.d(TAG, "Value exists, Password: " + mRegisterMap.get("Password"));
        }else {
            Log.d(TAG, "No Password value");
            mEdInput.setText("");
        }

        //Change the button Name
        mSubmit.setText(R.string.next);

        //Disable button until Password has valid string
        mSubmit.setEnabled(false);

        //Set the the input the dots
        mEdInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

        //Aak for password
        mTxtQuestion.setText(R.string.registerPassword);

        //Check for input and ensure the string is not empty
        mEdInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {


                if(!textView.getText().toString().isEmpty()){
                    if(textView.getText().toString().trim().matches(STRONGPASSWORD)) {
                        Log.d(TAG, "Password: " + textView.getText().toString());

                        mSubmit.setEnabled(true);
                        Log.d(TAG, "Button enabled");

                        //Save the data
                        mRegisterMap.put("Password", textView.getText().toString());

                        //Move the next one
                        updateCardView(true);
                    } else {


                        //a digit must occur at least once
                        //a lower case letter must occur at least once
                        //an upper case letter must occur at least once
                        //a special character must occur at least once
                        //    no whitespace allowed in the entire string
                        //anything, at least eight places though

                        Log.d(TAG, "Condition failed: Text doesn't meet strong password requirement");

                        //TODO: Error message
                    }
                } else {
                    Log.d(TAG, "Condition failed: Text is empty");

                    //TODO: Error message
                }

                return false;
            }
        });
    }

    //Changes the cardview details to Confirm Password
    private void askDateOfBirth() {
        Log.d(TAG, "Ask for DOB");

        //Show previous data if exist
        if(mRegisterMap.get("DOB") != null){
            mEdInput.setText(mRegisterMap.get("DOB"));

            //Enable the button
            mSubmit.setEnabled(true);
            Log.d(TAG,"Button enabled");

            Log.d(TAG, "Value exists, DOB: " + mRegisterMap.get("DOB"));
        }else {
            Log.d(TAG, "No DOB value");
            mEdInput.setText("");
        }


        //Change the button Name
        mSubmit.setText(R.string.submit);

        //Disable button until Password has valid string
        mSubmit.setEnabled(false);

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
                        Log.d(TAG, "DOB: " + textView.getText().toString());

                        mSubmit.setEnabled(true);
                        Log.d(TAG, "Button enabled");

                        //Save the data
                        mRegisterMap.put("DOB", textView.getText().toString());

                        //Move the next one
                        updateCardView(true);

                    } else {


                        //a digit must occur at least once
                        //a lower case letter must occur at least once
                        //an upper case letter must occur at least once
                        //a special character must occur at least once
                        //    no whitespace allowed in the entire string
                        //anything, at least eight places though

                        Log.d(TAG, "Condition failed: Text doesn't meet DOB (DD/MM/YYYY) requirement");

                        //TODO: Error message
                    }
                } else {
                    Log.d(TAG, "Condition failed: Text is empty");

                    //TODO: Error message
                }

                return false;
            }
        });

    }
    
    private void startRegistration(){
        Log.d(TAG, "Start to register ");
        Log.d(TAG, mRegisterMap.toString());


        //Get the date from String
        Date DOB = stringToDate(mRegisterMap.get("DOB"));

        //Create a User object
        mUser = (Register) new Register(mRegisterMap.get("Name"), mRegisterMap.get("Password"), mRegisterMap.get("Email"), DOB );

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

    /**TODO
     * Class to process the request to firebase
     * in the backgrounf
     */

    @SuppressLint("StaticFieldLeak")
    public class RequestFirebase extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            mUser.createUser(RegisterActivity.this)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() { // Check if the process is completed
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "Firebase password auth is been completed");

                            //Check if is successful
                            if(task.isSuccessful()){
                                Log.d(TAG, mUser.getEmailAddr() + " is successfully created");

                                //TODO: Move to another activity

                            }else{
                                Log.d(TAG, mUser.getEmailAddr() + " is unsuccessfully");

                                //TODO: Do something

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() { //If firebase fail to create
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Firebase error: " + e.getLocalizedMessage() );
                    //TODO: Do something
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






}



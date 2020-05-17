package com.mad.p03.np2020.routine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;

public class Register extends AppCompatActivity {
    
    //Declare Constants
    final static String TAG = "Register";
    final String EMAILPATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    final String STRONGPASSWORD = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
    final String DOBPATTERN = "[0-9]+[0-9]+/+[0-9]+[0-9]+/[0-9]+[0-9][0-9]+[0-9]";


    //Declare member variables
    Button mSubmit;
    int mProgessCount = 0;
    TextView mTxtQuestion;
    EditText mEdInput;
    HashMap<String, String> mRegisterMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: Doing binding and startup logic");

        //Find IDs
        mSubmit = findViewById(R.id.btnSubmit);
        mTxtQuestion = findViewById(R.id.txtQuestion);
        mEdInput = findViewById(R.id.input);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: GUI is started ready to move to foreground");

        //Listener to the submit button
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCardView(true);
            }
        });

    }



    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: GUI is in the Foreground");

        //Default Update before the click
        starterUI();


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





    }


}



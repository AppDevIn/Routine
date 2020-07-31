package com.mad.p03.np2020.routine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.p03.np2020.routine.Home.Home;
import com.mad.p03.np2020.routine.Profile.ProfileActivity;
import com.mad.p03.np2020.routine.Register.RegisterActivity;
import com.mad.p03.np2020.routine.Register.models.RegisterFirebaseUser;
import com.mad.p03.np2020.routine.models.InternetStatus;
import com.mad.p03.np2020.routine.models.User;
import com.mad.p03.np2020.routine.DAL.UserDBHelper;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * Login activity used to manage the login layout section
 *
 * @author Lee Quan Sheng
 * @since 02-06-2020
 */


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {

    EditText et_Email, et_Password;
    Button btn_register;
    Button btn_login;
    FirebaseAuth mAuth;
    String email, password;
    TextView txtError;
    CheckBox checkBox;
    TextView errLogin, errPwd;

    String TAG = "LOGIN_ACITVITY";

    /**SharedPreference name for Remember Me*/
    public static final String SHARED_PREFS = "sharedPrefs";

    /**String for Email and password*/
    private String sUser, sPassword;

    /**String for username for SharedPreference*/
    public static final String Username = "username";

    /**String for password for SharedPreference*/
    public static final String Password = "password";

    /**String for switch for remember me checkbox*/
    public static final String SWITCH1 = "switch1";

    /**Boolean for checkbox for Remember Me*/
    private boolean switchOnOff;

    /**Firebase*/
    private DatabaseReference myRef;

    /**UserDBHelper for local database*/
    private UserDBHelper userDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //Database has to be declared before login check
        userDatabase = new UserDBHelper(this);

        //Used to check login
        try {
            CheckLoggedIn();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "user at login page");
        mAuth = FirebaseAuth.getInstance();

        et_Email = findViewById(R.id.editEmail);
        et_Password = findViewById(R.id.editPassword);
        btn_login = findViewById(R.id.buttonLogin);
        btn_register = findViewById(R.id.buttonRegister);
        txtError = findViewById(R.id.txtError);
        checkBox = findViewById(R.id.rememberMe);
        errLogin = findViewById(R.id.errorEmail);
        errPwd = findViewById(R.id.errorPwd);
        TextView textView = findViewById(R.id.txtReset);


        et_Email.setOnClickListener(this);
        et_Password.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        et_Email.setOnFocusChangeListener(this);
        et_Password.setOnFocusChangeListener(this);

        et_Email.setOnKeyListener(this);
        et_Password.setOnKeyListener(this);
        checkBox.setOnKeyListener(this);
        loadData();
        updateViews();

        errLogin.setVisibility(View.INVISIBLE);
        errPwd.setVisibility(View.INVISIBLE);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomPasswordDialog();
            }
        });
        et_Password.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                HideKeyboard(et_Password);
                Login();
                return true;
            }
            return false;
        });

    }

    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     *
     * Onclick listener
     *
     * */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editEmail: //Show Keyboard on click Email
                ShowKeyboard(et_Email);
                break;
            case R.id.editPassword: //Show Keyboard on click Password
                ShowKeyboard(et_Password);
                break;
            case R.id.buttonLogin: //Login Button
                Login();

                break;
            case R.id.buttonRegister: //Registration Button
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    public void Login(){
        if (!TextUtils.isEmpty(et_Email.getText().toString()) & !TextUtils.isEmpty(et_Password.getText().toString())) {
            if (InternetStatus.getInstance(getApplicationContext()).isOnline()) {
                Log.i(TAG, "User is online");
                email = et_Email.getText().toString();
                password = et_Password.getText().toString();
                firebaseAuthWithGoogle(email, password, this);
            } else {
                Log.e(TAG, "User does not have an internet connection");
                txtError.setText("Please Check Your Network Connectivity");
                txtError.setVisibility(View.VISIBLE);
            }
        } else {
            errLogin.setVisibility(View.VISIBLE);
            errPwd.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                et_Email.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                et_Password.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            }
        }
    }

    /**
     *
     * onFocusChange event listener
     * Track the Focus so that if user click outside the keyboard area, the keyboard will hide
     *
     *
     * @param v set view to this content
     * @param hasFocus set hasFocus to this content
     * */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.editEmail:
                if (!hasFocus) {
                    HideKeyboard(et_Email);
                }
                break;
            case R.id.editPassword:
                if (!hasFocus) {
                    HideKeyboard(et_Password);
                }
                break;
        }
    }

    /**
     *
     * onKey keyboard event listener
     *
     * If user clicks enter the keyboard will hide
     *
     * */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        return false;
    }

    /**
     *
     * this is called the first time a database is accessed. Creation a new database will involve here
     *
     * @param email set email to this content to be used for firebaseAuth
     * @param password set password to this content to be used for firebaseAuth
     * */
    private void firebaseAuthWithGoogle(String email, String password, final Context context) {


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser fbuser = mAuth.getCurrentUser();
                            if(fbuser.isEmailVerified() ) {
                                mAuth.getAccessToken(true);

                                // Sign in success, update UI with the signed-in user's information
                                if (checkBox.isChecked()) {
                                    saveData();
                                }

                                Log.d(TAG, "signInWithEmail:success");

                                //Getting current user details to pass on to the next activities
                                String UID = fbuser.getUid();
                                myRef = FirebaseDatabase.getInstance().getReference().child("users").child(UID);

                                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        String pwd = et_Password.getText().toString();
                                        String name = dataSnapshot.child("Name").getValue(String.class);
                                        String email = dataSnapshot.child("Email").getValue(String.class);

                                        //Storing user details into object to put into intent
                                        User user = new User(UID, name, pwd, email);
                                        userDatabase.insertUser(user);

                                        //Get All User Routine Data
                                        user.readHabitRepetition_Firebase(context);
                                        user.readHabitGroup_Firebase(context);
                                        user.getAllSectionAndTask();
                                        user.readHabit_Firebase(context, false);

                                        Intent intent = new Intent(LoginActivity.this, Home.class);
                                        intent.putExtra("user", user);
                                        startActivity(intent);
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }else{
                                txtError.setVisibility(View.VISIBLE);
                                txtError.setText("Email has not been verified");
                                showCustomVerification();
                                if (checkBox.isChecked()) {
                                    saveData();
                                }
                            }

                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            txtError.setText("Invalid Username and Password");

                            txtError.setVisibility(View.VISIBLE);
                            txtError.setText("Invalid Email or Password");

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                                et_Email.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                                et_Password.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            }

                            if (checkBox.isChecked()) {
                                saveData();
                            }
                        }

                    }
                });


    }

    /**
     *
     * Method used to check if there is an existing user session so that user does not need to
     * go through the login phase again
     *
     * */
    private void CheckLoggedIn() throws ParseException {
        mAuth = FirebaseAuth.getInstance();


        if ((mAuth.getCurrentUser() != null) && new UserDBHelper(this).getUser(FirebaseAuth.getInstance().getCurrentUser().getUid()) != null ) {
            User user;
            user = userDatabase.getUser(mAuth.getCurrentUser().getUid());
            Intent intent = new Intent(LoginActivity.this, Home.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        }
    }

    //This method is used to remember user details
    /**
     *
     * Method used to save data if the the rememberMe Check box is checked
     * */
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Username, et_Email.getText().toString());
        editor.putString(Password, et_Password.getText().toString());
        editor.putBoolean(SWITCH1, checkBox.isChecked());
        editor.apply();
        Log.v(TAG, "Data saved");
    }

    //This method is used to load the details that is stored
    /**
     *
     * Method used to load data if the the rememberMe Check box is checked
     *
     * */
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        sUser = sharedPreferences.getString(Username, "");
        sPassword = sharedPreferences.getString(Password, "");
        switchOnOff = sharedPreferences.getBoolean(SWITCH1, false);
    }

    //Update views if user clicks remember me
    /**
     *
     * Method used to update the editText if there is an existing email and password saved in sharedPreference
     * */
    public void updateViews() {
        et_Email.setText(sUser);
        et_Password.setText(sPassword);
        checkBox.setChecked(switchOnOff);
    }


    /**
     *
     * Method used to hide soft keyboard
     *
     * @param taskInput Passed into this context where keyboard will show the keyboard targeting the on the editText
     * */
    private void HideKeyboard(EditText taskInput) {
        Log.i(TAG, "Hide soft keyboard");
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert mgr != null;
        mgr.hideSoftInputFromWindow(taskInput.getWindowToken(), 0);
    }


    /**
     *
     * Method used to show soft keyboard
     *
     * @param taskInput Passed into this context where keyboard will hide from the keyboard
     * */
    private void ShowKeyboard(EditText taskInput) {
        Log.i(TAG, "Show soft keyboard");
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert mgr != null;
        mgr.showSoftInput(taskInput, InputMethodManager.SHOW_IMPLICIT);
    }

    @SuppressLint("ResourceAsColor")
    private void showCustomVerification() {

        Button mBtnOk, mBtnAgain;


        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.login_email_verfication_dialog, null, false);

        mBtnOk = dialogView.findViewById(R.id.btnOk);
        mBtnAgain = dialogView.findViewById(R.id.btnAgain);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        RegisterFirebaseUser registerFirebaseUser = new RegisterFirebaseUser(null,this, null, null);

        //When the add button is clicked
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick(): Add button is pressed ");

                alertDialog.cancel();
            }
        });

        mBtnAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerFirebaseUser.sendEmailVerification(FirebaseAuth.getInstance(), LoginActivity.this);
                alertDialog.cancel();

            }
        });

    }

    private void changePassword(String email)
    {
        //PasswordDialog passwordDialog = new PasswordDialog();
        //passwordDialog.show(getSupportFragmentManager(), "Change Password Dialog");
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            MakeToast("Password reset email sent!");

                            //logout();
                        }
                    }
                });
    }

    private void showCustomPasswordDialog(){

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.login_reset_password_dialog, null, false);

        Button mBtnsend = dialogView.findViewById(R.id.btnSend);
        EditText edEmail = dialogView.findViewById(R.id.email);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        RegisterFirebaseUser registerFirebaseUser = new RegisterFirebaseUser(null,this, null, null);

        //When the add button is clicked
        mBtnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick(): Add button is pressed ");
                changePassword(edEmail.getText().toString().trim());
                alertDialog.cancel();
            }
        });


    }

    public void MakeToast(String info)
    {
        Toast toast = Toast.makeText(LoginActivity.this, info, Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.GRAY);
        TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
        text.setTextColor(Color.WHITE);
        toast.show();
    }
}

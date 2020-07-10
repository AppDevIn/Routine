package com.mad.p03.np2020.routine.Register.models;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.os.AsyncTask;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
<<<<<<< HEAD:app/src/main/java/com/mad/p03/np2020/routine/Register/models/RegisterFirebaseUser.java
=======
import com.mad.p03.np2020.routine.Register.models.OnFirebaseAuth;
>>>>>>> master:app/src/main/java/com/mad/p03/np2020/routine/Register/models/RegisterFirebaseUser.java

import androidx.annotation.NonNull;

/**
 * Class to process the request to firebase
 * in the background
 * @author Jeyavishnu
 * @since 06-06-2020
 */
public class RegisterFirebaseUser extends AsyncTask<Void, Void, Void> {

    private OnFirebaseAuth mOnFirebaseAuth;
    @SuppressLint("StaticFieldLeak")
    private Activity mActivity;
    private String mEmail;
    private String mPassword;

    /**
     *
     * To create object give access to a its method, it will take
     * in a listener to inform ant changes in state of authentication.
     * It will set the email and password the give the object on the user password
     *
     *
     * @param onFirebaseAuth The listener this get triggered when successes or failed
     * @param activity The activity this was called from
     * @param email The email the user want to register this account with
     * @param password The password the user wants to set
     */
    public RegisterFirebaseUser(OnFirebaseAuth onFirebaseAuth, Activity activity, String email, String password) {
        mOnFirebaseAuth = onFirebaseAuth;
        mActivity = activity;
        mEmail = email;
        mPassword = password;
    }

    /**
     *
     * This will register the user in the background
     *
     * @param voids This parameter accepts vVid
     * @return it will return a void
     */
    @Override
    protected Void doInBackground(Void... voids) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() { // Check if the process is completed
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Check if is successful
                        if (task.isSuccessful()) {
                            mOnFirebaseAuth.OnSignUpSuccess();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() { //If firebase fail to create
            @Override
            public void onFailure(@NonNull Exception e) {
                mOnFirebaseAuth.OnSignUpFailure(e);
            }
        });
        return null;
    }
}

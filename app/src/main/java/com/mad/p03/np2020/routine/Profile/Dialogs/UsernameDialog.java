package com.mad.p03.np2020.routine.Profile.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.FormatException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.mad.p03.np2020.routine.DAL.UserDBHelper;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.models.User;

import static android.content.ContentValues.TAG;

public class UsernameDialog extends AppCompatDialogFragment {
    private EditText changedUsername;
    private UsernameDialogListener listener;
    private User mUser;
    private String currentName;

    /*
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //mUser = new UserDBHelper(getActivity()).getUser(getIntent().getStringExtra("user"));

    }
     */

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        mUser = new UserDBHelper(getActivity()).getUser(FirebaseAuth.getInstance().getUid());
        currentName = mUser.getName();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view  = inflater.inflate(R.layout.activity_profile_changename, null);

        builder.setView(view)
                .setTitle("Change Username")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String username = changedUsername.getText().toString();
                        try {
                            listener.getNewUsername(username);
                        } catch (FormatException e) {
                            Toast.makeText(getContext(), "Username field must not be empty", Toast.LENGTH_SHORT).show();
                            Log.v(TAG, "Username is empty");
                        }
                    }
                });

        changedUsername = view.findViewById(R.id.newUsername);

        return builder.create();
    }

    public interface UsernameDialogListener{
        void getNewUsername(String newUsername) throws FormatException;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (UsernameDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement new dialog listener");
        }
    }
}

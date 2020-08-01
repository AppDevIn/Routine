package com.mad.p03.np2020.routine.Profile.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.mad.p03.np2020.routine.R;
/**
 *
 * PasswordDialog Class for managing password dialog
 *
 * @author Pritheev
 * @since 02-06-2020
 *
 */
public class PasswordDialog extends AppCompatDialogFragment {
    private EditText currentPassword;
    private EditText newPassword;
    private EditText reNewPassword;
    private PasswordDialogListener listener;
    private Button positiveButton;


/*
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view  = inflater.inflate(R.layout.activity_profile_changepassword, null);

        builder.setView(view)
                .setTitle("Change Password")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Change", null);

        currentPassword = view.findViewById(R.id.currentPassword);
        newPassword = view.findViewById(R.id.newPassword);
        reNewPassword = view.findViewById(R.id.reNewPassword);


        return builder.create();
    }


 */
    @Override
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_profile_changepassword, null);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Change Password")
                .setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Change", null)
                .show();

        currentPassword = view.findViewById(R.id.currentPassword);
        newPassword = view.findViewById(R.id.newPassword);
        reNewPassword = view.findViewById(R.id.reNewPassword);

        positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "TEST", Toast.LENGTH_LONG).show();
            }
        });

        return dialog;
    }

    public interface PasswordDialogListener{
        void getNewPassword(String oldPassword, String newPassword, String reNewPassword);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (PasswordDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement new dialog listener");
        }
    }
}

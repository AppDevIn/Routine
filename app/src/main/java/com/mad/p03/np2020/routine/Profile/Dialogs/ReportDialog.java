package com.mad.p03.np2020.routine.Profile.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.nfc.FormatException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.p03.np2020.routine.DAL.UserDBHelper;
import com.mad.p03.np2020.routine.R;

import static android.content.ContentValues.TAG;
/**
 *
 * ReportDialog Class for managing ReportDialog
 *
 * @author Pritheev
 * @since 02-06-2020
 *
 */
public class ReportDialog extends AppCompatDialogFragment {

    EditText problem;
    String problemText;
    String UID;
    DatabaseReference mDatabase;
    DatabaseReference feedbackRef;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;


    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        //Building a alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view  = inflater.inflate(R.layout.activity_profile_feedback, null);

        builder.setView(view)
                .setTitle("Report a problem!")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Report", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        problemText = problem.getText().toString();

                        feedbackRef = mDatabase.child("feedback").child(UID).push();

                        feedbackRef.setValue(problemText);

                        //mDatabase.child("feedback").child(UID).setValue(problemText);

                        Toast toast = Toast.makeText(getContext(), "Thank you for your feedback!", Toast.LENGTH_LONG);
                        toast.getView().setBackgroundColor(Color.GRAY);
                        TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
                        text.setTextColor(Color.WHITE);
                        toast.show();
                    }
                });

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        UID = firebaseUser.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        problem = view.findViewById(R.id.problemEditText);

        return builder.create();
    }
}

package com.mad.p03.np2020.routine.Home.models;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mad.p03.np2020.routine.models.Section;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 *
 * This is to upload the data to Section data from
 * the background which implements the Worker.
 *
 * @author Jeyavishnu
 * @since 02-06-2020
 *
 */
public class UploadSectionWorker extends Worker {

    public UploadSectionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     *
     * To do background processing, to add the section data
     * in firebase using the section object and UID and SectionID as the key
     *
     * Data that need to be sent over are Name, ID, Image, Color, Position and UID.
     * You can use {@code OneTimeWorkRequest.setInputData(data)} to send and
     * {@code getInputData()} to retrieve it.
     *
     *
     * @return Result This is tell what happen to the background work
     */
    @NonNull
    @Override
    public Result doWork() {
        //Get the input and create the user object
        String name =  getInputData().getString(Section.COLUMN_NAME);
        String UID = getInputData().getString(Section.COLUMN_USERID) ;
        String id = getInputData().getString(Section.COLUMN_SECTION_ID) ;
        int image = getInputData().getInt(Section.COLUMN_IMAGE, 0) ;
        int color = getInputData().getInt(Section.COLUMN_COLOR, 0);
        int position = getInputData().getInt(Section.COLUMN_POSITION, 0);
        boolean update = getInputData().getBoolean("Update", false);



        //Getting a database reference to Users
        //users/{UID}/section/{id}/data
//        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(UID).child("section").child(String.valueOf(id));
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Section").child(String.valueOf(id));
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(UID).child("Section");
        DatabaseReference teamDatabase = FirebaseDatabase.getInstance().getReference().child("Section").child(String.valueOf(id)).child("Team");



        if(update) {

            //Change the name, icon and color
            mDatabase.child("name").setValue(name);
            mDatabase.child("iconValue").setValue(image);
            mDatabase.child("backgroundColor").setValue(color);





        }else{

            //Setting value using object
            mDatabase.setValue(new Section(name, color, image, id, position, UID));

            //Set the section id in user
            userDatabase.child(id).setValue(id);

            //Set the email for this section in Team
            teamDatabase.child(teamDatabase.push().getKey()).setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }



        Log.d("Register", "doInBackground(): Name, Email and DOB are uploaded");

        return Result.success();
    }
}

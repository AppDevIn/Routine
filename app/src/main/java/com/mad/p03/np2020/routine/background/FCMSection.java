package com.mad.p03.np2020.routine.background;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.Task;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.database.SectionDBHelper;
import com.mad.p03.np2020.routine.database.TaskDBHelper;

import java.util.Objects;

import androidx.annotation.NonNull;

public class FCMSection extends FirebaseMessagingService {

    final private String TAG = "FCMMessaging";
    @Override
    public void onNewToken(@NonNull String token) {
        sendRegistrationToServer(token);
    }

    /**
     *
     * Send to the user the messaging token and save it there
     * When the the messaging token is re-created
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.

        Log.d(TAG, "sendRegistrationToServer: sending token to server: " + token);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.child("messagingToken").setValue(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Log.d(TAG, "Date: " + remoteMessage.getData());



        switch (Objects.requireNonNull(remoteMessage.getData().get("for"))){
            case "SectionAdd": addSectionSQL(remoteMessage); break;
            case "SectionDelete": deleteSectionSQL(remoteMessage); ;break;
            case "TaskAdd": addTaskSQL(remoteMessage); ;break;
            case "TaskDelete": deleteTaskSQL(remoteMessage); ;break;

        }

    }

    private void addSectionSQL(RemoteMessage remoteMessage){
        Section section = Section.fromJSON(remoteMessage.getData().toString());
        Log.d(TAG, "onMessageReceived(): Section info: " + section.toString());

        String id = remoteMessage.getData().get("id");

        //Save to SQL
        //Check if the Data already exist
        SectionDBHelper sectionDBHelper = new SectionDBHelper(this);
        Log.d(TAG, "onMessageReceived: " + sectionDBHelper.hasID(id));
        if(!sectionDBHelper.hasID(id)){
            sectionDBHelper.insertSection(section, FirebaseAuth.getInstance().getCurrentUser().getUid());
            Log.d(TAG, "Added to new Firebase data to Section ");
        }
    }


    private void deleteSectionSQL(RemoteMessage remoteMessage){
        String id = remoteMessage.getData().get("id");

        SectionDBHelper sectionDBHelper = new SectionDBHelper(this);

        if(sectionDBHelper.hasID(id)){
            sectionDBHelper.delete(id);
            Log.d(TAG, "delete: " + id + " Has been deleted");
        }
    }


    private void addTaskSQL(RemoteMessage remoteMessage){
        Task task =  Task.fromJSON(remoteMessage.getData().toString());
        Log.d(TAG, "onMessageReceived(): Section info: " + task.toString());

        String id = remoteMessage.getData().get("id");
        String sectionID = remoteMessage.getData().get("sectionID");

        //Save to SQL
        //Check if the Data already exist
        TaskDBHelper taskDBHelper = new TaskDBHelper(this);
        Log.d(TAG, "onMessageReceived: " + taskDBHelper.hasID(id));
        if(!taskDBHelper.hasID(id)){
            taskDBHelper.insertTask(task, sectionID);
            Log.d(TAG, "Task from firebase added to SQL");
        }
    }


    private void deleteTaskSQL(RemoteMessage remoteMessage){
        String id = remoteMessage.getData().get("id");

        TaskDBHelper taskDBHelper = new TaskDBHelper(this);

        if(taskDBHelper.hasID(id)){
            taskDBHelper.delete(id);
            Log.d(TAG, "Task ID " + id + " , has been deleted");
        }
    }




}

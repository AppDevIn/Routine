package com.mad.p03.np2020.routine.background;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.Class.Task;
import com.mad.p03.np2020.routine.database.SectionDBHelper;
import com.mad.p03.np2020.routine.database.TaskDBHelper;

import java.util.Objects;

import androidx.annotation.NonNull;


/**
 *
 * This is a Firebase CLoud messaging listener program
 * which implements the FirebaseMessagingService.
 *
 * This program listens to any changes to database
 * and using cloud trigger to send data through
 * Firebase CLoud Messaging. The token is actually the messaging
 * token.
 *
 *
 * @author Jeyavishnu
 * @since 02-06-2020
 *
 */

public class FCM extends FirebaseMessagingService {

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


    /**
     * Called when a message is received.
     *
     * This is  called when a notification message is received
     * while the app is in the foreground. It can receive the
     * data sent just by using {@code remoteMessage.getData()}.
     * Depending on the reason (for) it will run different functions
     * the edit in the database
     *
     * @param remoteMessage Remote message that has been received.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Log.d(TAG, "Date: " + remoteMessage.getData());


        //Depending on the reason different functions run
        switch (Objects.requireNonNull(remoteMessage.getData().get("for"))){
            case "SectionAdd": addSectionSQL(remoteMessage); break;
            case "SectionDelete": deleteSectionSQL(remoteMessage); ;break;
            case "TaskAdd": addTaskSQL(remoteMessage); ;break;
            case "TaskDelete": deleteTaskSQL(remoteMessage); ;break;

        }

    }

    /**
     *
     * This method used create the section object, using the
     * object to create the a row in the section table. It also check
     * the data is already create to avoid duplicate
     *
     * @param remoteMessage Remote message that has been received.
     */

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


    /**
     *
     * This method is used to get the data from remote messages
     * and get the id from the data. The id is unique to each
     * section which will be used to delete using SectionDBHelper.
     * It also check is the data exist if it does that it will
     * delete.
     *
     * @param remoteMessage Remote message that has been received.
     */
    private void deleteSectionSQL(RemoteMessage remoteMessage){
        String id = remoteMessage.getData().get("id");

        SectionDBHelper sectionDBHelper = new SectionDBHelper(this);

        //If data exist than delete
        if(sectionDBHelper.hasID(id)){
            sectionDBHelper.delete(id);
            Log.d(TAG, "delete: " + id + " Has been deleted");
        }
    }

    /**
     *
     * This method is used to add task into the sql.
     * It created the task object after checking if the
     * data exist it will add it into the database
     *
     * @param remoteMessage Remote message that has been received.
     */
    private void addTaskSQL(RemoteMessage remoteMessage){
        Task task =  Task.fromJSON(remoteMessage.getData().toString());
        Log.d(TAG, "onMessageReceived(): Section info: " + task.toString());

        String id = remoteMessage.getData().get("id");
        String sectionID = remoteMessage.getData().get("sectionID");

        //Save to SQL
        //Check if the Data already exist
        TaskDBHelper taskDBHelper = new TaskDBHelper(this);
        Log.d(TAG, "onMessageReceived: " + taskDBHelper.hasID(id));

        //If the data doesn't exist than add
        if(!taskDBHelper.hasID(id)){
            taskDBHelper.insertTask(task, sectionID);
            Log.d(TAG, "Task from firebase added to SQL");
        }
    }

    /**
     *
     * This method is used to get the data from remote messages
     * and get the id from the data. The id is unique to each
     * task. I will check if it exist in the database if does it will delete it
     *
     * @param remoteMessage Remote message that has been received.
     */
    private void deleteTaskSQL(RemoteMessage remoteMessage){
        String id = remoteMessage.getData().get("id");

        TaskDBHelper taskDBHelper = new TaskDBHelper(this);

        //If the data exist it will delete
        if(taskDBHelper.hasID(id)){
            taskDBHelper.delete(id);
            Log.d(TAG, "Task ID " + id + " , has been deleted");
        }
    }




}

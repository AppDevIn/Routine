package com.mad.p03.np2020.routine.background;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mad.p03.np2020.routine.Class.Section;
import com.mad.p03.np2020.routine.R;
import com.mad.p03.np2020.routine.database.SectionDBHelper;

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


        Section section = Section.fromJSON(remoteMessage.getData().toString());
        Log.d(TAG, "onMessageReceived(): Section info: " + section.toString());

        //Save to SQL
        SectionDBHelper sectionDBHelper = new SectionDBHelper(this);
        sectionDBHelper.insertSection(section, "pXIeuenKaGWjEU5ruEQ6ahiS8FK2");
        Log.d(TAG, "Added to new Firebase data to Section ");


    }

}

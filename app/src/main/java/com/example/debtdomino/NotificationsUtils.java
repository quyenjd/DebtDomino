package com.example.debtdomino;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationsUtils extends FirebaseMessagingService {

    private static final String TAG = "NotificationsUtils";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle FCM messages here.
        // If the application is in the foreground handling of the message is here
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            //Handle the data message here
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            //Handle the notification message here
        }
    }
}

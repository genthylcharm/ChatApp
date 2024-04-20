package com.example.FireBase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {


    private static final String TAG = "MessagingService";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Xử lý khi có một Instance ID token mới được tạo
        Log.d("FCM", "Token" + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Xử lý tin nhan FCM tại đây
        Log.d("FCM", "Message: " + remoteMessage.getNotification().getBody());
    }


}

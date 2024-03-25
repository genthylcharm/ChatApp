package com.example.FireBase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {


    private static final String TAG = "MessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Xử lý tin nhắn FCM tại đây
        Log.d(TAG, "Received FCM message: " + remoteMessage.getData());
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Xử lý khi có một Instance ID token mới được tạo
        Log.d(TAG, "Received new token: " + token);
    }
}
